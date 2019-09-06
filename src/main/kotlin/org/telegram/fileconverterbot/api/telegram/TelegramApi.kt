/**
 * Copyright (c) 2016 Timofeev Vlad
 */

package org.telegram.fileconverterbot.api.telegram

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import org.jboss.logging.Logger
import org.jboss.resteasy.util.HttpHeaderNames
import org.json.JSONObject
import org.telegram.fileconverterbot.httpmodel.TelegramGetFile
import org.telegram.fileconverterbot.httpmodel.TelegramGetFileResponse
import org.telegram.fileconverterbot.httpmodel.TelegramResponse
import org.telegram.fileconverterbot.httpmodel.TelegramSendMessage
import org.telegram.fileconverterbot.model.MessageEntity
import org.telegram.fileconverterbot.model.MessageParseMode
import org.telegram.fileconverterbot.responseJson
import org.telegram.fileconverterbot.utils.Constants
import org.telegram.fileconverterbot.utils.FormBuilder
import javax.ws.rs.core.MediaType


object TelegramApi {

    private val logger = Logger.getLogger(this.javaClass)

    fun sendTextMessage(recipientId: Int, reply: String, inlineKeyboard: List<Pair<String, String>>? = null,
                        entities: List<MessageEntity>? = null, parseMode: MessageParseMode? = null) {
        val message = TelegramSendMessage(recipientId, reply, inlineKeyboard, entities, parseMode)
        val responseObject = sendMessage(message)
        if (!responseObject.ok) {
            return logger.error("Unable to send message. ${responseObject.description}")
        }
    }

    fun sendMessage(message: TelegramSendMessage): TelegramResponse {
        val responseJson = "${Constants.TELEGRAM_BASE_API_URL}/sendMessage"
                .httpPost()
                .header(Pair(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .body(message.toString())
                .responseJson()
        return TelegramResponse(responseJson)
    }

    fun editMessage(chatId: Any, messageId: Int, text: String): TelegramResponse {
        val editedMessage = object {
            val chat_id = chatId
            val message_id = messageId
            val text = text
        }
        val editedMessageJson = JSONObject(editedMessage)
        val responseJson = "${Constants.TELEGRAM_BASE_API_URL}/editMessageText"
                .httpPost()
                .header(Pair(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .body(editedMessageJson.toString())
                .responseJson()
        return TelegramResponse(responseJson)
    }

    fun getFile(getFileObject: TelegramGetFile): TelegramGetFileResponse {
        val responseJson = "${Constants.TELEGRAM_BASE_API_URL}/getFile"
                .httpPost()
                .header(Pair(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .body(getFileObject.toString())
                .responseJson()
        return TelegramGetFileResponse(responseJson)
    }

    fun downloadFile(filePath: String): ByteArray {
        val url = "${Constants.TELEGRAM_FILE_DOWNLOAD_URL}/${filePath}"
        return url
                .httpGet()
                .response()
                .third
                .get()
    }

    fun sendDocument(chatId: Int, file: ByteArray, fileName: String): TelegramResponse {
        val formBuilder = FormBuilder()
        formBuilder.addText("chat_id", chatId.toString())
        formBuilder.addFile("document", file, fileName)

        val response = "${Constants.TELEGRAM_BASE_API_URL}/sendDocument"
                .httpPost()
                .header(Pair(HttpHeaderNames.CONTENT_TYPE, "${MediaType.MULTIPART_FORM_DATA}; " +
                        "boundary=${formBuilder.boundary}"))
                .body(formBuilder.toByteArray())
                .responseJson()
        return TelegramResponse(response)
    }

    fun answerCallbackQuery(callbackQueryId: String, text: String): TelegramResponse {
        val answer = object {
            val callback_query_id = callbackQueryId
            val text = text
        }
        val answerJson = JSONObject(answer)
        val responseJson = "${Constants.TELEGRAM_BASE_API_URL}/answerCallbackQuery"
                .httpPost()
                .header(Pair(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .body(answerJson.toString())
                .responseJson()
        return TelegramResponse(responseJson)
    }
}