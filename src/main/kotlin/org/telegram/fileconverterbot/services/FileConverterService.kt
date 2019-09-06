/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.services

import org.jboss.logging.Logger
import org.json.JSONObject
import org.telegram.fileconverterbot.api.telegram.TelegramApi
import org.telegram.fileconverterbot.api.telegram.TelegramApi.sendTextMessage
import org.telegram.fileconverterbot.exceptions.ConcurrentConversionException
import org.telegram.fileconverterbot.exceptions.FileConversionException
import org.telegram.fileconverterbot.httpmodel.TelegramMessage
import org.telegram.fileconverterbot.httpmodel.TelegramResponse
import org.telegram.fileconverterbot.httpmodel.TelegramSendMessage
import org.telegram.fileconverterbot.localization.Localization
import org.telegram.fileconverterbot.model.FileFormat
import org.telegram.fileconverterbot.model.FileType
import org.telegram.fileconverterbot.utils.Constants
import org.telegram.fileconverterbot.utils.ProcessUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.UUID
import java.util.regex.Pattern
import javax.ejb.Asynchronous
import javax.ejb.Stateless
import javax.inject.Inject

@Stateless
open class ConvertingProgressService {

    companion object {
        const val UNKNOWN_VALUE = "N/A"
        const val MAX_PROGRESS_UPDATES = 120
        const val PROGRESS_UPDATE_INTERVAL_MILLISECONDS = 1000L
    }

    private fun matchPattern(output: String, patternString: String): String? {
        val pattern = Pattern.compile(patternString, Pattern.DOTALL)
        val matcher = pattern.matcher(output)
        if (matcher.matches()) {
            return matcher.group(1)
        }
        return null
    }

    private fun extractTime(output: String): String? {
        return matchPattern(output, ".*time=(\\S*).*")
    }

    private fun extractDuration(output: String): String? {
        return matchPattern(output, ".*Duration:\\s(\\S*),.*")
    }

    private fun calculateSeconds(time: String): Int {
        val pattern = Pattern.compile("(\\d*):(\\d*):(\\d*).(\\d*)")
        val matcher = pattern.matcher(time)
        if (matcher.matches()) {
            val hours = matcher.group(1).toInt()
            val minutes = matcher.group(2).toInt()
            val seconds = matcher.group(3).toInt()
            return hours * 3600 + minutes * 60 + seconds
        }
        return -1
    }

    private fun calculateProgress(time: String, duration: String): Double {
        if (time == UNKNOWN_VALUE || duration == UNKNOWN_VALUE) {
            return Double.NaN
        }
        val secondsConverted = calculateSeconds(time)
        val secondsTotal = calculateSeconds(duration)
        if (secondsConverted < 0 || secondsTotal < 0) {
            return Double.NaN
        }
        return (secondsConverted * 1.0 / secondsTotal).coerceIn(0.0, 1.0)
    }

    private fun formatProgressMessage(progress: Double): String {
        val blackBox = "" + '\u25AA'
        val whiteBox = "" + '\u25AB'
        val progressLong = Math.round(progress * 100)
        val blackBoxNumber = progressLong / 10
        val whiteBoxNumber = 10 - blackBoxNumber
        return String.format("${blackBox.repeat(blackBoxNumber.toInt())}${whiteBox.repeat(whiteBoxNumber.toInt())}(%02d%%)", progressLong)
    }

    @Asynchronous
    open fun monitorFFMPEGProgress(processOutputFile: File, recipientId: Int) {
        var messageId: Int? = null
        for (i in 0..MAX_PROGRESS_UPDATES) {
            if (processOutputFile.exists()) {
                val output = String(processOutputFile.readBytes())
                val time = extractTime(output) ?: UNKNOWN_VALUE
                val duration = extractDuration(output) ?: UNKNOWN_VALUE
                val progress = calculateProgress(time, duration)
                if (progress != Double.NaN) {
                    val progressText = formatProgressMessage(progress)
                    var response: TelegramResponse?
                    if (messageId == null) {
                        val message = TelegramSendMessage(recipientId, progressText)
                        response = TelegramApi.sendMessage(message)
                        if (response.ok) {
                            val sentMessage = TelegramMessage(response.result as JSONObject)
                            messageId = sentMessage.messageId
                        }
                    } else {
                        TelegramApi.editMessage(recipientId, messageId, progressText)
                    }
                }
            } else if (messageId != null) {
                TelegramApi.editMessage(recipientId, messageId, formatProgressMessage(1.0))
                break
            }
            Thread.sleep(PROGRESS_UPDATE_INTERVAL_MILLISECONDS)
        }
    }
}

@Stateless
open class FileConverterService {

    @Inject
    private lateinit var convertingProgressService: ConvertingProgressService

    @Inject
    lateinit private var fileConverterDB: FileConverterDB

    private val logger = Logger.getLogger(this.javaClass)

    open fun getFileFormatsForConversion(fileFormat: FileFormat): Set<FileFormat> {
        return FileFormat
                .values()
                .filter { format -> format.fileType == fileFormat.fileType && format != fileFormat }
                .toSet()
    }

    open fun getAudioFormats(): Set<FileFormat> {
        return FileFormat
                .values()
                .filter { format -> format.fileType == FileType.AUDIO }
                .toSet()
    }

    @Throws(FileNotFoundException::class)
    open fun ensureFileExists(fileName: String) {
        val sourceFile = File(Constants.TEMP_DIRECTORY, fileName)
        if (!sourceFile.exists()) {
            throw FileNotFoundException()
        }
    }

