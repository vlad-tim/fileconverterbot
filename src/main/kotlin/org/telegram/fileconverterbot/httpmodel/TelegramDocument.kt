/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

class TelegramDocument(pJsonObject: JSONObject) : TelegramAttachment(pJsonObject) {
    val fileName: String?

    init {
        fileName = jsonObject.getOrNull("file_name") as String?
    }
}