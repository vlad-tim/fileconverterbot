/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject

class TelegramGetFileResponse(pJsonObject: JSONObject) : TelegramResponse(pJsonObject) {

    val file: TelegramFile? = if (result != null) TelegramFile(result as JSONObject) else null
}