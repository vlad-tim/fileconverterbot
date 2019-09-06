/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONArray
import org.json.JSONObject
import org.telegram.fileconverterbot.model.MessageEntity
import org.telegram.fileconverterbot.model.MessageParseMode

class TelegramSendMessage(chatId: Any, text: String, inlineKeyboard: List<Pair<String, String>>? = null,
                          entities: List<MessageEntity>? = null, parseMode: MessageParseMode? = null)
: AbstractTelegramRequest() {

    init {
        jsonObject.put("chat_id", chatId)
        jsonObject.put("text", text)
        jsonObject.put("disable_web_page_preview", true)

        if (inlineKeyboard != null) {
            val replyMarkup = inlineKeyboard.map {
                pair ->
                arrayOf(object {
                    val text = pair.first
                    val callback_data = pair.second
                })
            }
            val replyMarkupWrapper = JSONObject()
            replyMarkupWrapper.put("inline_keyboard", replyMarkup)
            jsonObject.put("reply_markup", replyMarkupWrapper)
        }
        if (entities != null) {
            jsonObject.put("entities", JSONArray(entities))
        }
        if (parseMode != null) {
            jsonObject.put("parse_mode", parseMode.value)
        }
    }
}