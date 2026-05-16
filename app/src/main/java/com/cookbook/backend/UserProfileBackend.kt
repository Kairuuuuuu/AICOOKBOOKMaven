package com.cookbook.backend

object UserProfileBackend {
    var firstName: String = "Guest"
    var lastName: String = ""
    var email: String = ""

    fun getAvatarInitial(): String {
        return if (firstName.isNotBlank() && firstName != "Guest") {
            firstName.first().uppercase()
        } else "G"
    }

    fun updateProfile(first: String, last: String) {
        firstName = first
        lastName = last
    }

    fun performLogout() {
        FirebaseManager.logout()
        firstName = "Guest"
        lastName = ""
        email = ""
    }
}
