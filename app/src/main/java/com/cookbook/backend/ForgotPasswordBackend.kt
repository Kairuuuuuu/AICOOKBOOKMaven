package com.cookbook.backend

object ForgotPasswordBackend {

    sealed class PasswordChangeResult {
        data object Success : PasswordChangeResult()
        data class Error(val message: String) : PasswordChangeResult()
    }

    suspend fun processPasswordChange(
        email: String,
        newPassword: String,
        confirmPassword: String
    ): PasswordChangeResult {
        if (newPassword.isBlank() || confirmPassword.isBlank()) {
            return PasswordChangeResult.Error("Please enter a new password!")
        }
        if (newPassword != confirmPassword) {
            return PasswordChangeResult.Error("Passwords do not match!")
        }
        if (newPassword.length < 6) {
            return PasswordChangeResult.Error("Password must be at least 6 characters!")
        }

        val result = FirebaseManager.changePassword(email, newPassword)
        return if (result == "SUCCESS") {
            PasswordChangeResult.Success
        } else {
            PasswordChangeResult.Error(result)
        }
    }
}
