package com.krishna.soslocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    // Firebase instances
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // UI Components
    private lateinit var welcomeTextView: TextView
    private lateinit var contactsContainer: LinearLayout
    private lateinit var addContactButton: Button
    private lateinit var saveContactsButton: Button
    private lateinit var sosButton: Button
    private lateinit var stopSosButton: Button
    private lateinit var statusText: TextView

    // Location and SMS related
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var sharedPreferences: SharedPreferences

    // SOS tracking variables
    private var isSosActive = false
    private var sosHandler: Handler? = null
    private var sosRunnable: Runnable? = null
    private val sosUpdateInterval = 30000L // 30 seconds

    // Permissions
    private val locationPermissionCode = 100
    private val smsPermissionCode = 101
    private val allPermissionsCode = 102

    // Contact storage
    private val contactsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if user is authenticated
        checkAuthenticationState()

        initializeViews()
        initializeLocationServices()
        initializeSharedPreferences()
        loadSavedContacts()
        setupClickListeners()

        // Load user profile and display welcome message
        loadUserProfile()

        // Request permissions on startup
        requestPermissions()
    }

    /**
     * Check if user is authenticated, redirect to login if not
     */
    private fun checkAuthenticationState() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null || !currentUser.isEmailVerified) {
            // User not authenticated or email not verified, redirect to login
            redirectToLogin()
            return
        }
    }

    /**
     * Redirect to login activity
     */
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Load user profile from Firestore and display welcome message
     */
    private fun loadUserProfile() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userName = document.getString("name") ?: currentUser.displayName ?: "User"
                        welcomeTextView.text = "Welcome, $userName! 👋"
                    } else {
                        welcomeTextView.text = "Welcome, ${currentUser.displayName ?: "User"}! 👋"
                    }
                }
                .addOnFailureListener {
                    // Fallback to Firebase Auth display name
                    welcomeTextView.text = "Welcome, ${currentUser.displayName ?: "User"}! 👋"
                }
        }
    }

    /**
     * Initialize all UI views
     */
    private fun initializeViews() {
        welcomeTextView = findViewById(R.id.welcomeTextView)
        contactsContainer = findViewById(R.id.contactsContainer)
        addContactButton = findViewById(R.id.addContactButton)
        saveContactsButton = findViewById(R.id.saveContactsButton)
        sosButton = findViewById(R.id.sosButton)
        stopSosButton = findViewById(R.id.stopSosButton)
        statusText = findViewById(R.id.statusText)

        // Initially hide stop button
        stopSosButton.visibility = View.GONE
    }

    /**
     * Initialize location services and callback
     */
    private fun initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Create location request for high accuracy updates
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, sosUpdateInterval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(15000L)
            .setMaxUpdateDelayMillis(sosUpdateInterval)
            .build()

        // Location callback for continuous updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    if (isSosActive) {
                        sendSosMessage(location)
                        statusText.text = "SOS Active - Location sent: ${location.latitude}, ${location.longitude}"
                    }
                }
            }
        }
    }

    /**
     * Initialize SharedPreferences for contact storage
     */
    private fun initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences("SOS_CONTACTS", Context.MODE_PRIVATE)
    }

    /**
     * Load saved contacts from SharedPreferences
     */
    private fun loadSavedContacts() {
        val savedContacts = sharedPreferences.getStringSet("contacts", emptySet()) ?: emptySet()
        contactsList.clear()
        contactsList.addAll(savedContacts)

        // If no contacts exist, add 3 empty fields
        if (contactsList.isEmpty()) {
            repeat(3) { contactsList.add("") }
        }

        refreshContactsUI()
    }

    /**
     * Setup click listeners for all buttons
     */
    private fun setupClickListeners() {
        addContactButton.setOnClickListener { addNewContact() }
        saveContactsButton.setOnClickListener { saveContacts() }
        sosButton.setOnClickListener { startSOS() }
        stopSosButton.setOnClickListener { stopSOS() }
    }

    /**
     * Create options menu with logout option
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Handle options menu item selection
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                showUserProfile()
                true
            }
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Show user profile information
     */
    private fun showUserProfile() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val message = """
                Name: ${currentUser.displayName ?: "Not set"}
                Email: ${currentUser.email}
                Email Verified: ${if (currentUser.isEmailVerified) "✅ Yes" else "❌ No"}
                Account Created: ${android.text.format.DateFormat.getDateFormat(this).format(java.util.Date(currentUser.metadata?.creationTimestamp ?: 0))}
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("👤 Profile Information")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNeutralButton("Edit Profile") { _, _ ->
                    // TODO: Navigate to profile editing activity
                    Toast.makeText(this, "Profile editing coming soon", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    /**
     * Show logout confirmation dialog
     */
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Perform logout operation
     */
    private fun performLogout() {
        // Stop any active SOS session
        if (isSosActive) {
            stopSOS()
        }

        // Sign out from Firebase
        firebaseAuth.signOut()

        // Clear any cached data
        sharedPreferences.edit().clear().apply()

        // Show success message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Redirect to login activity
        redirectToLogin()
    }

    /**
     * Refresh the contacts UI display
     */
    private fun refreshContactsUI() {
        contactsContainer.removeAllViews()

        for (i in contactsList.indices) {
            val contactLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }

            val editText = EditText(this).apply {
                hint = "Contact ${i + 1} (Phone Number)"
                setText(contactsList[i])
                inputType = InputType.TYPE_CLASS_PHONE
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(16, 16, 16, 16)
            }

            val deleteButton = Button(this).apply {
                text = "Delete"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    if (contactsList.size > 3) {
                        removeContact(i)
                    } else {
                        Toast.makeText(this@MainActivity, "Minimum 3 contacts required", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            contactLayout.addView(editText)
            contactLayout.addView(deleteButton)
            contactsContainer.addView(contactLayout)
        }
    }

    /**
     * Add a new contact field
     */
    private fun addNewContact() {
        contactsList.add("")
        refreshContactsUI()
    }

    /**
     * Remove contact at specific index
     */
    private fun removeContact(index: Int) {
        if (contactsList.size > 3) {
            contactsList.removeAt(index)
            refreshContactsUI()
        }
    }

    /**
     * Save all contacts to SharedPreferences and optionally to Firestore
     */
    private fun saveContacts() {
        val validContacts = mutableSetOf<String>()

        for (i in 0 until contactsContainer.childCount) {
            val layout = contactsContainer.getChildAt(i) as LinearLayout
            val editText = layout.getChildAt(0) as EditText
            val contact = editText.text.toString().trim()

            if (contact.isNotEmpty() && isValidPhoneNumber(contact)) {
                validContacts.add(contact)
            }
        }

        if (validContacts.size < 3) {
            Toast.makeText(this, "Please add at least 3 valid contacts", Toast.LENGTH_LONG).show()
            return
        }

        // Save to SharedPreferences
        sharedPreferences.edit()
            .putStringSet("contacts", validContacts)
            .apply()

        // Save to Firestore for backup
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val contactsData = hashMapOf(
                "contacts" to validContacts.toList(),
                "lastUpdated" to java.util.Date()
            )

            firestore.collection("users").document(currentUser.uid)
                .update(contactsData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Contacts saved successfully! (Local + Cloud)", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Contacts saved locally", Toast.LENGTH_SHORT).show()
                }
        }

        contactsList.clear()
        contactsList.addAll(validContacts)
    }

    /**
     * Validate phone number format
     */
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length >= 10 && phoneNumber.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
    }

    /**
     * Start SOS emergency mode with location tracking AND audio recording
     */
    private fun startSOS() {
        // Check all required permissions
        if (!hasLocationPermission() || !hasSmsPermission() || !hasAudioPermission()) {
            Toast.makeText(this, "Location, SMS, and Audio permissions required for full SOS functionality", Toast.LENGTH_LONG).show()
            requestAllPermissions()
            return
        }

        if (contactsList.isEmpty() || contactsList.all { it.isEmpty() }) {
            Toast.makeText(this, "Please add and save contacts first", Toast.LENGTH_LONG).show()
            return
        }

        // Start SOS mode
        isSosActive = true
        sosButton.visibility = View.GONE
        stopSosButton.visibility = View.VISIBLE
        statusText.text = "🆘 SOS Activated - Getting location & starting recording..."

        // Start location updates
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            stopSOS()
            return
        }

        // Start audio recording automatically
        RecorderActivity.startRecordingFromSOS(this)

        Toast.makeText(this, "🆘 SOS Activated: Location tracking + Audio recording started", Toast.LENGTH_LONG).show()
    }

    /**
     * Stop SOS emergency mode and audio recording
     */
    private fun stopSOS() {
        isSosActive = false
        sosButton.visibility = View.VISIBLE
        stopSosButton.visibility = View.GONE
        statusText.text = "SOS Stopped"

        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Cancel any pending SOS updates
        sosHandler?.removeCallbacks(sosRunnable ?: return)

        // Stop audio recording
        RecorderActivity.stopGlobalRecording()

        Toast.makeText(this, "🛑 SOS Stopped: Location tracking + Audio recording ended", Toast.LENGTH_SHORT).show()
    }

    /**
     * Send SOS message with current location to all contacts
     */
    private fun sendSosMessage(location: Location) {
        val mapsUrl = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
        val recordingStatus = if (RecorderActivity.isRecordingActive()) "Audio recording: ACTIVE" else "Audio recording: INACTIVE"
        val currentUser = firebaseAuth.currentUser
        val userName = currentUser?.displayName ?: "Someone"

        val message = """
            🚨 EMERGENCY ALERT 🚨
            
            $userName is in danger and needs help!
            
            Location: $mapsUrl
            $recordingStatus
            
            Time: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}
            
            This is an automated SOS alert.
        """.trimIndent()

        @Suppress("DEPRECATION")
        val smsManager = SmsManager.getDefault()

        for (contact in contactsList) {
            if (contact.isNotEmpty()) {
                try {
                    // Split message if it's too long
                    val parts = smsManager.divideMessage(message)
                    smsManager.sendMultipartTextMessage(contact, null, parts, null, null)
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to send SMS to $contact", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Toast.makeText(this, "SOS message sent to ${contactsList.size} contacts", Toast.LENGTH_SHORT).show()
    }

    /**
     * Request all necessary permissions including audio recording
     */
    private fun requestAllPermissions() {
        val permissions = mutableListOf<String>()

        if (!hasLocationPermission()) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (!hasSmsPermission()) {
            permissions.add(Manifest.permission.SEND_SMS)
        }

        if (!hasAudioPermission()) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), allPermissionsCode)
        }
    }

    /**
     * Request necessary permissions (legacy method for backward compatibility)
     */
    private fun requestPermissions() {
        requestAllPermissions()
    }

    /**
     * Check if location permission is granted
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if SMS permission is granted
     */
    private fun hasSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if audio recording permission is granted
     */
    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Handle permission request results
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            locationPermissionCode, smsPermissionCode, allPermissionsCode -> {
                val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (allGranted) {
                    Toast.makeText(this, "✅ All permissions granted - SOS fully functional", Toast.LENGTH_SHORT).show()
                } else {
                    val deniedPermissions = mutableListOf<String>()
                    permissions.forEachIndexed { index, permission ->
                        if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                            when (permission) {
                                Manifest.permission.ACCESS_FINE_LOCATION -> deniedPermissions.add("Location")
                                Manifest.permission.SEND_SMS -> deniedPermissions.add("SMS")
                                Manifest.permission.RECORD_AUDIO -> deniedPermissions.add("Audio Recording")
                            }
                        }
                    }
                    Toast.makeText(this, "⚠️ Missing permissions: ${deniedPermissions.joinToString(", ")}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Stop location updates when activity is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        if (isSosActive) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        sosHandler?.removeCallbacks(sosRunnable ?: return)
    }

    /**
     * Handle activity pause - keep SOS running in background
     */
    override fun onPause() {
        super.onPause()
        // Keep location updates running even when app is in background during SOS
    }

    /**
     * Handle activity resume
     */
    override fun onResume() {
        super.onResume()
        if (isSosActive && hasLocationPermission()) {
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            } catch (e: SecurityException) {
                stopSOS()
            }
        }

        // Update status to reflect recording state
        updateStatusWithRecordingInfo()
    }

    /**
     * Update status text to include recording information
     */
    private fun updateStatusWithRecordingInfo() {
        if (isSosActive) {
            val recordingStatus = if (RecorderActivity.isRecordingActive()) "📹 Recording" else "🔇 No Recording"
            statusText.text = "🆘 SOS Active - $recordingStatus"
        }
    }
}