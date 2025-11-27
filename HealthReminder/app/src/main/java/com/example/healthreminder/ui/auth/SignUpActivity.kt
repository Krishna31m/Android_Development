package com.example.healthreminder.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.example.healthreminder.data.model.UserProfile
import com.example.healthreminder.ui.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignup: MaterialButton
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnSignup = findViewById(R.id.btn_signup)
        tvLogin = findViewById(R.id.tv_login)
        progressBar = findViewById(R.id.progress_bar)

        // Sign up button click
        btnSignup.setOnClickListener {
            signUpUser()
        }

        // Login link click
        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun signUpUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Please confirm your password"
            etConfirmPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return
        }

        // Show progress
        progressBar.visibility = View.VISIBLE
        btnSignup.isEnabled = false

        // Create user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, create user profile
                    val userId = auth.currentUser?.uid ?: ""
                    createUserProfile(userId, name, email)
                } else {
                    // Sign up failed
                    progressBar.visibility = View.GONE
                    btnSignup.isEnabled = true
                    Toast.makeText(
                        this,
                        "Sign Up Failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun createUserProfile(userId: String, name: String, email: String) {
        val userProfile = UserProfile(
            id = userId,
            name = name,
            email = email,
            createdAt = Timestamp.now()
        )

        firestore.collection("users")
            .document(userId)
            .set(userProfile)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                btnSignup.isEnabled = true
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                // Go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                btnSignup.isEnabled = true
                Toast.makeText(
                    this,
                    "Error creating profile: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}