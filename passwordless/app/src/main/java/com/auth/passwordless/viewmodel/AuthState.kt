package com.auth.passwordless.viewmodel

sealed class AuthState {
    data object Initial : AuthState()
    data class OtpSent(
        val email: String,
        val remainingAttempts: Int = 3,
        val expiryTimeMillis: Long,
    ) : AuthState()
    data class OtpError(
        val email: String,
        val message: String,
        val remainingAttempts: Int,
        val expiryTimeMillis: Long
    ) : AuthState()
    data class Authenticated(
        val email: String,
        val sessionStartTimeMillis: Long
    ) : AuthState()
}