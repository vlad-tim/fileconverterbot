/**
 * Copyright (c) 2016 Timofeev Vlad
 */
package org.telegram.fileconverterbot.services

import org.jboss.logging.Logger
import java.util.Properties
import javax.ejb.Asynchronous
import javax.ejb.Stateless
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Stateless
open class EmailService {

    private val logger = Logger.getLogger(this.javaClass)

    companion object {
        private const val EMAIL_SENDER = "..."
        private const val EMAIL_SENDER_PASSWORD = "..."
        private const val EMAIL_RECIPIENT = "..."
    }

    @Asynchronous
    open fun sendEmail(subject: String, text: String) {
        val session = createEmailSession()
        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(EMAIL_SENDER))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_RECIPIENT))
            message.subject = subject
            message.setText(text)

            Transport.send(message)
        } catch (e: MessagingException) {
            logger.error("Unable to send email", e)
        }
        logger.info("Email sent")
    }

    private fun createEmailSession(): Session {
        val props = Properties()
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.host", "smtp.yandex.ru")
        props.put("mail.smtp.port", "587")
        return Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EMAIL_SENDER, EMAIL_SENDER_PASSWORD)
                    }
                })
    }
}
