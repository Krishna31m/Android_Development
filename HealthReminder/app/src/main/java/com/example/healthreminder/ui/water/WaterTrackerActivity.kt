package com.example.healthreminder.ui.water

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.example.healthreminder.data.model.WaterIntake
import com.example.healthreminder.data.model.WaterLog
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WaterTrackerActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvGoal: TextView
    private lateinit var tvConsumed: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView
    private lateinit var btn250ml: MaterialButton
    private lateinit var btn500ml: MaterialButton
    private lateinit var btn1000ml: MaterialButton
    private lateinit var btnSetGoal: MaterialButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var currentGoal = 3000 // Default 3L in ml
    private var currentConsumed = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_tracker)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup click listeners
        setupClickListeners()

        // Load today's water intake
        loadWaterIntake()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        tvGoal = findViewById(R.id.tv_goal)
        tvConsumed = findViewById(R.id.tv_consumed)
        tvRemaining = findViewById(R.id.tv_remaining)
        progressBar = findViewById(R.id.progress_water)
        tvProgress = findViewById(R.id.tv_progress_percent)
        btn250ml = findViewById(R.id.btn_250ml)
        btn500ml = findViewById(R.id.btn_500ml)
        btn1000ml = findViewById(R.id.btn_1000ml)
        btnSetGoal = findViewById(R.id.btn_set_goal)
    }

    private fun setupClickListeners() {
        btn250ml.setOnClickListener { addWater(250) }
        btn500ml.setOnClickListener { addWater(500) }
        btn1000ml.setOnClickListener { addWater(1000) }

        btnSetGoal.setOnClickListener {
            // Show dialog to set custom goal
            showSetGoalDialog()
        }
    }

    private fun loadWaterIntake() {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        firestore.collection("users")
            .document(userId)
            .collection("waterIntake")
            .document(today)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentGoal = document.getLong("goal")?.toInt() ?: 3000
                    currentConsumed = document.getLong("consumed")?.toInt() ?: 0
                } else {
                    // Create today's entry
                    val waterIntake = WaterIntake(
                        id = today,
                        userId = userId,
                        date = today,
                        goal = currentGoal,
                        consumed = currentConsumed
                    )
                    firestore.collection("users")
                        .document(userId)
                        .collection("waterIntake")
                        .document(today)
                        .set(waterIntake)
                }
                updateUI()
            }
    }

    private fun addWater(amount: Int) {
        currentConsumed += amount

        if (currentConsumed > currentGoal) {
            currentConsumed = currentGoal
        }

        saveWaterIntake(amount)
        updateUI()

        // Show congratulations if goal reached
        if (currentConsumed >= currentGoal) {
            Toast.makeText(this, "🎉 Great! You've reached your daily goal!", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveWaterIntake(amount: Int) {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val waterLog = WaterLog(amount = amount, timestamp = Timestamp.now())

        firestore.collection("users")
            .document(userId)
            .collection("waterIntake")
            .document(today)
            .get()
            .addOnSuccessListener { document ->
                val logs = if (document.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    (document.get("logs") as? List<Map<String, Any>>)?.map { map ->
                        WaterLog(
                            amount = (map["amount"] as Long).toInt(),
                            timestamp = map["timestamp"] as Timestamp
                        )
                    }?.toMutableList() ?: mutableListOf()
                } else {
                    mutableListOf()
                }

                logs.add(waterLog)

                firestore.collection("users")
                    .document(userId)
                    .collection("waterIntake")
                    .document(today)
                    .update(
                        mapOf(
                            "consumed" to currentConsumed,
                            "logs" to logs
                        )
                    )
            }
    }

    private fun updateUI() {
        tvGoal.text = "${currentGoal / 1000f}L"
        tvConsumed.text = "${currentConsumed / 1000f}L"

        val remaining = currentGoal - currentConsumed
        tvRemaining.text = if (remaining > 0) "${remaining / 1000f}L" else "0L"

        val progress = ((currentConsumed.toFloat() / currentGoal) * 100).toInt()
        progressBar.progress = progress
        tvProgress.text = "$progress%"
    }

    private fun showSetGoalDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Set Daily Goal")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.hint = "Enter goal in liters (e.g., 3)"
        builder.setView(input)

        builder.setPositiveButton("Set") { _, _ ->
            val goalInLiters = input.text.toString().toFloatOrNull()
            if (goalInLiters != null && goalInLiters > 0) {
                currentGoal = (goalInLiters * 1000).toInt()
                updateGoal()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun updateGoal() {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        firestore.collection("users")
            .document(userId)
            .collection("waterIntake")
            .document(today)
            .update("goal", currentGoal)
            .addOnSuccessListener {
                updateUI()
                Toast.makeText(this, "Goal updated!", Toast.LENGTH_SHORT).show()
            }
    }
}