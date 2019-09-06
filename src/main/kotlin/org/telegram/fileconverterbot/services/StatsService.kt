/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.services

import javax.ejb.Schedule
import javax.ejb.Singleton
import javax.inject.Inject

@Singleton
open class StatsService {

    @Inject
    lateinit private var fileConverterDB: FileConverterDB

    @Inject
    lateinit private var emailService: EmailService

    @Schedule(hour = "2", minute = "0", second = "0", persistent = false)
    private fun updateStats() {
        val stats = fileConverterDB.updateStats()
        val subject = "File Converted daily stats"
        val text = "Total requests: ${stats.first}.\r\nTotal files converted: ${stats.second}"
        emailService.sendEmail(subject, text)
    }
}