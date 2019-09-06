/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.httpmodel

import org.json.JSONArray
import org.json.JSONObject
import org.telegram.fileconverterbot.getOrNull

class TelegramMessage(pJsonObject: JSONObject) : AbstractJsonObject(pJsonObject) {
    val messageId: Int
    val from: TelegramUser
    val text: String?
    val document: TelegramDocument?
    val audio: TelegramAudio?
    val video: TelegramVideo?
    val voice: TelegramVoice?
    val videoNote: TelegramVideoNote?
    val photo: Array<TelegramPhotoSize>?
    val sticker: TelegramSticker?

    init {
        messageId = jsonObject.getOrNull("message_id") as Int
        from = TelegramUser(jsonObject["from"] as JSONObject)
        text = jsonObject.getOrNull("text") as String?

        document = getObjectOrNull("document", { str -> TelegramDocument(JSONObject(str)) })
        audio = getObjectOrNull("audio", { str -> TelegramAudio(JSONObject(str)) })
        video = getObjectOrNull("video", { str -> TelegramVideo(JSONObject(str)) })
        voice = getObjectOrNull("voice", { str -> TelegramVoice(JSONObject(str)) })
        videoNote = getObjectOrNull("video_note", { str -> TelegramVideoNote(JSONObject(str)) })
        sticker = getObjectOrNull("sticker", { str -> TelegramSticker(JSONObject(str)) })
        val photoArray = getObjectOrNull("photo", ::JSONArray)
        photo = photoArray?.map { it -> TelegramPhotoSize(it as JSONObject) }?.toTypedArray()
    }
}