package com.cookbook.backend

import com.cookbook.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseManager {

    fun connect(context: android.content.Context) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .setApplicationId("1:863089731969:android:1057d7248353846c4ee080")
                .setProjectId("ai-cookbook-f347b")
                .build()
            FirebaseApp.initializeApp(context, options)
        }
    }

    suspend fun signUpUser(email: String, password: String): String = suspendCoroutine { cont ->
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume("SUCCESS")
                } else {
                    val msg = mapFirebaseError(task.exception)
                    cont.resume(msg)
                }
            }
    }

    suspend fun loginUser(email: String, password: String): String = suspendCoroutine { cont ->
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume("SUCCESS")
                } else {
                    val msg = mapFirebaseError(task.exception)
                    cont.resume(msg)
                }
            }
    }

    suspend fun changePassword(email: String, newPassword: String): String = suspendCoroutine { cont ->
        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                user.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cont.resume("SUCCESS")
                        } else {
                            val msg = mapFirebaseError(task.exception)
                            cont.resume(msg)
                        }
                    }
            } else {
                cont.resume("No authenticated user found. Please log in again.")
            }
        } catch (e: Exception) {
            cont.resume("Password update failed: ${e.message}")
        }
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String) -> Unit) {
        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Password reset email sent.")
                } else {
                    val msg = mapFirebaseError(task.exception)
                    onResult(false, msg)
                }
            }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun mapFirebaseError(exception: Exception?): String {
        if (exception !is FirebaseAuthException) {
            return exception?.message ?: "An unknown error occurred."
        }
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password."
            "ERROR_USER_NOT_FOUND" -> "No account found with this email."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered."
            "ERROR_WEAK_PASSWORD" -> "Password must be at least 6 characters."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later."
            "ERROR_USER_DISABLED" -> "This account has been disabled."
            "ERROR_OPERATION_NOT_ALLOWED" -> "This operation is not allowed."
            "ERROR_REQUIRES_RECENT_LOGIN" -> "Please log out and log in again before changing your password."
            else -> exception.message ?: "An error occurred."
        }
    }
}
