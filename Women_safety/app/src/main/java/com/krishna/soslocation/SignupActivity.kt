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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SignupActivity : AppCompatActivity() {

    // Firebase instances
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // UI Components
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var mobileInputLayout: TextInputLayout

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var mobileEditText: TextInputEditText

    private lateinit var signupButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI components
        initializeViews()

        // Set up click listeners
        setupClickListeners()
    }

    /**
     * Initialize all UI components
     */
    private fun initializeViews() {
        nameInputLayout = findViewById(R.id.nameInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout)
        mobileInputLayout = findViewById(R.id.mobileInputLayout)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        mobileEditText = findViewById(R.id.mobileEditText)

        signupButton = findViewById(R.id.signupButton)
        loginTextView = findViewById(R.id.loginTextView)
        progressBar = findViewById(R.id.progressBar)
        titleTextView = findViewById(R.id.titleTextView)

        // Initially hide progress bar
        progressBar.visibility = View.GONE
    }

    /**
     * Set up click listeners for buttons and text views
     */
    private fun setupClickListeners() {
        // Signup button click listener
        signupButton.setOnClickListener {
            attemptSignup()
        }

        // Navigate to login activity
        loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

    /**
     * Attempt to register new user
     */
    private fun attemptSignup() {
        // Clear previous errors
        clearErrors()

        // Get input values
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val mobile = mobileEditText.text.toString().trim()

        // Validate inputs
        if (!validateInputs(name, email, password, confirmPassword, mobile)) {
            return
        }

        // Show loading state
        showLoading(true)

        // Create Firebase user account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account created successfully
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        // Update user profile with name
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    // Save user data to Firestore
                                    saveUserToFirestore(user.uid, name, email, mobile)
                                } else {
                                    showLoading(false)
                                    Toast.makeText(this, "Failed to update profile: ${profileTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }

                        // Send email verification
                        sendEmailVerification(user)
                    }
                } else {
                    // Account creation failed
                    showLoading(false)
                    handleSignupError(task.exception)
                }
            }
    }

    /**
     * Save user profile data to Firestore database
     */
    private fun saveUserToFirestore(userId: String, name: String, email: String, mobile: String) {
        val userData = hashMapOf(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "mobile" to mobile,
            "createdAt" to Date(),
            "isEmailVerified" to false,
            "profileComplete" to true
        )

        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Account created successfully! Please verify your email.", Toast.LENGTH_LONG).show()
                // Navigate to login activity
                navigateToLoginActivity()
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                Toast.makeText(this, "Failed to save user data: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Send email verification to newly created user
     */
    private fun sendEmailVerification(user: com.google.firebase.auth.FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent to ${user.email}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Validate all input fields
     */
    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String, mobile: String): Boolean {
        var isValid = true

        // Validate name
        if (TextUtils.isEmpty(name)) {
            nameInputLayout.error = "Name is required"
            isValid = false
        } else if (name.length < 2) {
            nameInputLayout.error = "Name must be at least 2 characters"
            isValid = false
        }

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
        } else if (!isPasswordStrong(password)) {
            passwordInputLayout.error = "Password must contain at least one letter and one number"
            isValid = false
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordInputLayout.error = "Passwords do not match"
            isValid = false
        }

        // Validate mobile number
        if (TextUtils.isEmpty(mobile)) {
            mobileInputLayout.error = "Mobile number is required"
            isValid = false
        } else if (!isValidMobileNumber(mobile)) {
            mobileInputLayout.error = "Please enter a valid 10-digit mobile number"
            isValid = false
        }

        return isValid
    }

    /**
     * Check if password is strong enough
     */
    private fun isPasswordStrong(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    /**
     * Validate mobile number format
     */
    private fun isValidMobileNumber(mobile: String): Boolean {
        // Check if it's a 10-digit number (can be customized based on country)
        return mobile.length == 10 && mobile.all { it.isDigit() }
    }

    /**
     * Clear all error messages
     */
    private fun clearErrors() {
        nameInputLayout.error = null
        emailInputLayout.error = null
        passwordInputLayout.error = null
        confirmPasswordInputLayout.error = null
        mobileInputLayout.error = null
    }

    /**
     * Handle signup errors and show appropriate messages
     */
    private fun handleSignupError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                "Password is too weak. Please choose a stronger password."
            }
            is FirebaseAuthUserCollisionException -> {
                "An account with this email already exists. Please login instead."
            }
            else -> {
                "Signup failed: ${exception?.message ?: "Unknown error"}"
            }
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

        // Show error in relevant field
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                passwordInputLayout.error = "Password is too weak"
            }
            is FirebaseAuthUserCollisionException -> {
                emailInputLayout.error = "Email already registered"
            }
        }
    }

    /**
     * Show or hide loading state
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            signupButton.isEnabled = false
            signupButton.text = "Creating Account..."
        } else {
            progressBar.visibility = View.GONE
            signupButton.isEnabled = true
            signupButton.text = "Sign Up"
        }
    }

    /**
     * Navigate to LoginActivity after successful signup
     */
    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}