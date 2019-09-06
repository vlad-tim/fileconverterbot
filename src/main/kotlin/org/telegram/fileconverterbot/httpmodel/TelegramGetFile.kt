/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

class TelegramGetFile(fileId: String) : AbstractTelegramRequest() {

    init {
        jsonObject.put("file_id", fileId)
    }
}