/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

class TelegramUpdate(pJsonObject: JSONObject) : AbstractJsonObject(pJsonObject) {
    val updateId: Int
    val message: TelegramMessage?
    val callbackQuery: TelegramCallbackQuery?

    init {
        updateId = jsonObject.getOrNull("update_id") as Int
        message = getObjectOrNull("message", { str -> TelegramMessage(JSONObject(str)) })
        callbackQuery = getObjectOrNull("callback_query", { str -> TelegramCallbackQuery(JSONObject(str)) })
    }
}