/**
 * Copyright (c) 2016 Timofeev Vlad
 */

package org.telegram.fileconverterbot

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import org.json.JSONException
import org.json.JSONObject

fun JSONObject.getOrNull(key: String): Any? {
    try {
        return this[key]
    } catch (e: JSONException) {
        return null;
    }
}

@Throws(FuelError::class)
fun Request.responseJson(): JSONObject {
    val response = this.responseString()
    val responseString = response.third.get()
    try {
        return JSONObject(responseString)
    } catch(e: JSONException) {
        val fuelError = FuelError()
        fuelError.exception = e
        fuelError.errorData = "Unable to construct JSON from response body ${responseString}".toByteArray()
        fuelError.response = response.second
        throw fuelError
    }
}

