/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

open class TelegramResponse(pJsonObject: JSONObject) : AbstractJsonObject(pJsonObject) {
    val ok: Boolean
    val description: String?
    val result: Any?

    init {
        ok = jsonObject.getOrNull("ok") as Boolean
        description = jsonObject.getOrNull("description") as String?
        result = jsonObject.getOrNull("result")
    }
}