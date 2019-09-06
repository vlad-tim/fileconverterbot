/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject

open class AbstractTelegramRequest() {

    protected val jsonObject: JSONObject = JSONObject()

    override fun toString(): String {
        return jsonObject.toString()
    }
}