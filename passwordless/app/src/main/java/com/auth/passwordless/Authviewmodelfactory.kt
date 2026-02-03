package com.auth.passwordless

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.auth.passwordless.analytics.AnalyticsLogger
import com.auth.passwordless.data.OtpManager
import com.auth.passwordless.viewmodel.AuthViewModel

class AuthViewModelFactory(
    private val otpManager: OtpManager,
    private val analyticsLogger: AnalyticsLogger
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(otpManager, analyticsLogger) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}