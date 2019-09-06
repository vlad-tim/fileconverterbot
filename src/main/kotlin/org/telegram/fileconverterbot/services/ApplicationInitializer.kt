/**
 * Copyright (c) 2016 Timofeev Vlad
 */

package org.telegram.fileconverterbot.services

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.interceptors.validatorResponseInterceptor
import com.github.kittinunf.fuel.httpPost
import org.jboss.logging.Logger
import org.jboss.resteasy.util.HttpHeaderNames
import org.telegram.fileconverterbot.api.Endpoints
import org.telegram.fileconverterbot.httpmodel.TelegramResponse
import org.telegram.fileconverterbot.responseJson
import org.telegram.fileconverterbot.utils.Constants
import org.telegram.fileconverterbot.utils.FormBuilder
import org.telegram.fileconverterbot.utils.LogUtils
import javax.ejb.Singleton
import javax.ejb.Startup
import javax.ws.rs.core.MediaType

@Singleton
@Startup
open class ApplicationInitializer {

    private val logger = Logger.getLogger(this.javaClass)

    companion object {
        const val SET_WEBHOOK_ENDPOINT = "setWebhook"
        const val PARAMETER_URL = "url"
        const val PARAMETER_CERTIFICATE = "certificate"
        const val PARAMETER_MAX_CONNECTIONS = "max_connections"
        const val CERT_FILE_NAME = "fileconverter.pem"
    }

    init {
        logger.info("Application initialization...")
        initFuel()
    }

    private fun initFuel() {
        FuelManager.instance.removeAllResponseInterceptors()
        FuelManager.instance.addResponseInterceptor(validatorResponseInterceptor(IntRange(100, 599)))
    }

    private fun setWebHook() {
        javaClass.classLoader.getResourceAsStream("${Constants.RESOURCES_PATH}/$CERT_FILE_NAME").use {

            val formBuilder = FormBuilder()
            formBuilder.addText("$PARAMETER_URL", "${Constants.BASE_API_URL}/${Endpoints.UPDATES}")
            formBuilder.addText("$PARAMETER_MAX_CONNECTIONS", Constants.MAX_CONNECTIONS.toString())
            formBuilder.addFile("$PARAMETER_CERTIFICATE", it, CERT_FILE_NAME)

            try {
                val responseJson = "${Constants.TELEGRAM_BASE_API_URL}/$SET_WEBHOOK_ENDPOINT"
                        .httpPost()
                        .header(Pair(HttpHeaderNames.CONTENT_TYPE,
                                "${MediaType.MULTIPART_FORM_DATA}; boundary=${formBuilder.boundary}"))
                        .body(formBuilder.toByteArray())
                        .responseJson()
                val telegramResponse = TelegramResponse(responseJson)
                if (!telegramResponse.ok) {
                    logger.error("Unable to set webHook. ${telegramResponse.description}")
                }
            } catch(e: FuelError) {
                LogUtils.logFuelError(e, logger)
            }
        }
    }
}