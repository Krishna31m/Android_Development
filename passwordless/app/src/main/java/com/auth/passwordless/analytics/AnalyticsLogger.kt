package com.auth.passwordless.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import java.security.MessageDigest

/**
 * Analytics logger interface for tracking authentication events.
 * Using Firebase Analytics for production-ready event tracking and user analytics.
 */
interface AnalyticsLogger {
    fun logOtpGenerated(email: String)
    fun logOtpValidationSuccess(email: String)
    fun logOtpValidationFailure(email: String, reason: String)
    fun logLogout(email: String, sessionDurationSeconds: Long)
}

class FirebaseAnalyticsLogger : AnalyticsLogger {
    private val analytics: FirebaseAnalytics = Firebase.analytics

    companion object {
        private const val EVENT_OTP_GENERATED = "otp_generated"
        private const val EVENT_OTP_VALIDATION_SUCCESS = "otp_validation_success"
        private const val EVENT_OTP_VALIDATION_FAILURE = "otp_validation_failure"
        private const val EVENT_LOGOUT = "user_logout"

        private const val PARAM_EMAIL = "email"
        private const val PARAM_FAILURE_REASON = "failure_reason"
        private const val PARAM_SESSION_DURATION = "session_duration_seconds"
    }

    override fun logOtpGenerated(email: String) {
        val bundle = Bundle().apply {
            putString(PARAM_EMAIL, hashEmail(email))
        }
        analytics.logEvent(EVENT_OTP_GENERATED, bundle)
    }

    override fun logOtpValidationSuccess(email: String) {
        val bundle = Bundle().apply {
            putString(PARAM_EMAIL, hashEmail(email))
        }
        analytics.logEvent(EVENT_OTP_VALIDATION_SUCCESS, bundle)
    }

    override fun logOtpValidationFailure(email: String, reason: String) {
        val bundle = Bundle().apply {
            putString(PARAM_EMAIL, hashEmail(email))
            putString(PARAM_FAILURE_REASON, reason)
        }
        analytics.logEvent(EVENT_OTP_VALIDATION_FAILURE, bundle)
    }

    override fun logLogout(email: String, sessionDurationSeconds: Long) {
        val bundle = Bundle().apply {
            putString(PARAM_EMAIL, hashEmail(email))
            putLong(PARAM_SESSION_DURATION, sessionDurationSeconds)
        }
        analytics.logEvent(EVENT_LOGOUT, bundle)
    }

    /**
     * Hash email for privacy - we don't want to send PII to analytics
     */
    private fun hashEmail(email: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(email.lowercase().trim().toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

}