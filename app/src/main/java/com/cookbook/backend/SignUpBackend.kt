package com.cookbook.backend

object SignUpBackend {

    sealed class SignUpResult {
        data object Success : SignUpResult()
        data class Error(val message: String) : SignUpResult()
    }

    suspend fun attemptSignUp(email: String, password: String, confirmPassword: String): SignUpResult {
        if (password.isBlank() || confirmPassword.isBlank()) {
            return SignUpResult.Error("Password fields cannot be empty!")
        }
        if (password != confirmPassword) {
            return SignUpResult.Error("Passwords do not match!")
        }
        if (password.length < 6) {
            return SignUpResult.Error("Password must be at least 6 characters!")
        }

        val result = FirebaseManager.signUpUser(email, password)
        return if (result == "SUCCESS") {
            SignUpResult.Success
        } else {
            SignUpResult.Error(result)
        }
    }
}
