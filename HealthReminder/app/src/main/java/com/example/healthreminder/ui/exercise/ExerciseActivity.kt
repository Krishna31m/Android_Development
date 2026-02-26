package com.example.healthreminder.ui.exercise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Exercise
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ExerciseActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvExercises: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var exerciseAdapter: ExerciseAdapter
    private val exerciseList = mutableListOf<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup RecyclerView
        setupRecyclerView()

        // FAB click listener
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddExerciseActivity::class.java))
        }

        // Load exercises
        loadExercises()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        rvExercises = findViewById(R.id.rv_exercises)
        emptyState = findViewById(R.id.empty_state)
        fabAdd = findViewById(R.id.fab_add_exercise)
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter(exerciseList) { exercise ->
            editExercise(exercise)
        }
        rvExercises.layoutManager = LinearLayoutManager(this)
        rvExercises.adapter = exerciseAdapter
    }

    override fun onResume() {
        super.onResume()
        loadExercises()
    }

    private fun loadExercises() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Log.e("ExerciseActivity", "User not logged in")
            updateEmptyState(true)
            return
        }

        Log.d("ExerciseActivity", "Loading exercises for user: $userId")

        // ✅ FIXED: Changed "isActive" to "active"
        // NOTE: Removed .orderBy() to avoid needing a composite index
        // You can sort in memory after fetching
        firestore.collection("users")
            .document(userId)
            .collection("exercises")
            .whereEqualTo("active", true)  // Changed from "isActive"
            .get()
            .addOnSuccessListener { documents ->
                Log.d("ExerciseActivity", "Query successful, ${documents.size()} documents found")

                exerciseList.clear()
                for (doc in documents) {
                    try {
                        val exercise = doc.toObject(Exercise::class.java)
                        exerciseList.add(exercise)
                        Log.d("ExerciseActivity", "Added exercise: ${exercise.name}")
                    } catch (e: Exception) {
                        Log.e("ExerciseActivity", "Error parsing exercise: ${doc.id}", e)
                    }
                }

                // Sort in memory by time
                exerciseList.sortBy { it.time }

                exerciseAdapter.notifyDataSetChanged()
                updateEmptyState(exerciseList.isEmpty())

                Log.d("ExerciseActivity", "Total exercises loaded: ${exerciseList.size}")
            }
            .addOnFailureListener { e ->
                Log.e("ExerciseActivity", "Error loading exercises", e)
                updateEmptyState(true)
            }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            emptyState.visibility = View.VISIBLE
            rvExercises.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            rvExercises.visibility = View.VISIBLE
        }
    }

    private fun editExercise(exercise: Exercise) {
        val intent = Intent(this, AddExerciseActivity::class.java)
        intent.putExtra("EXERCISE_ID", exercise.id)
        startActivity(intent)
    }
}

