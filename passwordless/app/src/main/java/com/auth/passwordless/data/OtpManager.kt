package com.auth.passwordless.data

import kotlin.random.Random

class OtpManager {
    private val otpStorage: MutableMap<String, OtpData> = mutableMapOf()

    companion object {
        private const val OTP_LENGTH = 6
        private const val OTP_EXPIRY_DURATION_MS = 60_000L // 60 seconds
        private const val MAX_ATTEMPTS = 3
    }

    /**
     * Generates a new 6-digit OTP for the given email.
     * Invalidates any existing OTP and resets attempt count.
     *
     * @param email The email address to generate OTP for
     * @return Generated OTP string and expiry time in milliseconds
     */
    fun generateOtp(email: String): Pair<String, Long> {
        val otp = generateRandomOtp()
        val expiryTime = System.currentTimeMillis() + OTP_EXPIRY_DURATION_MS

        otpStorage[email] = OtpData(
            otp = otp,
            expiryTimeMillis = expiryTime,
            attemptsRemaining = MAX_ATTEMPTS
        )

        return Pair(otp, expiryTime)
    }

    /**
     * Validates the provided OTP against the stored OTP for the email.
     * Decrements attempt count on failure.
     *
     * @param email The email address
     * @param inputOtp The OTP entered by user
     * @return ValidationResult indicating success, failure reason, and remaining attempts
     */
    fun validateOtp(email: String, inputOtp: String): ValidationResult {
        val otpData = otpStorage[email]
            ?: return ValidationResult.NoOtpFound

        // Check expiry first
        if (otpData.isExpired()) {
            otpStorage.remove(email)
            return ValidationResult.Expired
        }

        // Check attempts
        if (otpData.attemptsRemaining <= 0) {
            otpStorage.remove(email)
            return ValidationResult.MaxAttemptsExceeded
        }

        // Validate OTP
        if (otpData.otp == inputOtp) {
            otpStorage.remove(email) // Clear OTP on successful validation
            return ValidationResult.Success
        }

        // Decrement attempts on failure
        val updatedData = otpData.copy(
            attemptsRemaining = otpData.attemptsRemaining - 1
        )
        otpStorage[email] = updatedData

        return ValidationResult.Invalid(updatedData.attemptsRemaining)
    }

    /**
     * Gets the OTP data for a given email (for testing/debugging purposes)
     */
    fun getOtpData(email: String): OtpData? {
        return otpStorage[email]
    }

    /**
     * Gets remaining time in milliseconds for an OTP
     */
    fun getRemainingTime(email: String): Long {
        val otpData = otpStorage[email] ?: return 0L
        val remaining = otpData.expiryTimeMillis - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0L
    }

    /**
     * Clears OTP for a specific email
     */
    fun clearOtp(email: String) {
        otpStorage.remove(email)
    }

    private fun generateRandomOtp(): String {
        return (100000..999999).random().toString()
    }

    sealed class ValidationResult {
        data object Success : ValidationResult()
        data object Expired : ValidationResult()
        data object NoOtpFound : ValidationResult()
        data object MaxAttemptsExceeded : ValidationResult()
        data class Invalid(val attemptsRemaining: Int) : ValidationResult()
    }
}