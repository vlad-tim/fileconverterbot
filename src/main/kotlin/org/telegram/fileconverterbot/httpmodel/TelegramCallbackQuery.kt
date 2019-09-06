/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

class TelegramCallbackQuery(pJsonObject: JSONObject) : AbstractJsonObject(pJsonObject) {
    val id: String
    val from: TelegramUser
    val data: String?

    init {
        id = jsonObject.getOrNull("id") as String
        from = TelegramUser(jsonObject["from"] as JSONObject)
        data = jsonObject.getOrNull("data") as String?
    }
}