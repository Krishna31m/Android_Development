package com.krishna.soslocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecorderActivity : AppCompatActivity() {

    // UI Components
    private lateinit var recordingStatusText: TextView
    private lateinit var stopRecordingButton: Button
    private lateinit var recordingTimeText: TextView

    // Audio Recording Components
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var recordingFilePath: String? = null
    private var recordingStartTime: Long = 0

    // Timer for recording duration display
    private val recordingHandler = Handler(Looper.getMainLooper())
    private var recordingRunnable: Runnable? = null

    // Permission request code
    private val audioPermissionCode = 200

    // Static companion object for managing recording state across activities
    companion object {
        private var globalRecordingInstance: RecorderActivity? = null
        private var isGloballyRecording = false

        /**
         * Start recording from any activity (called from SOS button)
         * This prevents duplicate recordings and manages global state
         */
        fun startRecordingFromSOS(context: Context) {
            if (isGloballyRecording) {
                Toast.makeText(context, "Recording already in progress", Toast.LENGTH_SHORT).show()
                return
            }

            val intent = Intent(context, RecorderActivity::class.java)
            intent.putExtra("AUTO_START_RECORDING", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }

        /**
         * Check if recording is currently active globally
         */
        fun isRecordingActive(): Boolean = isGloballyRecording

        /**
         * Stop recording from external activity if needed
         */
        fun stopGlobalRecording() {
            globalRecordingInstance?.stopRecording()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)

        // Set global instance reference
        globalRecordingInstance = this

        initializeViews()
        setupClickListeners()

        // Check if we should auto-start recording (called from SOS button)
        val autoStart = intent.getBooleanExtra("AUTO_START_RECORDING", false)
        if (autoStart && !isRecording) {
            requestAudioPermissionAndStart()
        }

        // Update UI based on current state
        updateUI()
    }

    /**
     * Initialize all UI components
     */
    private fun initializeViews() {
        recordingStatusText = findViewById(R.id.recordingStatusText)
        stopRecordingButton = findViewById(R.id.stopRecordingButton)
        recordingTimeText = findViewById(R.id.recordingTimeText)
    }

    /**
     * Setup click listeners for buttons
     */
    private fun setupClickListeners() {
        stopRecordingButton.setOnClickListener {
            stopRecording()
        }
    }

    /**
     * Request audio recording permission and start recording
     */
    private fun requestAudioPermissionAndStart() {
        if (hasAudioPermission()) {
            startRecording()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                audioPermissionCode
            )
        }
    }

    /**
     * Check if audio recording permission is granted
     */
    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Start audio recording
     */
    private fun startRecording() {
        if (isRecording) {
            Toast.makeText(this, "Recording already in progress", Toast.LENGTH_SHORT).show()
            return
        }

        if (!hasAudioPermission()) {
            Toast.makeText(this, "Audio permission required", Toast.LENGTH_LONG).show()
            return
        }

        try {
            // Create recordings directory
            val recordingsDir = createRecordingsDirectory()
            if (recordingsDir == null) {
                Toast.makeText(this, "Failed to create recordings directory", Toast.LENGTH_LONG).show()
                return
            }

            // Generate unique filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "SOS_Recording_$timestamp.3gp"
            recordingFilePath = File(recordingsDir, fileName).absolutePath

            // Initialize MediaRecorder
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(recordingFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                try {
                    prepare()
                    start()

                    // Update state
                    isRecording = true
                    isGloballyRecording = true
                    recordingStartTime = System.currentTimeMillis()

                    // Start timer for recording duration
                    startRecordingTimer()

                    // Update UI
                    updateUI()

                    // Show success message
                    Toast.makeText(this@RecorderActivity, "🎙️ Recording started", Toast.LENGTH_SHORT).show()

                } catch (e: IOException) {
                    Toast.makeText(this@RecorderActivity, "Failed to start recording: ${e.message}", Toast.LENGTH_LONG).show()
                    releaseMediaRecorder()
                } catch (e: IllegalStateException) {
                    Toast.makeText(this@RecorderActivity, "Recording error: ${e.message}", Toast.LENGTH_LONG).show()
                    releaseMediaRecorder()
                }
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing recorder: ${e.message}", Toast.LENGTH_LONG).show()
            releaseMediaRecorder()
        }
    }

    /**
     * Stop audio recording
     */
    private fun stopRecording() {
        if (!isRecording) {
            Toast.makeText(this, "No recording in progress", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null

            // Update state
            isRecording = false
            isGloballyRecording = false

            // Stop timer
            stopRecordingTimer()

            // Update UI
            updateUI()

            // Show success message with file location
            val fileName = recordingFilePath?.substringAfterLast("/") ?: "Unknown"
            Toast.makeText(this, "✅ Recording saved: $fileName", Toast.LENGTH_LONG).show()

        } catch (e: RuntimeException) {
            Toast.makeText(this, "Error stopping recording: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            releaseMediaRecorder()
        }
    }

    /**
     * Create recordings directory in internal storage
     */
    private fun createRecordingsDirectory(): File? {
        return try {
            val recordingsDir = File(filesDir, "SOSRecordings")
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs()
            }
            recordingsDir
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Start timer to display recording duration
     */
    private fun startRecordingTimer() {
        recordingRunnable = object : Runnable {
            override fun run() {
                if (isRecording) {
                    val elapsedTime = System.currentTimeMillis() - recordingStartTime
                    val seconds = (elapsedTime / 1000) % 60
                    val minutes = (elapsedTime / 1000) / 60
                    recordingTimeText.text = String.format("Recording: %02d:%02d", minutes, seconds)
                    recordingHandler.postDelayed(this, 1000)
                }
            }
        }
        recordingHandler.post(recordingRunnable!!)
    }

    /**
     * Stop recording timer
     */
    private fun stopRecordingTimer() {
        recordingRunnable?.let { recordingHandler.removeCallbacks(it) }
        recordingRunnable = null
    }

    /**
     * Update UI based on recording state
     */
    private fun updateUI() {
        if (isRecording) {
            recordingStatusText.text = "🎙️ Recording in Progress"
            recordingStatusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            stopRecordingButton.isEnabled = true
            stopRecordingButton.text = "⏹️ Stop Recording"
        } else {
            recordingStatusText.text = "🔇 Recording Stopped"
            recordingStatusText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            recordingTimeText.text = "Ready to record"
            stopRecordingButton.isEnabled = false
            stopRecordingButton.text = "⏹️ Stop Recording"
        }
    }

    /**
     * Release MediaRecorder resources
     */
    private fun releaseMediaRecorder() {
        mediaRecorder?.apply {
            try {
                release()
            } catch (e: Exception) {
                // Ignore release errors
            }
        }
        mediaRecorder = null
        isRecording = false
        isGloballyRecording = false
    }

    /**
     * Handle permission request results
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            audioPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Audio permission granted", Toast.LENGTH_SHORT).show()
                    startRecording()
                } else {
                    Toast.makeText(this, "Audio permission denied. Recording not available.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Handle activity lifecycle - continue recording in background
     */
    override fun onPause() {
        super.onPause()
        // Recording continues in background
        if (isRecording) {
            Toast.makeText(this, "Recording continues in background", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handle activity resume - update UI state
     */
    override fun onResume() {
        super.onResume()
        updateUI()
        if (isRecording) {
            startRecordingTimer() // Restart timer display
        }
    }

    /**
     * Handle back button - warn user about ongoing recording
     */
    override fun onBackPressed() {
        if (isRecording) {
            Toast.makeText(this, "Recording continues in background. Use Stop button to end.", Toast.LENGTH_LONG).show()
        }
        super.onBackPressed()
    }

    /**
     * Clean up resources when activity is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()

        // Only release resources if activity is being destroyed permanently
        if (isFinishing) {
            releaseMediaRecorder()
            stopRecordingTimer()
        }

        // Clear global reference if this is the active instance
        if (globalRecordingInstance == this) {
            globalRecordingInstance = null
        }
    }

    /**
     * Get list of all recorded files for display or management
     */
    private fun getRecordedFiles(): List<File> {
        val recordingsDir = File(filesDir, "SOSRecordings")
        return if (recordingsDir.exists()) {
            recordingsDir.listFiles()?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Delete a specific recording file
     */
    private fun deleteRecording(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get total size of all recordings
     */
    private fun getTotalRecordingSize(): Long {
        return getRecordedFiles().sumOf { it.length() }
    }
}