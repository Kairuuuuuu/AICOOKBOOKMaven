package com.cookbook.backend

object VerificationBackend {
    fun verifyCode(typedCode: String, correctCode: String): Boolean {
        return typedCode.trim() == correctCode.trim()
    }
}
