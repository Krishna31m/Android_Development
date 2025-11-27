package com.example.healthreminder.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.example.healthreminder.ui.auth.LoginActivity
import com.example.healthreminder.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAuthentication()
        }, 2000)
    }

    private fun checkUserAuthentication() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is logged in, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not logged in, go to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}