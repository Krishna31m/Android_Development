package com.example.healthreminder.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.ui.challenges.ChallengesActivity
import com.example.healthreminder.ui.chatbot.ChatBotActivity
import com.example.healthreminder.ui.diet.DietActivity
import com.example.healthreminder.ui.doctor.DoctorVisitActivity
import com.example.healthreminder.ui.emergency.EmergencyCardActivity
import com.example.healthreminder.ui.exercise.ExerciseActivity
import com.example.healthreminder.ui.medicine.MedicineActivity
import com.example.healthreminder.ui.mood.MoodTrackerActivity
import com.example.healthreminder.ui.water.WaterTrackerActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tvWelcome: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvWaterProgress: TextView
    private lateinit var tvMedicineCount: TextView
    private lateinit var tvExerciseCount: TextView
    private lateinit var tvDietCount: TextView
    private lateinit var tvDoctorCount: TextView
    private lateinit var tvChallengeStreak: TextView
    private lateinit var rvTodaySchedule: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews(view)

        // Setup click listeners
        setupClickListeners(view)

        // Load data
        loadDashboardData()

        // Display date
        displayCurrentDate()

        return view
    }

    private fun initializeViews(view: View) {
        tvWelcome = view.findViewById(R.id.tv_welcome)
        tvDate = view.findViewById(R.id.tv_date)
        tvWaterProgress = view.findViewById(R.id.tv_water_progress)
        tvMedicineCount = view.findViewById(R.id.tv_medicine_count)
        tvExerciseCount = view.findViewById(R.id.tv_exercise_count)
        tvDietCount = view.findViewById(R.id.tv_diet_count)
        tvDoctorCount = view.findViewById(R.id.tv_doctor_count)
        tvChallengeStreak = view.findViewById(R.id.tv_challenge_streak)
        rvTodaySchedule = view.findViewById(R.id.rv_today_schedule)

        // Setup RecyclerView
        rvTodaySchedule.layoutManager = LinearLayoutManager(context)
    }

    private fun setupClickListeners(view: View) {
        // Emergency Card
        view.findViewById<MaterialCardView>(R.id.card_emergency).setOnClickListener {
            startActivity(Intent(context, EmergencyCardActivity::class.java))
        }

        // Medicine Card
        view.findViewById<MaterialCardView>(R.id.card_medicine).setOnClickListener {
            startActivity(Intent(context, MedicineActivity::class.java))
        }

        // Water Card
        view.findViewById<MaterialCardView>(R.id.card_water).setOnClickListener {
            startActivity(Intent(context, WaterTrackerActivity::class.java))
        }

        // Exercise Card
        view.findViewById<MaterialCardView>(R.id.card_exercise).setOnClickListener {
            startActivity(Intent(context, ExerciseActivity::class.java))
        }

        // Diet Card
        view.findViewById<MaterialCardView>(R.id.card_diet).setOnClickListener {
            startActivity(Intent(context, DietActivity::class.java))
        }

        // Mood Card
        view.findViewById<MaterialCardView>(R.id.card_mood).setOnClickListener {
            startActivity(Intent(context, MoodTrackerActivity::class.java))
        }

        // Doctor Card
        view.findViewById<MaterialCardView>(R.id.card_doctor).setOnClickListener {
            startActivity(Intent(context, DoctorVisitActivity::class.java))
        }

        // Challenges Card
        view.findViewById<MaterialCardView>(R.id.card_challenges).setOnClickListener {
            startActivity(Intent(context, ChallengesActivity::class.java))
        }

        // Health Stats Card
        view.findViewById<MaterialCardView>(R.id.card_health_stats).setOnClickListener {
            // Switch to Health Stats Fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HealthStatsFragment())
                .commit()
        }

        view.findViewById<MaterialCardView>(R.id.card_chatbot).setOnClickListener {
            startActivity(Intent(context, ChatBotActivity::class.java))
        }
    }

    private fun displayCurrentDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(Date())
    }

    private fun loadDashboardData() {
        val userId = auth.currentUser?.uid ?: return

        // Load user name
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "User"
                tvWelcome.text = "Welcome Back, $name!"
            }

        // Load today's water intake
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        firestore.collection("users")
            .document(userId)
            .collection("waterIntake")
            .document(today)
            .get()
            .addOnSuccessListener { document ->
                val consumed = document.getLong("consumed")?.toInt() ?: 0
                val goal = document.getLong("goal")?.toInt() ?: 3000
                tvWaterProgress.text = "${consumed / 1000f}L / ${goal / 1000}L"
            }

        // Load medicine count
        firestore.collection("users")
            .document(userId)
            .collection("medicines")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->
                tvMedicineCount.text = "${documents.size()} Today"
            }

        // Load exercise count
        firestore.collection("users")
            .document(userId)
            .collection("exercises")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->
                tvExerciseCount.text = "${documents.size()} Planned"
            }

        // Load diet/meal count
        firestore.collection("users")
            .document(userId)
            .collection("meals")
            .get()
            .addOnSuccessListener { documents ->
                tvDietCount.text = "${documents.size()} Meals"
            }

        // Load doctor appointments
        firestore.collection("users")
            .document(userId)
            .collection("doctorVisits")
            .whereEqualTo("completed", false)
            .get()
            .addOnSuccessListener { documents ->
                tvDoctorCount.text = "${documents.size()} Upcoming"
            }

        // Load challenge streak
        firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .whereEqualTo("isActive", true)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val streak = documents.documents[0].getLong("currentStreak")?.toInt() ?: 0
                    tvChallengeStreak.text = "$streak Day Streak"
                }
            }
    }
}