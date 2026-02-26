package com.auth.passwordless.data

data class OtpData(
    val otp: String,
    val expiryTimeMillis: Long,
    val attemptsRemaining: Int = 3
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiryTimeMillis
    }

    fun isValid(inputOtp: String): Boolean {
        return !isExpired() && otp == inputOtp && attemptsRemaining > 0
    }
}