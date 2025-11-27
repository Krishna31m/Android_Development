package com.example.healthreminder.ui.mood

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.example.healthreminder.data.model.MoodEntry
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MoodTrackerActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvDate: TextView
    private lateinit var btnHappy: MaterialButton
    private lateinit var btnSad: MaterialButton
    private lateinit var btnAnxious: MaterialButton
    private lateinit var btnCalm: MaterialButton
    private lateinit var btnEnergetic: MaterialButton
    private lateinit var etNote: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var tvCurrentMood: TextView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var selectedMood = ""
    private var selectedEmoji = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_tracker)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Display date
        displayDate()

        // Setup mood buttons
        setupMoodButtons()

        // Load today's mood
        loadTodaysMood()

        // Save button
        btnSave.setOnClickListener {
            saveMood()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        tvDate = findViewById(R.id.tv_date)
        btnHappy = findViewById(R.id.btn_happy)
        btnSad = findViewById(R.id.btn_sad)
        btnAnxious = findViewById(R.id.btn_anxious)
        btnCalm = findViewById(R.id.btn_calm)
        btnEnergetic = findViewById(R.id.btn_energetic)
        etNote = findViewById(R.id.et_note)
        btnSave = findViewById(R.id.btn_save)
        tvCurrentMood = findViewById(R.id.tv_current_mood)
    }

    private fun displayDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(Date())
    }

    private fun setupMoodButtons() {
        btnHappy.setOnClickListener {
            selectMood("Happy", "😊", btnHappy)
        }

        btnSad.setOnClickListener {
            selectMood("Sad", "😢", btnSad)
        }

        btnAnxious.setOnClickListener {
            selectMood("Anxious", "😰", btnAnxious)
        }

        btnCalm.setOnClickListener {
            selectMood("Calm", "😌", btnCalm)
        }

        btnEnergetic.setOnClickListener {
            selectMood("Energetic", "⚡", btnEnergetic)
        }
    }

    private fun selectMood(mood: String, emoji: String, button: MaterialButton) {
        selectedMood = mood
        selectedEmoji = emoji

        // Reset all buttons
        val buttons = listOf(btnHappy, btnSad, btnAnxious, btnCalm, btnEnergetic)
        buttons.forEach { it.strokeWidth = 0 }

        // Highlight selected button
        button.strokeWidth = 4

        tvCurrentMood.text = "Selected: $emoji $mood"
    }

    private fun saveMood() {
        if (selectedMood.isEmpty()) {
            Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show()
            return
        }

        val note = etNote.text.toString().trim()
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val moodEntry = MoodEntry(
            id = today,
            userId = userId,
            date = today,
            mood = selectedMood,
            emoji = selectedEmoji,
            note = note,
            timestamp = Timestamp.now()
        )

        firestore.collection("users")
            .document(userId)
            .collection("mood")
            .document(today)
            .set(moodEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Mood saved! $selectedEmoji", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTodaysMood() {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        firestore.collection("users")
            .document(userId)
            .collection("mood")
            .document(today)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val mood = document.getString("mood") ?: ""
                    val emoji = document.getString("emoji") ?: ""
                    val note = document.getString("note") ?: ""

                    selectedMood = mood
                    selectedEmoji = emoji
                    etNote.setText(note)
                    tvCurrentMood.text = "Today's Mood: $emoji $mood"

                    // Highlight the selected button
                    when (mood) {
                        "Happy" -> selectMood(mood, emoji, btnHappy)
                        "Sad" -> selectMood(mood, emoji, btnSad)
                        "Anxious" -> selectMood(mood, emoji, btnAnxious)
                        "Calm" -> selectMood(mood, emoji, btnCalm)
                        "Energetic" -> selectMood(mood, emoji, btnEnergetic)
                    }
                }
            }
    }
}