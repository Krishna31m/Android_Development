package com.krishna.soslocation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    // Firebase Authentication instance
    private lateinit var firebaseAuth: FirebaseAuth

    // UI Components
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize UI components
        initializeViews()

        // Set up click listeners
        setupClickListeners()
    }

    /**
     * Initialize all UI components
     */
    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupTextView = findViewById(R.id.signupTextView)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        progressBar = findViewById(R.id.progressBar)
        titleTextView = findViewById(R.id.titleTextView)

        // Initially hide progress bar
        progressBar.visibility = View.GONE
    }

    /**
     * Set up click listeners for buttons and text views
     */
    private fun setupClickListeners() {
        // Login button click listener
        loginButton.setOnClickListener {
            attemptLogin()
        }

        // Navigate to signup activity
        signupTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Forgot password functionality
        forgotPasswordTextView.setOnClickListener {
            handleForgotPassword()
        }
    }

    /**
     * Attempt to log in the user with provided credentials
     */
    private fun attemptLogin() {
        // Clear previous errors
        emailInputLayout.error = null
        passwordInputLayout.error = null

        // Get input values
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validate inputs
        if (!validateInputs(email, password)) {
            return
        }

        // Show loading state
        showLoading(true)

        // Attempt Firebase login
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    // Login successful
                    val user = firebaseAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Email is verified, proceed to main activity
                        Toast.makeText(this, "Welcome back! Login successful.", Toast.LENGTH_SHORT).show()
                        navigateToMainActivity()
                    } else {
                        // Email not verified
                        Toast.makeText(this, "Please verify your email address first.", Toast.LENGTH_LONG).show()
                        sendEmailVerification()
                    }
                } else {
                    // Login failed, handle error
                    handleLoginError(task.exception)
                }
            }
    }

    /**
     * Validate email and password inputs
     */
    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Please enter a valid email address"
            isValid = false
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    /**
     * Handle login errors and show appropriate messages
     */
    private fun handleLoginError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> {
                "No account found with this email address. Please sign up first."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                "Invalid email or password. Please try again."
            }
            else -> {
                "Login failed: ${exception?.message ?: "Unknown error"}"
            }
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

        // Show error in relevant field
        if (exception is FirebaseAuthInvalidUserException) {
            emailInputLayout.error = "Email not found"
        } else if (exception is FirebaseAuthInvalidCredentialsException) {
            passwordInputLayout.error = "Incorrect password"
        }
    }

    /**
     * Handle forgot password functionality
     */
    private fun handleForgotPassword() {
        val email = emailEditText.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.error = "Please enter your email address first"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Please enter a valid email address"
            return
        }

        showLoading(true)

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    /**
     * Send email verification to current user
     */
    private fun sendEmailVerification() {
        val user = firebaseAuth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent to ${user.email}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Show or hide loading state
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
            loginButton.text = "Logging in..."
        } else {
            progressBar.visibility = View.GONE
            loginButton.isEnabled = true
            loginButton.text = "Login"
        }
    }

    /**
     * Navigate to MainActivity after successful login
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    /**
     * Check if user is already logged in when activity starts
     */
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            // User is already logged in, go to main activity
            navigateToMainActivity()
        }
    }

    /**
     * Handle back button press
     */
    override fun onBackPressed() {
        // Exit the app when back is pressed from login screen
        finishAffinity()
    }
}