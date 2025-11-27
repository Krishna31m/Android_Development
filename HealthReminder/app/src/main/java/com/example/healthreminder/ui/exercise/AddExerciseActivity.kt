package com.example.healthreminder.ui.exercise

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Exercise
import com.example.healthreminder.utils.AlarmScheduler
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddExerciseActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etExerciseName: TextInputEditText
    private lateinit var acExerciseType: AutoCompleteTextView
    private lateinit var etDuration: TextInputEditText
    private lateinit var btnSelectTime: MaterialButton
    private lateinit var switchReminder: SwitchMaterial
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDelete: MaterialButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var alarmScheduler: AlarmScheduler

    private var selectedTime = ""
    private var exerciseId: String? = null
    private val selectedDays = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        alarmScheduler = AlarmScheduler(this)

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup exercise type dropdown
        setupExerciseTypeDropdown()

        // Check if editing
        exerciseId = intent.getStringExtra("EXERCISE_ID")
        if (exerciseId != null) {
            toolbar.title = "Edit Exercise"
            btnDelete.visibility = View.VISIBLE
            loadExerciseData(exerciseId!!)
        }

        // Setup listeners
        setupListeners()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        etExerciseName = findViewById(R.id.et_exercise_name)
        acExerciseType = findViewById(R.id.ac_exercise_type)
        etDuration = findViewById(R.id.et_duration)
        btnSelectTime = findViewById(R.id.btn_select_time)
        switchReminder = findViewById(R.id.switch_reminder)
        btnSave = findViewById(R.id.btn_save)
        btnDelete = findViewById(R.id.btn_delete)
    }

    private fun setupExerciseTypeDropdown() {
        val exerciseTypes = arrayOf("Gym", "Yoga", "Running", "Walking", "Cycling",
            "Swimming", "Dance", "Sports", "Cardio", "Strength Training", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, exerciseTypes)
        acExerciseType.setAdapter(adapter)
    }

    private fun setupListeners() {
        btnSelectTime.setOnClickListener {
            showTimePicker()
        }

        // Setup day chips
        setupDayChips()

        btnSave.setOnClickListener {
            saveExercise()
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun setupDayChips() {
        val dayChips = listOf(
            findViewById<Chip>(R.id.chip_monday),
            findViewById<Chip>(R.id.chip_tuesday),
            findViewById<Chip>(R.id.chip_wednesday),
            findViewById<Chip>(R.id.chip_thursday),
            findViewById<Chip>(R.id.chip_friday),
            findViewById<Chip>(R.id.chip_saturday),
            findViewById<Chip>(R.id.chip_sunday)
        )

        dayChips.forEachIndexed { index, chip ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                val day = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[index]
                if (isChecked) {
                    if (!selectedDays.contains(day)) selectedDays.add(day)
                } else {
                    selectedDays.remove(day)
                }
            }
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            btnSelectTime.text = selectedTime
        }, hour, minute, true).show()
    }

    private fun saveExercise() {
        val name = etExerciseName.text.toString().trim()
        val type = acExerciseType.text.toString().trim()
        val durationStr = etDuration.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            etExerciseName.error = "Exercise name is required"
            etExerciseName.requestFocus()
            return
        }

        if (type.isEmpty()) {
            acExerciseType.error = "Exercise type is required"
            acExerciseType.requestFocus()
            return
        }

        if (durationStr.isEmpty()) {
            etDuration.error = "Duration is required"
            etDuration.requestFocus()
            return
        }

        val duration = durationStr.toIntOrNull()
        if (duration == null || duration <= 0) {
            etDuration.error = "Enter valid duration in minutes"
            etDuration.requestFocus()
            return
        }

        if (duration > 480) {
            etDuration.error = "Duration cannot exceed 480 minutes"
            etDuration.requestFocus()
            return
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDays.isEmpty()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val docId = exerciseId ?: firestore.collection("users")
            .document(userId)
            .collection("exercises")
            .document().id

        val exercise = Exercise(
            id = docId,
            userId = userId,
            name = name,
            type = type,
            duration = duration,
            time = selectedTime,
            days = selectedDays.toList(),
            reminderEnabled = switchReminder.isChecked,
            isActive = true,
            createdAt = Timestamp.now()
        )

        Log.d("AddExercise", "Saving exercise: $exercise")

        firestore.collection("users")
            .document(userId)
            .collection("exercises")
            .document(docId)
            .set(exercise)
            .addOnSuccessListener {
                Log.d("AddExercise", "Exercise saved successfully: $docId")

                // Schedule alarm
                if (switchReminder.isChecked) {
                    alarmScheduler.scheduleExerciseReminder(docId, name, selectedTime, selectedDays.toList())
                }

                Toast.makeText(this, "Exercise saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("AddExercise", "Error saving exercise", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Exercise")
            .setMessage("Are you sure you want to delete this exercise?")
            .setPositiveButton("Delete") { _, _ ->
                deleteExercise()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Add this to your AddExerciseActivity.kt

    private fun deleteExercise() {
        val userId = auth.currentUser?.uid ?: return
        exerciseId?.let { id ->
            firestore.collection("users")
                .document(userId)
                .collection("exercises")
                .document(id)
                .update("active", false)  // ✅ FIXED: Changed from "isActive" to "active"
                .addOnSuccessListener {
                    alarmScheduler.cancelAlarm(id.hashCode(),
                        com.example.healthreminder.receivers.ExerciseAlarmReceiver::class.java)

                    Toast.makeText(this, "Exercise deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadExerciseData(exerciseId: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("exercises")
            .document(exerciseId)
            .get()
            .addOnSuccessListener { document ->
                val exercise = document.toObject(Exercise::class.java) ?: return@addOnSuccessListener

                etExerciseName.setText(exercise.name)
                acExerciseType.setText(exercise.type, false)
                etDuration.setText(exercise.duration.toString())
                selectedTime = exercise.time
                btnSelectTime.text = selectedTime
                switchReminder.isChecked = exercise.reminderEnabled

                selectedDays.clear()
                selectedDays.addAll(exercise.days)

                exercise.days.forEach { day ->
                    val chipId = when (day) {
                        "Mon" -> R.id.chip_monday
                        "Tue" -> R.id.chip_tuesday
                        "Wed" -> R.id.chip_wednesday
                        "Thu" -> R.id.chip_thursday
                        "Fri" -> R.id.chip_friday
                        "Sat" -> R.id.chip_saturday
                        "Sun" -> R.id.chip_sunday
                        else -> null
                    }
                    chipId?.let { findViewById<Chip>(it).isChecked = true }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AddExercise", "Error loading exercise", e)
                Toast.makeText(this, "Error loading exercise", Toast.LENGTH_SHORT).show()
            }
    }
}
