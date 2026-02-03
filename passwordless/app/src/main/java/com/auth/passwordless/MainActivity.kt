package com.auth.passwordless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.auth.passwordless.analytics.AnalyticsLogger
import com.auth.passwordless.analytics.FirebaseAnalyticsLogger
import com.auth.passwordless.data.OtpManager
import com.auth.passwordless.ui.LoginScreen
import com.auth.passwordless.ui.OtpScreen
import com.auth.passwordless.ui.SessionScreen
import com.auth.passwordless.ui.theme.PasswordlessTheme
import com.auth.passwordless.viewmodel.AuthState
import com.auth.passwordless.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val otpManager by lazy { OtpManager() }
    private val analyticsLogger by lazy { FirebaseAnalyticsLogger() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PasswordlessTheme {
                PasswordlessApp(
                    otpManager = otpManager,
                    analyticsLogger = analyticsLogger
                )
            }
        }
    }
}


@Composable
fun PasswordlessApp(
    otpManager: OtpManager,
    analyticsLogger: AnalyticsLogger,
    viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(otpManager, analyticsLogger)
    )
) {
    val authState by viewModel.authState.collectAsState()

    when (val state = authState) {
        is AuthState.Initial -> LoginScreen { viewModel.sendOtp(it) }

        is AuthState.OtpSent -> OtpScreen(
            email = state.email,
            expiryTimeMillis = state.expiryTimeMillis,
            remainingAttempts = state.remainingAttempts,
            errorMessage = null,
            onVerifyOtp = { viewModel.verifyOtp(state.email, it) },
            onResendOtp = { viewModel.sendOtp(state.email) },
            onBackToLogin = { viewModel.resetToInitial() }
        )

        is AuthState.OtpError -> OtpScreen(
            email = state.email,
            expiryTimeMillis = state.expiryTimeMillis,
            remainingAttempts = state.remainingAttempts,
            errorMessage = state.message,
            onVerifyOtp = { viewModel.verifyOtp(state.email, it) },
            onResendOtp = { viewModel.sendOtp(state.email) },
            onBackToLogin = { viewModel.resetToInitial() }
        )

        is AuthState.Authenticated -> SessionScreen(
            email = state.email,
            sessionStartTimeMillis = state.sessionStartTimeMillis,
            onLogout = { viewModel.logout() }
        )
    }
}
