/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.services

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import org.jboss.resteasy.util.HttpHeaderNames
import org.telegram.fileconverterbot.exceptions.ConcurrentConversionException
import org.telegram.fileconverterbot.responseJson
import org.telegram.fileconverterbot.utils.Constants
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.ejb.Asynchronous
import javax.ejb.ConcurrencyManagement
import javax.ejb.ConcurrencyManagementType
import javax.ejb.Singleton
import javax.ws.rs.core.MediaType
import javax.xml.bind.DatatypeConverter

@Singleton
@ConcurrencyManagement(value = ConcurrencyManagementType.BEAN)
open class FileConverterDB {

    companion object {
        private val DELIMITER = "|"
    }
    private val fileIdFileNameMapping = ConcurrentHashMap<String, String>()

    private val activeUsersLock = Object()
    private val activeUsers = mutableSetOf<Int>()

    private val totalRequestsLock = Object()
    private val totalFilesConvertedLock = Object()

    private val totalStatsLock = Object()

    open fun storeFileNameMapping(fileId: String, fileName: String) {
        fileIdFileNameMapping.put(fileId, fileName)
    }

    open fun getFileNameMapping(fileId: String): String? {
        return fileIdFileNameMapping.get(fileId)
    }

    open fun removeFileNameMapping(fileId: String) {
        fileIdFileNameMapping.remove(fileId)
    }

    @Throws(ConcurrentConversionException::class)
    open fun markUserAsPerformingConversion(userId: Int) {
        synchronized(activeUsersLock) {
            if (activeUsers.contains(userId)) {
                throw ConcurrentConversionException()
            }
            activeUsers.add(userId)
        }
    }

    open fun unmarkUserAsPerformingConversion(userId: Int) {
        synchronized(activeUsersLock) {
            activeUsers.remove(userId)
        }
    }

    @Asynchronous
    open fun incrementTotalRequests() {
        synchronized(totalRequestsLock) {
            incrementFileInt(Constants.TOTAL_REQUESTS_FILENAME)
        }
    }

    open fun getTotalRequests(): Int {
        synchronized(totalRequestsLock) {
            return getFileInt(Constants.TOTAL_REQUESTS_FILENAME)
        }
    }

    @Asynchronous
    open fun incrementFilesConverted() {
        synchronized(totalFilesConvertedLock) {
            incrementFileInt(Constants.TOTAL_FILES_CONVERTED_FILENAME)
        }
    }

    open fun getFilesConverted(): Int {
        synchronized(totalFilesConvertedLock) {
            return getFileInt(Constants.TOTAL_FILES_CONVERTED_FILENAME)
        }
    }

    open fun updateStats(): Pair<Int, Int> {
        synchronized(totalStatsLock) {
            var previousTotalRequests = 0
            var previousTotalFilesConverted = 0
            val file = File(Constants.DATA_DIRECTORY, Constants.TOTAL_REQUESTS_FILES_SNAPSHOT_FILENAME)
            if (file.exists()) {
                val fileText = file.readText()
                val parts = fileText.split(DELIMITER)
                previousTotalRequests = parts[0].toInt()
                previousTotalFilesConverted = parts[1].toInt()
            }
            val totalRequests = getTotalRequests()
            val totalFilesConverted = getFilesConverted()

            file.writeText(totalRequests.toString() + DELIMITER + totalFilesConverted.toString())

            val totalRequestsDiff = totalRequests - previousTotalRequests
            val totalFilesConvertedDiff = totalFilesConverted - previousTotalFilesConverted

            return Pair(totalRequestsDiff, totalFilesConvertedDiff)
        }
    }

    private fun readFileInt(file: File): Int {
        if (!file.exists()) {
            return 0
        }
        val intAsString = file.readText()
        try {
            return intAsString.toInt()
        } catch (e: NumberFormatException) {
            return -1
        }
    }

    private fun incrementFileInt(fileName: String) {
        val totalFile = File(Constants.DATA_DIRECTORY, fileName)
        val currentTotal = readFileInt(totalFile)
        totalFile.writeText((currentTotal + 1).toString())
    }

    private fun getFileInt(fileName: String): Int {
        val totalFile = File(Constants.DATA_DIRECTORY, fileName)
        return readFileInt(totalFile)
    }
}
