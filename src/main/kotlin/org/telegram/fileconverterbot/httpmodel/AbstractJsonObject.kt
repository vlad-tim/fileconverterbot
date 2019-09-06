/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

abstract class AbstractJsonObject(jsonObject: JSONObject) {

    protected val jsonObject: JSONObject = jsonObject

    protected fun <T> getObjectOrNull(fieldName: String, factory: (str: String) -> T): T? {
        val objectString = jsonObject.getOrNull(fieldName)?.toString()
        return if (objectString != null) factory(objectString) else null
    }
}