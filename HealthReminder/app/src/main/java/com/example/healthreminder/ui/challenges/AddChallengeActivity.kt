package com.example.healthreminder.ui.challenges

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.example.healthreminder.data.model.HealthChallenge
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddChallengeActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etChallengeName: TextInputEditText
    private lateinit var acChallengeType: AutoCompleteTextView
    private lateinit var acDuration: AutoCompleteTextView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDelete: MaterialButton
    private lateinit var btnMarkComplete: MaterialButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var challengeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_challenge)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup dropdowns
        setupDropdowns()

        // Check if editing
        challengeId = intent.getStringExtra("CHALLENGE_ID")
        if (challengeId != null) {
            toolbar.title = "Challenge Details"
            btnDelete.visibility = View.VISIBLE
            btnMarkComplete.visibility = View.VISIBLE
            loadChallengeData(challengeId!!)
        }

        // Setup listeners
        setupListeners()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        etChallengeName = findViewById(R.id.et_challenge_name)
        acChallengeType = findViewById(R.id.ac_challenge_type)
        acDuration = findViewById(R.id.ac_duration)
        btnSave = findViewById(R.id.btn_save)
        btnDelete = findViewById(R.id.btn_delete)
        btnMarkComplete = findViewById(R.id.btn_mark_complete)
    }

    private fun setupDropdowns() {
        // Challenge types
        val challengeTypes = arrayOf(
            "Water Challenge",
            "Walk Challenge",
            "Exercise Challenge",
            "Sugar Free Challenge",
            "Meditation Challenge",
            "Sleep Challenge",
            "Custom"
        )
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, challengeTypes)
        acChallengeType.setAdapter(typeAdapter)

        // Duration options
        val durations = arrayOf("7 Days", "15 Days", "21 Days", "30 Days", "60 Days", "90 Days")
        val durationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, durations)
        acDuration.setAdapter(durationAdapter)
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveChallenge()
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        btnMarkComplete.setOnClickListener {
            markDayComplete()
        }
    }

    private fun saveChallenge() {
        val name = etChallengeName.text.toString().trim()
        val type = acChallengeType.text.toString().trim()
        val durationStr = acDuration.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            etChallengeName.error = "Challenge name is required"
            etChallengeName.requestFocus()
            return
        }

        if (type.isEmpty()) {
            acChallengeType.error = "Challenge type is required"
            acChallengeType.requestFocus()
            return
        }

        if (durationStr.isEmpty()) {
            acDuration.error = "Duration is required"
            acDuration.requestFocus()
            return
        }

        val duration = durationStr.replace(" Days", "").toIntOrNull() ?: 0

        val userId = auth.currentUser?.uid ?: return
        val docId = challengeId ?: firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .document().id

        val challenge = HealthChallenge(
            id = docId,
            userId = userId,
            type = type,
            name = name,
            duration = duration,
            startDate = Timestamp.now(),
            currentStreak = 0,
            bestStreak = 0,
            isActive = true,
            completedDays = listOf(),
            createdAt = Timestamp.now()
        )

        firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .document(docId)
            .set(challenge)
            .addOnSuccessListener {
                Toast.makeText(this, "Challenge saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markDayComplete() {
        val userId = auth.currentUser?.uid ?: return
        challengeId?.let { id ->
            firestore.collection("users")
                .document(userId)
                .collection("challenges")
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    val challenge = document.toObject(HealthChallenge::class.java) ?: return@addOnSuccessListener

                    val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(java.util.Date())

                    if (!challenge.completedDays.contains(today)) {
                        val updatedDays = challenge.completedDays.toMutableList()
                        updatedDays.add(today)

                        val newStreak = challenge.currentStreak + 1
                        val newBestStreak = maxOf(newStreak, challenge.bestStreak)

                        firestore.collection("users")
                            .document(userId)
                            .collection("challenges")
                            .document(id)
                            .update(
                                mapOf(
                                    "completedDays" to updatedDays,
                                    "currentStreak" to newStreak,
                                    "bestStreak" to newBestStreak
                                )
                            )
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "🎉 Day completed! Streak: $newStreak",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadChallengeData(id)
                            }
                    } else {
                        Toast.makeText(this, "Already completed today!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Challenge")
            .setMessage("Are you sure you want to delete this challenge?")
            .setPositiveButton("Delete") { _, _ ->
                deleteChallenge()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteChallenge() {
        val userId = auth.currentUser?.uid ?: return
        challengeId?.let { id ->
            firestore.collection("users")
                .document(userId)
                .collection("challenges")
                .document(id)
                .update("isActive", false)
                .addOnSuccessListener {
                    Toast.makeText(this, "Challenge deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadChallengeData(challengeId: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .document(challengeId)
            .get()
            .addOnSuccessListener { document ->
                val challenge = document.toObject(HealthChallenge::class.java) ?: return@addOnSuccessListener

                etChallengeName.setText(challenge.name)
                acChallengeType.setText(challenge.type, false)
                acDuration.setText("${challenge.duration} Days", false)

                // Update button text with streak info
                btnMarkComplete.text = "Mark Today Complete (Streak: ${challenge.currentStreak})"
            }
    }
}