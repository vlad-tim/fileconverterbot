package org.telegram.fileconverterbot.utils

import com.github.kittinunf.fuel.core.FuelError
import org.jboss.logging.Logger

/**
 * Copyright (c) 2016 Timofeev Vlad
 */
object LogUtils {
    /**
     * This method is used to log all the important information about FuelError
     */
    fun logFuelError(e: FuelError, logger: Logger) {
        if (e.errorData.isNotEmpty()) {
            logger.error(String(e.errorData))
        }
        logger.error("FuelError", e.exception)
    }
}