package com.auth.passwordless.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth.passwordless.analytics.AnalyticsLogger
import com.auth.passwordless.data.OtpManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val otpManager: OtpManager,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Generates and sends OTP to the provided email.
     * Invalidates any existing OTP and resets attempt count.
     */
    fun sendOtp(email: String) {
        viewModelScope.launch {
            if (!isValidEmail(email)) {
                // Could add an error state here, but keeping it simple
                return@launch
            }

            val (otp, expiryTime) = otpManager.generateOtp(email)

            // In a real app, you would send this OTP via email/SMS
            // For this local-only implementation, we just log it
            println("🔐 OTP for $email: $otp (expires at $expiryTime)")

            analyticsLogger.logOtpGenerated(email)

            _authState.value = AuthState.OtpSent(
                email = email,
                remainingAttempts = otpManager.getOtpData(email)?.attemptsRemaining ?: 3,
                expiryTimeMillis = expiryTime
            )
        }
    }

    /**
     * Validates the entered OTP against the stored OTP for the email.
     */
    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            val result = otpManager.validateOtp(email, otp)

            when (result) {
                is OtpManager.ValidationResult.Success -> {
                    analyticsLogger.logOtpValidationSuccess(email)
                    _authState.value = AuthState.Authenticated(
                        email = email,
                        sessionStartTimeMillis = System.currentTimeMillis()
                    )
                }

                is OtpManager.ValidationResult.Invalid -> {
                    analyticsLogger.logOtpValidationFailure(email, "invalid_otp")
                    val otpData = otpManager.getOtpData(email)
                    _authState.value = AuthState.OtpError(
                        email = email,
                        message = "Invalid OTP. ${result.attemptsRemaining} attempts remaining.",
                        remainingAttempts = result.attemptsRemaining,
                        expiryTimeMillis = otpData?.expiryTimeMillis ?: System.currentTimeMillis()
                    )
                }

                is OtpManager.ValidationResult.Expired -> {
                    analyticsLogger.logOtpValidationFailure(email, "expired")
                    _authState.value = AuthState.Initial
                }

                is OtpManager.ValidationResult.MaxAttemptsExceeded -> {
                    analyticsLogger.logOtpValidationFailure(email, "max_attempts_exceeded")
                    _authState.value = AuthState.Initial
                }

                is OtpManager.ValidationResult.NoOtpFound -> {
                    analyticsLogger.logOtpValidationFailure(email, "no_otp_found")
                    _authState.value = AuthState.Initial
                }
            }
        }
    }

    /**
     * Logs out the user and returns to initial state.
     */
    fun logout() {
        viewModelScope.launch {
            val currentState = _authState.value
            if (currentState is AuthState.Authenticated) {
                val sessionDuration = (System.currentTimeMillis() - currentState.sessionStartTimeMillis) / 1000
                analyticsLogger.logLogout(currentState.email, sessionDuration)
            }
            _authState.value = AuthState.Initial
        }
    }

    /**
     * Returns to OTP entry screen (e.g., when user wants to resend OTP)
     */
    fun backToOtpEntry(email: String) {
        viewModelScope.launch {
            val otpData = otpManager.getOtpData(email)
            if (otpData != null && !otpData.isExpired()) {
                _authState.value = AuthState.OtpSent(
                    email = email,
                    remainingAttempts = otpData.attemptsRemaining,
                    expiryTimeMillis = otpData.expiryTimeMillis
                )
            } else {
                _authState.value = AuthState.Initial
            }
        }
    }

    /**
     * Resets to initial state
     */
    fun resetToInitial() {
        _authState.value = AuthState.Initial
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}