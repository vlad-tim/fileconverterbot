/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.api.updates

import com.github.kittinunf.fuel.core.FuelError
import org.jboss.logging.Logger
import org.json.JSONObject
import org.telegram.fileconverterbot.api.Endpoints
import org.telegram.fileconverterbot.api.telegram.TelegramApi
import org.telegram.fileconverterbot.httpmodel.*
import org.telegram.fileconverterbot.localization.Localization
import org.telegram.fileconverterbot.model.FileType
import org.telegram.fileconverterbot.model.MessageEntity
import org.telegram.fileconverterbot.model.MessageParseMode
import org.telegram.fileconverterbot.services.EmailService
import org.telegram.fileconverterbot.services.FileConverterDB
import org.telegram.fileconverterbot.services.FileConverterService
import org.telegram.fileconverterbot.services.FileIdentificationService
import org.telegram.fileconverterbot.utils.Constants
import org.telegram.fileconverterbot.utils.LogUtils
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/${Endpoints.UPDATES}")
open class UpdateApi {

    companion object {
        const val COMMAND_PREFIX = "/"
        const val START_COMMAND = "start"
        const val HELP_COMMAND = "help"
        const val PROMO_COMMAND = "promo"
        const val TERMS_COMMAND = "terms"
        const val CHANGELOG_COMMAND = "changelog"
        const val FEEDBACK_COMMAND = "feedback"
        const val DONATE_COMMAND = "donate"
        const val STATS_COMMAND = "stats"
        const val DATA_DELIMITER = '|'
        const val EXTRACT_AUDIO = "TO_AUDIO"


        val COMMAND_RESPONSES = mapOf(
                Pair(START_COMMAND, Localization.START_COMMAND_RESPONSE),
                Pair(HELP_COMMAND, Localization.HELP_COMMAND_RESPONSE),
                Pair(PROMO_COMMAND, Localization.PROMO_COMMAND_RESPONSE),
                Pair(TERMS_COMMAND, Localization.TERMS_COMMAND_RESPONSE),
                Pair(CHANGELOG_COMMAND, Localization.CHANGELOG_COMMAND_RESPONSE),
                Pair(FEEDBACK_COMMAND, Localization.FEEDBACK_COMMAND_RESPONSE),
                Pair(DONATE_COMMAND, Localization.DONATION_COMMAND_RESPONSE)
        )
    }

    private val logger = Logger.getLogger(this.javaClass)

    @Inject
    lateinit private var fileIdentificationService: FileIdentificationService

    @Inject
    lateinit private var fileConverterService: FileConverterService

    @Inject
    lateinit private var fileConverterDB: FileConverterDB

    @Inject
    lateinit private var emailService: EmailService

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    open fun handleUpdate(update: String): Response? {
        val updateJson = JSONObject(update)
        logger.info("\n*****UPDATE*****\n${updateJson.toString(2)}\n**********")
        fileConverterDB.incrementTotalRequests()
        val telegramUpdate = TelegramUpdate(updateJson)
        try {
            if (telegramUpdate.callbackQuery != null) {
                val callbackQuery = telegramUpdate.callbackQuery
                val callbackQueryData = callbackQuery.data
                if (callbackQueryData != null) {
                    val dataFragments = callbackQueryData.split(DATA_DELIMITER)
                    if (dataFragments.size != 2) {
                        logger.error("Callback data is not correct: ${callbackQueryData}")
                        TelegramApi.sendTextMessage(callbackQuery.from.id, Localization.INVALID_CALLBACK_DATA)
                    } else {
                        val fileId = dataFragments.first()
                        val formatString = dataFragments.last()
                        if (formatString == EXTRACT_AUDIO) {
                            sendAudioExtractionOptions(fileId, callbackQuery.from.id)
                        } else {
                            fileConverterService.processConvertRequestAsync(
                                    callbackQuery.id, fileId, formatString, callbackQuery.from.id)
                        }
                    }
                }
            } else if (telegramUpdate.message != null) {
                val message = telegramUpdate.message
                val fromId = message.from.id
                if (message.text != null) {
                    replyToMessage(fromId, message.text)
                } else {
                    replyToFile(message)
                }
            }
        } catch(e: FuelError) {
            LogUtils.logFuelError(e, logger)
        }
        return buildResponse()
    }

    private fun buildResponse(): Response {
        return Response.ok().build();
    }

    private fun replyToMessage(messageFromId: Int, messageText: String) {
        val reply = getTextMessageResponse(messageText, messageFromId)
        val parseMode = if (!messageText.startsWith(COMMAND_PREFIX)) null else MessageParseMode.HTML
        if (messageText == "${COMMAND_PREFIX}${START_COMMAND}") {
            substituteHelp(messageFromId, reply)
        } else {
            TelegramApi.sendTextMessage(messageFromId, reply, parseMode = parseMode)
        }
    }

