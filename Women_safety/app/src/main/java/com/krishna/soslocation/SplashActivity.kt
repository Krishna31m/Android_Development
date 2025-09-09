package com.krishna.soslocation


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity : AppCompatActivity() {

    // Firebase Authentication instance
    private lateinit var firebaseAuth: FirebaseAuth

    // UI Components
    private lateinit var appLogoImageView: ImageView
    private lateinit var appNameTextView: TextView
    private lateinit var loadingTextView: TextView

    // Splash screen duration in milliseconds
    private val splashDuration = 2500L // 2.5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize UI components
        initializeViews()

        // Start animations
        startAnimations()

        // Check authentication status after splash duration
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationStatus()
        }, splashDuration)
    }

    /**
     * Initialize all UI components
     */
    private fun initializeViews() {
        appLogoImageView = findViewById(R.id.appLogoImageView)
        appNameTextView = findViewById(R.id.appNameTextView)
        loadingTextView = findViewById(R.id.loadingTextView)
    }

    /**
     * Start splash screen animations
     */
    private fun startAnimations() {
        // Fade in animation for logo
        val fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeInAnimation.duration = 1000 // 1 second
        appLogoImageView.startAnimation(fadeInAnimation)

        // Slide in animation for app name
        val slideInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        slideInAnimation.duration = 800
        slideInAnimation.startOffset = 300 // Start after 300ms
        appNameTextView.startAnimation(slideInAnimation)

        // Blinking animation for loading text
        val blinkAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        blinkAnimation.duration = 1000
        blinkAnimation.repeatCount = 2
        blinkAnimation.startOffset = 800
        loadingTextView.startAnimation(blinkAnimation)
    }

    /**
     * Check if user is already logged in and navigate accordingly
     */
    private fun checkAuthenticationStatus() {
        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        if (currentUser != null && currentUser.isEmailVerified) {
            // User is logged in and email is verified, go to MainActivity
            navigateToMainActivity()
        } else {
            // User is not logged in or email not verified, go to LoginActivity
            navigateToLoginActivity()
        }
    }

    /**
     * Navigate to MainActivity (user already logged in)
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // Add flags to prevent going back to splash screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        // Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    /**
     * Navigate to LoginActivity (user not logged in)
     */
    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        // Add flags to prevent going back to splash screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        // Add transition animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    /**
     * Handle back button - prevent going back during splash
     */
    override fun onBackPressed() {
        // Do nothing - prevent user from going back during splash screen
    }

    /**
     * Override onDestroy to ensure proper cleanup
     */
    override fun onDestroy() {
        super.onDestroy()
        // Clear any pending handlers if activity is destroyed
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
    }
}