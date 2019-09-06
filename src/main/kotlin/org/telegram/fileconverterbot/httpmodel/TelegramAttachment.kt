/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

open class TelegramAttachment(pJsonObject: JSONObject) : AbstractJsonObject(pJsonObject) {
    val fileId: String
    val fileSize: Int?

    init {
        fileId = jsonObject.getOrNull("file_id") as String
        fileSize = jsonObject.getOrNull("file_size") as Int?
    }
}