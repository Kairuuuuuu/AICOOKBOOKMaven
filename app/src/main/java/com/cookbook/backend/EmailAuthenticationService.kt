package com.cookbook.backend

import com.cookbook.data.model.OTPResult
import com.cookbook.data.model.OTPStatus

object EmailAuthenticationService {

    fun processEmailForOTP(email: String): OTPResult {
        if (!email.contains("@") || email.isBlank()) {
            return OTPResult(status = OTPStatus.INVALID_EMAIL)
        }
        return try {
            val sentCode = EmailSender.sendOTP(email)
            if (sentCode.isNotBlank()) {
                OTPResult(status = OTPStatus.SUCCESS, sentCode = sentCode)
            } else {
                OTPResult(status = OTPStatus.CONNECTION_ERROR)
            }
        } catch (_: Exception) {
            OTPResult(status = OTPStatus.CONNECTION_ERROR)
        }
    }
}
