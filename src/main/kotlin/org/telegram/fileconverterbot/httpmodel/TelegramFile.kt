/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

class TelegramFile(pJsonObject: JSONObject) : AbstractJsonObject(pJsonObject) {
    val fileId: String
    val fileSize: Int?
    val filePath: String?

    init {
        fileId = jsonObject.getOrNull("file_id") as String
        fileSize = jsonObject.getOrNull("file_size") as Int?
        filePath = jsonObject.getOrNull("file_path") as String?
    }
}