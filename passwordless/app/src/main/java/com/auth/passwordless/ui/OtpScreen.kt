package com.auth.passwordless.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun OtpScreen(
    email: String,
    expiryTimeMillis: Long,
    remainingAttempts: Int,
    errorMessage: String? = null,
    onVerifyOtp: (String) -> Unit,
    onResendOtp: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var otp by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var remainingSeconds by remember { mutableStateOf(0) }

    // Countdown timer effect
    LaunchedEffect(expiryTimeMillis) {
        while (isActive) {
            val remaining = (expiryTimeMillis - System.currentTimeMillis()) / 1000
            remainingSeconds = if (remaining > 0) remaining.toInt() else 0

            if (remainingSeconds <= 0) {
                break
            }

            delay(1000)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter OTP",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "OTP sent to $email",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Timer display
        Text(
            text = if (remainingSeconds > 0) {
                "Time remaining: ${remainingSeconds}s"
            } else {
                "OTP Expired"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (remainingSeconds > 10) {
                MaterialTheme.colorScheme.primary
            } else if (remainingSeconds > 0) {
                MaterialTheme.colorScheme.error
            } else {
                Color.Red
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = otp,
            onValueChange = {
                if (it.text.length <= 6 && it.text.all { char -> char.isDigit() }) {
                    otp = it
                }
            },
            label = { Text("6-Digit OTP") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = remainingSeconds > 0
        )

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Attempts remaining
        Text(
            text = "Attempts remaining: $remainingAttempts",
            style = MaterialTheme.typography.bodySmall,
            color = if (remainingAttempts <= 1) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (otp.text.isNotBlank()) {
                    onVerifyOtp(otp.text)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = otp.text.length == 6 && remainingSeconds > 0
        ) {
            Text("Verify OTP")
        }

        TextButton(
            onClick = onResendOtp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Resend OTP")
        }

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }
    }
}