package com.example.healthreminder.ui.diet

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Meal
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddMealActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var acMealType: AutoCompleteTextView
    private lateinit var etDescription: TextInputEditText
    private lateinit var etCalories: TextInputEditText
    private lateinit var btnSelectTime: MaterialButton
    private lateinit var switchReminder: SwitchMaterial
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDelete: MaterialButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var selectedTime = ""
    private var mealId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup meal type dropdown
        setupMealTypeDropdown()

        // Check if editing
        mealId = intent.getStringExtra("MEAL_ID")
        if (mealId != null) {
            toolbar.title = "Edit Meal"
            btnDelete.visibility = View.VISIBLE
            loadMealData(mealId!!)
        }

        // Setup listeners
        setupListeners()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        acMealType = findViewById(R.id.ac_meal_type)
        etDescription = findViewById(R.id.et_description)
        etCalories = findViewById(R.id.et_calories)
        btnSelectTime = findViewById(R.id.btn_select_time)
        switchReminder = findViewById(R.id.switch_reminder)
        btnSave = findViewById(R.id.btn_save)
        btnDelete = findViewById(R.id.btn_delete)
    }

    private fun setupMealTypeDropdown() {
        val mealTypes = arrayOf("Breakfast", "Lunch", "Dinner", "Snack")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mealTypes)
        acMealType.setAdapter(adapter)
    }

    private fun setupListeners() {
        btnSelectTime.setOnClickListener {
            showTimePicker()
        }

        btnSave.setOnClickListener {
            saveMeal()
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmation()
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

    private fun saveMeal() {
        val mealType = acMealType.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val caloriesStr = etCalories.text.toString().trim()

        // Validation
        if (mealType.isEmpty()) {
            acMealType.error = "Meal type is required"
            acMealType.requestFocus()
            return
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return
        }

        val calories = if (caloriesStr.isNotEmpty()) {
            caloriesStr.toIntOrNull() ?: 0
        } else {
            0
        }

        val userId = auth.currentUser?.uid ?: return
        val docId = mealId ?: firestore.collection("users")
            .document(userId)
            .collection("meals")
            .document().id

        val meal = Meal(
            id = docId,
            userId = userId,
            mealType = mealType,
            time = selectedTime,
            calories = calories,
            description = description,
            reminderEnabled = switchReminder.isChecked,
            createdAt = Timestamp.now()
        )

        firestore.collection("users")
            .document(userId)
            .collection("meals")
            .document(docId)
            .set(meal)
            .addOnSuccessListener {
                Toast.makeText(this, "Meal saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Meal")
            .setMessage("Are you sure you want to delete this meal?")
            .setPositiveButton("Delete") { _, _ ->
                deleteMeal()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMeal() {
        val userId = auth.currentUser?.uid ?: return
        mealId?.let { id ->
            firestore.collection("users")
                .document(userId)
                .collection("meals")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Meal deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadMealData(mealId: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("meals")
            .document(mealId)
            .get()
            .addOnSuccessListener { document ->
                val meal = document.toObject(Meal::class.java) ?: return@addOnSuccessListener

                acMealType.setText(meal.mealType, false)
                etDescription.setText(meal.description)
                etCalories.setText(meal.calories.toString())
                selectedTime = meal.time
                btnSelectTime.text = selectedTime
                switchReminder.isChecked = meal.reminderEnabled
            }
    }
}