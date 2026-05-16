package com.cookbook.backend

object AuthenticationService {

    sealed class LoginResult {
        data class Success(val email: String, val firstName: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    suspend fun attemptLogin(email: String, password: String): LoginResult {
        if (email.isBlank() || password.isBlank()) {
            return LoginResult.Error("Please fill in all fields!")
        }
        if (password.length < 6) {
            return LoginResult.Error("Password must be at least 6 characters!")
        }

        val result = FirebaseManager.loginUser(email, password)
        return if (result == "SUCCESS") {
            val firstName = generateFirstName(email)
            LoginResult.Success(email, firstName)
        } else {
            LoginResult.Error(result)
        }
    }

    private fun generateFirstName(email: String): String {
        val localPart = email.substringBefore("@")
        return localPart.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            .replace(Regex("[0-9._-]"), " ")
            .split(" ")
            .firstOrNull { it.isNotBlank() } ?: "User"
    }
}
