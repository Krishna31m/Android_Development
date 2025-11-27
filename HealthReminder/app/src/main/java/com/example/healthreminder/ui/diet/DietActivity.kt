package com.example.healthreminder.ui.diet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Meal
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DietActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvMeals: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var mealAdapter: MealAdapter
    private val mealList = mutableListOf<Meal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        rvMeals = findViewById(R.id.rv_meals)
        emptyState = findViewById(R.id.empty_state)
        fabAdd = findViewById(R.id.fab_add_meal)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup RecyclerView
        mealAdapter = MealAdapter(mealList) { meal ->
            editMeal(meal)
        }
        rvMeals.layoutManager = LinearLayoutManager(this)
        rvMeals.adapter = mealAdapter

        // FAB click listener
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }

        // Load meals
        loadMeals()
    }

    override fun onResume() {
        super.onResume()
        loadMeals()
    }

    private fun loadMeals() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("meals")
            .get()
            .addOnSuccessListener { documents ->
                mealList.clear()
                for (doc in documents) {
                    val meal = doc.toObject(Meal::class.java)
                    mealList.add(meal)
                }

                // Sort by meal type order
                val mealTypeOrder = mapOf(
                    "Breakfast" to 1,
                    "Snack" to 2,
                    "Lunch" to 3,
                    "Dinner" to 4
                )
                mealList.sortBy { mealTypeOrder[it.mealType] ?: 5 }

                mealAdapter.notifyDataSetChanged()

                // Show/hide empty state
                if (mealList.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    rvMeals.visibility = View.GONE
                } else {
                    emptyState.visibility = View.GONE
                    rvMeals.visibility = View.VISIBLE
                }
            }
    }

    private fun editMeal(meal: Meal) {
        val intent = Intent(this, AddMealActivity::class.java)
        intent.putExtra("MEAL_ID", meal.id)
        startActivity(intent)
    }
}