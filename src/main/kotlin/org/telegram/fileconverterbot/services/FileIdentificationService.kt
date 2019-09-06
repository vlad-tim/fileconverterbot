/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.services

import org.telegram.fileconverterbot.exceptions.FileIdentificationException
import org.telegram.fileconverterbot.model.FileFormat
import org.telegram.fileconverterbot.utils.Constants
import org.telegram.fileconverterbot.utils.ProcessUtils
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.regex.Pattern
import javax.ejb.Stateless

@Stateless
open class FileIdentificationService {

    /**
     * Stores this file, identifies file format
     * @param fileBytes file binary representation
     * @return null, if didn't succeed to identify, otherwise (format + temporary file Id)
     */
    @Throws(FileIdentificationException::class)
    open fun storeFile(fileBytes: ByteArray): Pair<FileFormat, String>? {
        val sourceName = UUID.randomUUID().toString()
        val sourceTempFile = File(Constants.TEMP_DIRECTORY, "$sourceName")
        val outputFile = File(Constants.TEMP_DIRECTORY, "${sourceName}.out")
        var fileFormat: FileFormat? = null
        try {
            sourceTempFile.writeBytes(fileBytes)
            fileFormat = identifyFileFormat(sourceName, outputFile)
                    ?: return null
            return Pair(fileFormat, sourceName)
        } catch (e: IOException) {
            throw FileIdentificationException("Unable to identify file format", e)
        } finally {
            // failed to identify file
            if (fileFormat == null && sourceTempFile.exists()) {
                sourceTempFile.delete()
            }
            if (outputFile.exists()) {
                outputFile.delete()
            }
        }
    }

    private fun identifyFileFormat(sourceName: String, outputFile: File): FileFormat? {
        var fileFormat: FileFormat? = identifyWithFfmpeg(sourceName, outputFile)

        if (fileFormat == null) {
            fileFormat = identifyWithImageMagic(sourceName, outputFile)
        }
        if (fileFormat == null) {
            fileFormat = identifyWithFile(sourceName, outputFile)
        }
        return fileFormat
    }

    private fun executeIdentificationProcess(identificationProcess: ProcessBuilder, outputFile: File): Pair<Int, String> {
        val exitCode = ProcessUtils.execProcess(identificationProcess, outputFile)
        return Pair(exitCode, outputFile.readText())
    }

    private fun identifyWithFfmpeg(sourceName: String, outputFile: File): FileFormat? {
        val identificationProcess = ProcessBuilder("ffprobe", "-show_format", sourceName)
        val processResult = executeIdentificationProcess(identificationProcess, outputFile)
        if (processResult.first != 0) {
            return null
        }
        val output = processResult.second
        val formatNamePattern = Pattern.compile(".*format_name=(\\S*).*", Pattern.DOTALL)
        val formatNameMatcher = formatNamePattern.matcher(output)
        val probeScore = parseProbeScore(output)
        if (formatNameMatcher.matches() && probeScore >= Constants.PROBE_SCORE_THRESHOLD) {
            val format = formatNameMatcher.group(1)
            return when (format) {
                "mp3" -> FileFormat.MP3
                "aac" -> FileFormat.AAC
                "amr" -> FileFormat.AMR
                "asf" -> if (!output.contains("Video:")) FileFormat.WMA else FileFormat.WMV
                "flac" -> FileFormat.FLAC
                "wav" -> FileFormat.WAV
                "ogg" -> if (output.contains("Audio: vorbis")) FileFormat.OGG
                else if (output.contains("Audio: opus")) FileFormat.OPUS
                else null
                "aiff" -> FileFormat.AIFF
                "avi" -> FileFormat.AVI
                "mov,mp4,m4a,3gp,3g2,mj2" -> if (output.contains("major_brand=qt")) FileFormat.MOV else FileFormat.MPEG4
                "mpeg" -> FileFormat.MPEG
                "flv" -> FileFormat.FLV
                "matroska,webm" -> FileFormat.MKV
                else -> null
            }
        }
        return null
    }

    private fun parseProbeScore(output: String): Int {
        val probeScorePattern = Pattern.compile(".*probe_score=(\\S*).*", Pattern.DOTALL)
        val probeScoreMatcher = probeScorePattern.matcher(output)
        if (probeScoreMatcher.matches()) {
            val scoreString = probeScoreMatcher.group(1)
            try {
                return scoreString.toInt()
            } catch (e: IllegalArgumentException) {
                return -1
            }
        }
        return -1
    }

    private fun identifyWithImageMagic(sourceName: String, outputFile: File): FileFormat? {
        val identificationProcess = ProcessBuilder("identify", "-format", "%m", "${sourceName}[0]")
        val processResult = executeIdentificationProcess(identificationProcess, outputFile)
        if (processResult.first != 0) {
            return null
        }
        val format = processResult.second
        return when (format) {
            "JPEG" -> FileFormat.JPEG
            "PNG" -> FileFormat.PNG
            "GIF" -> FileFormat.GIF
            "BMP", "BMP2", "BMP3" -> FileFormat.BMP
            "TIFF" -> FileFormat.TIFF
            else -> null
        }
    }

    private fun identifyWithFile(sourceName: String, outputFile: File): FileFormat? {
        val identificationProcess = ProcessBuilder("file", sourceName)
        val processResult = executeIdentificationProcess(identificationProcess, outputFile)
        if (processResult.first != 0) {
            return null
        }
        val data = processResult.second
        if (data.contains("Web/P image")) {
            return FileFormat.WEBP
        }
        return null
    }
}