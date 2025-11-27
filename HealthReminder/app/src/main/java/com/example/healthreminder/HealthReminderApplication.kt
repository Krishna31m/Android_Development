package com.example.healthreminder

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * Application class for initialization
 */
class HealthReminderApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Firestore offline persistence
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Initialize other components
        initializeApp()
    }

    private fun initializeApp() {
        // Initialize shared preferences
        // Initialize notification channels
        // Setup any other app-wide configurations
    }

    companion object {
        private const val TAG = "HealthReminderApp"
    }
}