/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

class TelegramUser(pJsonObject: JSONObject) : AbstractJsonObject(pJsonObject) {
    val id: Int
    val firstName: String
    val lastName: String?
    val userName: String?

    init {
        id = jsonObject.getOrNull("id") as Int
        firstName = jsonObject.getOrNull("first_name") as String
        lastName = jsonObject.getOrNull("last_name") as String?
        userName = jsonObject.getOrNull("username") as String?
    }
}