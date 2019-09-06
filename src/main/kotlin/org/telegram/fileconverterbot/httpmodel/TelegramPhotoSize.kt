/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

class TelegramPhotoSize(pJsonObject: JSONObject) : TelegramAttachment(pJsonObject) {
    val width: Int
    val height: Int

    init {
        width = jsonObject.getOrNull("width") as Int
        height = jsonObject.getOrNull("height") as Int
    }
}