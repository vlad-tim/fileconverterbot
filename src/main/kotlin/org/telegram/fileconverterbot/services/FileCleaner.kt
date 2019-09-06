/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.services

import org.telegram.fileconverterbot.utils.Constants
import java.io.File
import java.util.Date
import java.util.UUID
import javax.ejb.Schedule
import javax.ejb.Singleton
import javax.inject.Inject

@Singleton
open class FileCleaner {

    @Inject
    lateinit private var fileConverterDB: FileConverterDB

    @Schedule(hour = "*", minute = "*/5", second = "0", persistent = false)
    open fun removeTemporaryFiles() {
        val now = Date().time
        Constants.TEMP_DIRECTORY
                .listFiles()
                .filter { file ->
                    isFileForConversion(file) &&
                            (now - file.lastModified() > Constants.FILE_STORE_TIMEOUT_MILLISECONDS)
                }
                .forEach {
                    file ->
                    file.delete()
                    fileConverterDB.removeFileNameMapping(file.name)
                }
    }

    private fun isFileForConversion(file: File): Boolean {
        val fileName = file.name
        try {
            UUID.fromString(fileName)
            return true
        } catch (e: IllegalArgumentException) {
            return false
        }
    }
}