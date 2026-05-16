package com.cookbook.backend

object ChangePasswordService {

    sealed class PasswordUpdateResult {
        data object Success : PasswordUpdateResult()
        data class Error(val message: String) : PasswordUpdateResult()
        data object RequiresReauth : PasswordUpdateResult()
    }

    suspend fun updatePassword(
        email: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): PasswordUpdateResult {
        if (newPassword.isBlank() || confirmPassword.isBlank()) {
            return PasswordUpdateResult.Error("Please enter a new password!")
        }
        if (newPassword != confirmPassword) {
            return PasswordUpdateResult.Error("Passwords do not match!")
        }
        if (newPassword.length < 6) {
            return PasswordUpdateResult.Error("Password must be at least 6 characters!")
        }

        val loginResult = FirebaseManager.loginUser(email, currentPassword)
        if (loginResult != "SUCCESS") {
            return PasswordUpdateResult.Error("Current password is incorrect.")
        }

        val result = FirebaseManager.changePassword(email, newPassword)
        return if (result == "SUCCESS") {
            PasswordUpdateResult.Success
        } else {
            PasswordUpdateResult.Error(result)
        }
    }
}