    private fun getTextMessageResponse(messageText: String, messageFromId: Int): String {
        if (messageText.startsWith(COMMAND_PREFIX)) {
            return processCommand(messageText.substring(1), messageFromId)
        } else {
            return Localization.TEXT_RESPONSE
        }
    }

    private fun processCommand(command: String, messageFromId: Int): String {
        if (command.startsWith(FEEDBACK_COMMAND)) {
            val commandTrimmed = command.trim()
            val feedbackWithMessagePrefix = "$FEEDBACK_COMMAND "
            if (commandTrimmed.startsWith(feedbackWithMessagePrefix)) {
                emailService.sendEmail("${Localization.FEEDBACK_SUBJECT} from ${messageFromId}", commandTrimmed)
                return Localization.FEEDBACK_SENT_RESPONSE
            }
        } else if (command == STATS_COMMAND) {
            return "Total requests: ${fileConverterDB.getTotalRequests()}.\r\n" +
                    "Total files converted: ${fileConverterDB.getFilesConverted()}."
        } else if (command == "balance") {
            val balance = fileConverterDB.getBtcBalance() ?: "Unknown"
            return "mBTC balance: $balance"
        }
        return COMMAND_RESPONSES[command] ?: Localization.UNKNOWN_COMMAND_RESPONSE
    }

    private fun replyToFile(message: TelegramMessage) {
        val fromId = message.from.id
        if (message.document != null) {
            processAttachment(message.document, fromId, message.document.fileName)
        } else if (message.audio != null) {
            processAttachment(message.audio, fromId, message.audio.name())
        } else if (message.video != null) {
            processAttachment(message.video, fromId)
        } else if (message.voice != null) {
            processAttachment(message.voice, fromId)
        } else if (message.videoNote != null) {
            processAttachment(message.videoNote, fromId)
        } else if (message.sticker != null) {
            processAttachment(message.sticker, fromId)
        } else if (message.photo != null) {
            val photos = message.photo
            val largestPhoto = photos.maxBy { photo -> photo.width * photo.height } as TelegramPhotoSize
            processAttachment(largestPhoto, fromId)
        } else {
            TelegramApi.sendTextMessage(fromId, Localization.ATTACHMENT_NOT_SUPPORTED)
        }
    }

    private fun processAttachment(attachment: TelegramAttachment, recipientId: Int, fileName: String? = null) {
        val fileSize = attachment.fileSize
        if (fileSize != null && fileSize > Constants.MAX_INCOMING_FILE_SIZE_MB * Constants.BYTES_IN_MEGABYTE) {
            return TelegramApi.sendTextMessage(recipientId,
                    String.format(Localization.INCOMING_FILE_TOO_BIG, Constants.MAX_INCOMING_FILE_SIZE_MB))
        }
        val response = TelegramApi.getFile(TelegramGetFile(attachment.fileId))
        if (!response.ok) {
            return logger.error("Unable to get file. ${response.description}")
        }
        val filePath = (response.file as TelegramFile).filePath
        sendFileInformation(filePath as String, fileName, recipientId)
    }

    private fun substituteHelp(recipientId: Int, text: String) {
        val command = "/${HELP_COMMAND}"
        val offset = text.indexOf("/help")
        TelegramApi.sendTextMessage(recipientId, text, entities = listOf(MessageEntity("bot_command", offset, command.length)))
    }

    private fun sendFileInformation(filePath: String, fileName: String?, recipientId: Int) {
        val file = TelegramApi.downloadFile(filePath)
        val fileInfo = fileIdentificationService.storeFile(file)
                ?: return substituteHelp(recipientId, Localization.FILE_FORMAT_NOT_SUPPORTED)
        val format = fileInfo.first
        val fileId = fileInfo.second
        val formatsToConvertTo = fileConverterService.getFileFormatsForConversion(format)
        var formatsToConvertToButtons = formatsToConvertTo.map {
            format ->
            Pair(format.buttonDescription, "${fileId}${DATA_DELIMITER}${format.extension}")
        }
        if (format.fileType == FileType.VIDEO) {
            formatsToConvertToButtons = formatsToConvertToButtons.plus(Pair(Localization.EXTRACT_AUDIO,
                    "${fileId}${DATA_DELIMITER}${EXTRACT_AUDIO}"))
        }
        if (fileName != null) {
            fileConverterDB.storeFileNameMapping(fileId, fileName)
        }
        TelegramApi.sendTextMessage(recipientId, String.format(Localization.FILE_CONVERSION_INFO, format.identifiedDescription),
                formatsToConvertToButtons)
    }

    private fun sendAudioExtractionOptions(fileId: String, fromId: Int) {
        val audioFormats = fileConverterService.getAudioFormats()
        val audioFormatsButtons = audioFormats.map {
            format ->
            Pair(format.buttonDescription, "${fileId}${DATA_DELIMITER}${format.extension}")
        }
        TelegramApi.sendTextMessage(fromId, Localization.SUPPORTED_AUDIO_FORMATS, audioFormatsButtons)
    }
}
