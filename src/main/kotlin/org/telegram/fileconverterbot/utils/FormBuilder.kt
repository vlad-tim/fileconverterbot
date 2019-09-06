/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.utils

import org.apache.http.Consts
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.UUID

class FormBuilder {

    val boundary: String
    private val formBuilder: MultipartEntityBuilder

    private val octetStreamContentType = ContentType.create("application/octet-stream", Consts.UTF_8)

    init {
        boundary = UUID.randomUUID().toString()

        formBuilder = MultipartEntityBuilder.create()
        formBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
        formBuilder.setBoundary(boundary)
        formBuilder.setCharset(Charset.forName("UTF-8"))
    }

    fun addText(name: String, value: String) {
        formBuilder.addTextBody(name, value)
    }

    fun addFile(name: String, input: InputStream, fileName: String) {
        formBuilder.addBinaryBody(name, input, octetStreamContentType, fileName)
    }

    fun addFile(name: String, bytes: ByteArray, fileName: String) {
        formBuilder.addBinaryBody(name, bytes, octetStreamContentType, fileName)
    }

    fun toByteArray(): ByteArray {
        val form = formBuilder.build()
        val output = ByteArrayOutputStream()
        form.writeTo(output)
        return output.toByteArray()
    }
}