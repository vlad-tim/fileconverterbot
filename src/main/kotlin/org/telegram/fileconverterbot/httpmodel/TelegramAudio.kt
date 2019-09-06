/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

class TelegramAudio(pJsonObject: JSONObject) : TelegramAttachment(pJsonObject) {
    val title: String?
    val performer: String?

    init {
        title = jsonObject.getOrNull("title") as String?
        performer = jsonObject.getOrNull("performer") as String?
    }

    fun name(): String? {
        if (title != null && performer != null) {
            return "$performer - $title"
        } else if (title != null) {
            return title
        } else if (performer != null) {
            return "Audio by $performer"
        }
        return null
    }
}