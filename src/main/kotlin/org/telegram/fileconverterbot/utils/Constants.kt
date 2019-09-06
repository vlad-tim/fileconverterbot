package org.telegram.fileconverterbot.utils

import java.io.File

/**
 * Copyright (c) 2016 Timofeev Vlad
 */
object Constants {
    const val TELEGRAM_TOKEN = "..."
    const val BASE_URL = "https://<IP_ADDRESS>"
    const val API_PATH = "/${TELEGRAM_TOKEN}/api"
    const val BASE_API_URL = "${BASE_URL}${API_PATH}"

    const val MAX_CONNECTIONS = 40

    const val TELEGRAM_BASE_URL = "https://api.telegram.org"
    const val TELEGRAM_BASE_API_URL = "$TELEGRAM_BASE_URL/bot${TELEGRAM_TOKEN}"
    const val TELEGRAM_FILE_DOWNLOAD_URL = "$TELEGRAM_BASE_URL/file/bot${TELEGRAM_TOKEN}"

    const val RESOURCES_PATH = "/resources"

    const val MAX_INCOMING_FILE_SIZE_MB = 20;
    const val MAX_OUTCOMING_FILE_SIZE_MB = 50;
    const val BYTES_IN_MEGABYTE = 1024 * 1024;

    val TEMP_DIRECTORY = File(System.getProperty("java.io.tmpdir"))
    val DATA_DIRECTORY = File("/fileconverter")
    val TOTAL_REQUESTS_FILENAME = "total_requests"
    val TOTAL_FILES_CONVERTED_FILENAME = "total_files_converted"
    val TOTAL_REQUESTS_FILES_SNAPSHOT_FILENAME = "total_requests_files_snapshot"
    // 5 mins
    val FILE_STORE_TIMEOUT_MILLISECONDS = 5 * 60 * 1000

    val PROBE_SCORE_THRESHOLD = 50
}
