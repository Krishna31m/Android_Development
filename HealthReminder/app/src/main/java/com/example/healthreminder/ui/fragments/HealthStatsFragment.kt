package com.example.healthreminder.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.healthreminder.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HealthStatsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var tvDate: TextView
    private lateinit var tvWaterProgress: TextView
    private lateinit var tvMedicineCompliance: TextView
    private lateinit var tvExerciseCompleted: TextView
    private lateinit var tvMealsCompleted: TextView
    private lateinit var tvCurrentMood: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_health_stats, container, false)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews(view)

        // Display current date
        displayDate()

        // Load stats
        loadHealthStats()

        return view
    }

    private fun initializeViews(view: View) {
        tvDate = view.findViewById(R.id.tv_date)
        tvWaterProgress = view.findViewById(R.id.tv_water_progress)
        tvMedicineCompliance = view.findViewById(R.id.tv_medicine_compliance)
        tvExerciseCompleted = view.findViewById(R.id.tv_exercise_completed)
        tvMealsCompleted = view.findViewById(R.id.tv_meals_completed)
        tvCurrentMood = view.findViewById(R.id.tv_current_mood)
    }

    private fun displayDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(Date())
    }

    private fun loadHealthStats() {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Load water intake
        firestore.collection("users")
            .document(userId)
            .collection("waterIntake")
            .document(today)
            .get()
            .addOnSuccessListener { document ->
                val consumed = document.getLong("consumed")?.toInt() ?: 0
                val goal = document.getLong("goal")?.toInt() ?: 3000
                val percentage = if (goal > 0) (consumed.toFloat() / goal * 100).toInt() else 0
                tvWaterProgress.text = "$percentage% ($consumed ml / $goal ml)"
            }

        // Load medicine compliance
        firestore.collection("users")
            .document(userId)
            .collection("medicines")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->
                val total = documents.size()
                // Would need to check history for actual taken count
                tvMedicineCompliance.text = "0 / $total taken today"
            }

        // Load exercises
        firestore.collection("users")
            .document(userId)
            .collection("exercises")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->
                val total = documents.size()
                tvExerciseCompleted.text = "0 / $total completed"
            }

        // Load meals
        firestore.collection("users")
            .document(userId)
            .collection("meals")
            .get()
            .addOnSuccessListener { documents ->
                val total = documents.size()
                tvMealsCompleted.text = "0 / $total completed"
            }

        // Load mood
        firestore.collection("users")
            .document(userId)
            .collection("mood")
            .document(today)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val mood = document.getString("mood") ?: "Not tracked"
                    val emoji = document.getString("emoji") ?: ""
                    tvCurrentMood.text = "$emoji $mood"
                } else {
                    tvCurrentMood.text = "Not tracked yet"
                }
            }
    }

    override fun onResume() {
        super.onResume()
        loadHealthStats()
    }
}