package com.cookbook.backend

import com.cookbook.BuildConfig
import java.util.Properties
import java.util.Random
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {

    private const val SENDER_EMAIL = "aicookbooknoreply@gmail.com"

    fun sendOTP(recipientEmail: String): String {
        val emailPassword = BuildConfig.EMAIL_PASSWORD
        if (emailPassword.isBlank()) return ""
        val otp = String.format("%06d", Random().nextInt(999999))

        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(SENDER_EMAIL, emailPassword)
            }
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(SENDER_EMAIL))
            setRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
            subject = "Dirk's CookBook - Verification Code"
            setContent(
                """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto;">
                    <h2 style="color: #2D6A4F;">Dirk's CookBook</h2>
                    <p>Your verification code is:</p>
                    <h1 style="color: #2D6A4F; font-size: 32px; letter-spacing: 8px;">$otp</h1>
                    <p>Enter this code in the app to verify your email.</p>
                    <p style="color: #666; font-size: 12px;">
                        If you didn't request this code, please ignore this email.
                    </p>
                </div>
                """.trimIndent(),
                "text/html; charset=utf-8"
            )
        }

        Transport.send(message)
        return otp
    }
}