    @Throws(FileConversionException::class)
    open fun convertFile(sourceFileName: String, format: FileFormat, recipientId: Int): ByteArray {
        val extension = format.extension.toLowerCase()
        val convertedFileName = "$sourceFileName.${extension}"
        val convertedFile = File(Constants.TEMP_DIRECTORY, convertedFileName)
        val processOutputFileName = "$sourceFileName.output.${UUID.randomUUID()}"
        val processOutputFile = File(Constants.TEMP_DIRECTORY, processOutputFileName)
        try {
            val conversionProcess: ProcessBuilder?
            if (listOf(FileType.AUDIO, FileType.VIDEO).contains(format.fileType)) {
                convertingProgressService.monitorFFMPEGProgress(processOutputFile, recipientId)
                if (listOf(FileFormat.MPEG, FileFormat.AVI).contains(format)) {
                    conversionProcess = ProcessBuilder("ffmpeg", "-i", sourceFileName, "-q:v", "8", convertedFileName)
                } else if (listOf(FileFormat.WMV).contains(format)) {
                    conversionProcess = ProcessBuilder("ffmpeg", "-i", sourceFileName, "-q:v", "8", "-ac", "2", convertedFileName)
                } else if (listOf(FileFormat.FLV).contains(format)) {
                    conversionProcess = ProcessBuilder("ffmpeg", "-i", sourceFileName, "-q:v", "8",
                            "-ar", "44100", convertedFileName)
                } else if (listOf(FileFormat.MOV, FileFormat.MPEG4, FileFormat.MKV).contains(format)) {
                    conversionProcess = ProcessBuilder("ffmpeg", "-i", sourceFileName, "-c:a", "libfdk_aac",
                            "-preset", "veryfast", convertedFileName)
                } else if (listOf(FileFormat.AAC).contains(format)) {
                    conversionProcess = ProcessBuilder("ffmpeg", "-i", sourceFileName, "-c:a", "libfdk_aac", "-vn",
                            convertedFileName)
                } else if (listOf(FileFormat.AMR).contains(format)) {
                    conversionProcess = ProcessBuilder("ffmpeg", "-i", sourceFileName, "-ar", "8000", "-ac", "1", "-vn",
                            convertedFileName)
                } else if (format.fileType == FileType.AUDIO) {
                    conversionProcess = ProcessBuilder("ffmpeg", "-i", sourceFileName, "-vn", convertedFileName)
                } else {
                    conversionProcess = ProcessBuilder("ffmpeg", "-i", sourceFileName, convertedFileName)
                }
            } else {
                conversionProcess =
                        if (format != FileFormat.JPEG)
                            ProcessBuilder("convert", "${sourceFileName}[0]", "${extension}:${convertedFileName}")
                        else
                            ProcessBuilder("convert", "${sourceFileName}[0]", "-background", "white",
                                    "-flatten", "${extension}:${convertedFileName}")
            }
            val exitCode = ProcessUtils.execProcess(conversionProcess, processOutputFile)
            if (exitCode != 0) {
                throw FileConversionException("Unable to convert file. Exit code: ${exitCode}. " +
                        "Output:\n>>>>>\n${String(processOutputFile.readBytes())}\n>>>>>\n")
            }
            fileConverterDB.incrementFilesConverted()
            return convertedFile.readBytes()
        } catch (e: IOException) {
            throw FileConversionException("Unable to convert file", e)
        } finally {
            // source file will be deleted by FileCleaner
            if (convertedFile.exists()) {
                convertedFile.delete()
            }
            if (processOutputFile.exists()) {
                processOutputFile.delete()
            }
        }
    }

    @Asynchronous
    open fun processConvertRequestAsync(callbackQueryId: String, fileId: String, formatString: String, fromId: Int) {
        try {
            val formatEnum = FileFormat
                    .values()
                    .find { format -> format.extension == formatString }
                    ?: throw IllegalStateException()
            val callbackAnswer = String.format(Localization.CONVERTING_NOTIFICATION, formatEnum.buttonDescription)
            var conversionStarted = false
            try {
                ensureFileExists(fileId)
                fileConverterDB.markUserAsPerformingConversion(fromId)
                conversionStarted = true
                TelegramApi.answerCallbackQuery(callbackQueryId, callbackAnswer)
                val convertedFileBytes = convertFile(fileId, formatEnum, fromId)
                val convertedFileSize = convertedFileBytes.size
                if (convertedFileSize > Constants.MAX_OUTCOMING_FILE_SIZE_MB * Constants.BYTES_IN_MEGABYTE) {
                    val convertedFileSizeMB = Math.ceil(convertedFileSize.toDouble() / Constants.BYTES_IN_MEGABYTE).toInt()
                    sendTextMessage(fromId, String.format(Localization.OUTCOMING_FILE_TOO_BIG,
                            convertedFileSizeMB, Constants.MAX_OUTCOMING_FILE_SIZE_MB))
                } else {
                    val fileName = fileConverterDB.getFileNameMapping(fileId) ?: "file"
                    TelegramApi.sendDocument(fromId, convertedFileBytes, "${fileName}.${formatString.toLowerCase()}")
                }

            } catch (e: FileConversionException) {
                logger.error("Unable to convert file", e)
                sendTextMessage(fromId, Localization.UNABLE_TO_CONVERT)
            } catch (e: FileNotFoundException) {
                logger.error("File not found", e)
                sendTextMessage(fromId, Localization.FILE_NOT_FOUND)
            } catch (e: ConcurrentConversionException) {
                sendTextMessage(fromId, Localization.CONCURRENT_CONVERSION)
            } finally {
                if (conversionStarted) {
                    fileConverterDB.unmarkUserAsPerformingConversion(fromId)
                }
            }
        } catch (e: IllegalStateException) {
            logger.error("Callback data is not correct. Unknown format: ${formatString}")
            sendTextMessage(fromId, Localization.INVALID_CALLBACK_DATA)
        }
    }
}