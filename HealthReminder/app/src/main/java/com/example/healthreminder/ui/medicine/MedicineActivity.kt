package com.example.healthreminder.ui.medicine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Medicine
import com.example.healthreminder.viewmodel.MedicineViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MedicineActivity : AppCompatActivity() {

    private val TAG = "MedicineActivity"

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvMedicines: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAdd: FloatingActionButton

    // Use ViewModel instead of direct repository access
    private lateinit var viewModel: MedicineViewModel

    private lateinit var medicineAdapter: MedicineAdapter
    private val medicineList = mutableListOf<Medicine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine)

        Log.d(TAG, "onCreate: Initializing MedicineActivity")

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MedicineViewModel::class.java]

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        rvMedicines = findViewById(R.id.rv_medicines)
        emptyState = findViewById(R.id.empty_state)
        fabAdd = findViewById(R.id.fab_add_medicine)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup RecyclerView
        medicineAdapter = MedicineAdapter(medicineList) { medicine ->
            editMedicine(medicine)
        }
        rvMedicines.layoutManager = LinearLayoutManager(this)
        rvMedicines.adapter = medicineAdapter

        // FAB click listener
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddMedicineActivity::class.java))
        }

        // Observe ViewModel
        setupObservers()

        // Load medicines
        viewModel.loadMedicines()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Refreshing medicines")
        viewModel.loadMedicines()
    }

    private fun setupObservers() {
        // Observe medicines list
        viewModel.medicines.observe(this) { medicines ->
            Log.d(TAG, "Observer: Received ${medicines.size} medicines")

            medicineList.clear()
            medicineList.addAll(medicines)
            medicineAdapter.notifyDataSetChanged()

            // Show/hide empty state
            if (medicineList.isEmpty()) {
                Log.d(TAG, "Observer: Showing empty state")
                emptyState.visibility = View.VISIBLE
                rvMedicines.visibility = View.GONE
            } else {
                Log.d(TAG, "Observer: Showing ${medicineList.size} medicines")
                emptyState.visibility = View.GONE
                rvMedicines.visibility = View.VISIBLE
            }
        }

        // Observe loading state
        viewModel.loading.observe(this) { isLoading ->
            Log.d(TAG, "Observer: Loading state = $isLoading")
            // You can show a progress bar here if needed
        }

        // Observe errors
        viewModel.error.observe(this) { error ->
            error?.let {
                Log.e(TAG, "Observer: Error occurred - $it")
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun editMedicine(medicine: Medicine) {
        val intent = Intent(this, AddMedicineActivity::class.java)
        intent.putExtra("MEDICINE_ID", medicine.id)
        startActivity(intent)
    }
}

//package com.example.healthreminder.ui.medicine
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.LinearLayout
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.healthreminder.R
//import com.example.healthreminder.data.model.Medicine
//import com.example.healthreminder.data.repository.MedicineRepository
//import com.google.android.material.appbar.MaterialToolbar
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import kotlinx.coroutines.launch
//
//class MedicineActivity : AppCompatActivity() {
//
//    private lateinit var toolbar: MaterialToolbar
//    private lateinit var rvMedicines: RecyclerView
//    private lateinit var emptyState: LinearLayout
//    private lateinit var fabAdd: FloatingActionButton
//
//    // Use the Repository
//    private val medicineRepository = MedicineRepository()
//
//    private lateinit var medicineAdapter: MedicineAdapter
//    private val medicineList = mutableListOf<Medicine>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_medicine)
//
//        // Initialize views
//        toolbar = findViewById(R.id.toolbar)
//        rvMedicines = findViewById(R.id.rv_medicines)
//        emptyState = findViewById(R.id.empty_state)
//        fabAdd = findViewById(R.id.fab_add_medicine)
//
//        // Setup toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbar.setNavigationOnClickListener { finish() }
//
//        // Setup RecyclerView
//        medicineAdapter = MedicineAdapter(medicineList) { medicine ->
//            // Handle medicine click
//            editMedicine(medicine)
//        }
//        rvMedicines.layoutManager = LinearLayoutManager(this)
//        rvMedicines.adapter = medicineAdapter
//
//        // FAB click listener
//        fabAdd.setOnClickListener {
//            startActivity(Intent(this, AddMedicineActivity::class.java))
//        }
//
//        // Load medicines initially
//        loadMedicines()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Ensure data is refreshed every time the activity returns to the foreground
//        loadMedicines()
//    }
//
//    private fun loadMedicines() {
//        lifecycleScope.launch {
//            // Use the repository method
//            val result = medicineRepository.getAllMedicines()
//
//            result.onSuccess { medicines ->
//                medicineList.clear()
//                medicineList.addAll(medicines)
//                medicineAdapter.notifyDataSetChanged()
//
//                // Show/hide empty state
//                if (medicineList.isEmpty()) {
//                    emptyState.visibility = View.VISIBLE
//                    rvMedicines.visibility = View.GONE
//                } else {
//                    emptyState.visibility = View.GONE
//                    rvMedicines.visibility = View.VISIBLE
//                }
//            }.onFailure { e ->
//                Toast.makeText(this@MedicineActivity, "Error loading medicines: ${e.message}", Toast.LENGTH_LONG).show()
//                // Optionally show empty state on failure
//                emptyState.visibility = View.VISIBLE
//                rvMedicines.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun editMedicine(medicine: Medicine) {
//        val intent = Intent(this, AddMedicineActivity::class.java)
//        intent.putExtra("MEDICINE_ID", medicine.id)
//        startActivity(intent)
//    }
//}

//package com.example.healthreminder.ui.medicine
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.LinearLayout
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.healthreminder.R
//import com.example.healthreminder.data.model.Medicine
//import com.google.android.material.appbar.MaterialToolbar
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class MedicineActivity : AppCompatActivity() {
//
//    private lateinit var toolbar: MaterialToolbar
//    private lateinit var rvMedicines: RecyclerView
//    private lateinit var emptyState: LinearLayout
//    private lateinit var fabAdd: FloatingActionButton
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var auth: FirebaseAuth
//    private lateinit var medicineAdapter: MedicineAdapter
//    private val medicineList = mutableListOf<Medicine>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_medicine)
//
//        // Initialize Firebase
//        firestore = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//
//        // Initialize views
//        toolbar = findViewById(R.id.toolbar)
//        rvMedicines = findViewById(R.id.rv_medicines)
//        emptyState = findViewById(R.id.empty_state)
//        fabAdd = findViewById(R.id.fab_add_medicine)
//
//        // Setup toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbar.setNavigationOnClickListener { finish() }
//
//        // Setup RecyclerView
//        medicineAdapter = MedicineAdapter(medicineList) { medicine ->
//            // Handle medicine click
//            editMedicine(medicine)
//        }
//        rvMedicines.layoutManager = LinearLayoutManager(this)
//        rvMedicines.adapter = medicineAdapter
//
//        // FAB click listener
//        fabAdd.setOnClickListener {
//            startActivity(Intent(this, AddMedicineActivity::class.java))
//        }
//
//        // Load medicines
//        loadMedicines()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        loadMedicines()
//    }
//
//    private fun loadMedicines() {
//        val userId = auth.currentUser?.uid ?: return
//
//        firestore.collection("users")
//            .document(userId)
//            .collection("medicines")
//            .whereEqualTo("isActive", true)
//            .get()
//            .addOnSuccessListener { documents ->
//                medicineList.clear()
//                for (doc in documents) {
//                    val medicine = doc.toObject(Medicine::class.java)
//                    medicineList.add(medicine)
//                }
//                medicineAdapter.notifyDataSetChanged()
//
//                // Show/hide empty state
//                if (medicineList.isEmpty()) {
//                    emptyState.visibility = View.VISIBLE
//                    rvMedicines.visibility = View.GONE
//                } else {
//                    emptyState.visibility = View.GONE
//                    rvMedicines.visibility = View.VISIBLE
//                }
//            }
//    }
//
//    private fun editMedicine(medicine: Medicine) {
//        val intent = Intent(this, AddMedicineActivity::class.java)
//        intent.putExtra("MEDICINE_ID", medicine.id)
//        startActivity(intent)
//    }
//}