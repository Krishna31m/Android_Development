package com.example.healthreminder.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.ui.medicine.MedicineActivity
import com.example.healthreminder.ui.exercise.ExerciseActivity
import com.example.healthreminder.ui.diet.DietActivity
import com.example.healthreminder.ui.water.WaterTrackerActivity
import com.example.healthreminder.ui.doctor.DoctorVisitActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RemindersFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var rvTodayReminders: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminders, container, false)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        rvTodayReminders = view.findViewById(R.id.rv_today_reminders)
        rvTodayReminders.layoutManager = LinearLayoutManager(context)

        // Setup category cards
        setupCategoryCards(view)

        // Load today's reminders
        loadTodayReminders()

        return view
    }

    private fun setupCategoryCards(view: View) {
        view.findViewById<MaterialCardView>(R.id.card_medicine_reminders).setOnClickListener {
            startActivity(Intent(context, MedicineActivity::class.java))
        }

        view.findViewById<MaterialCardView>(R.id.card_water_reminders).setOnClickListener {
            startActivity(Intent(context, WaterTrackerActivity::class.java))
        }

        view.findViewById<MaterialCardView>(R.id.card_exercise_reminders).setOnClickListener {
            startActivity(Intent(context, ExerciseActivity::class.java))
        }

        view.findViewById<MaterialCardView>(R.id.card_diet_reminders).setOnClickListener {
            startActivity(Intent(context, DietActivity::class.java))
        }

        view.findViewById<MaterialCardView>(R.id.card_doctor_reminders).setOnClickListener {
            startActivity(Intent(context, DoctorVisitActivity::class.java))
        }
    }

    private fun loadTodayReminders() {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Load all active reminders for today
        // This would aggregate medicines, exercises, meals, etc.
        // For simplicity, showing count in each category
    }
}