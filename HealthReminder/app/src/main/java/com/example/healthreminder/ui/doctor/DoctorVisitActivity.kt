package com.example.healthreminder.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthreminder.R
import com.example.healthreminder.data.model.DoctorVisit
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DoctorVisitActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvDoctorVisits: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var doctorVisitAdapter: DoctorVisitAdapter
    private val doctorVisitList = mutableListOf<DoctorVisit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_visit)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        rvDoctorVisits = findViewById(R.id.rv_doctor_visits)
        emptyState = findViewById(R.id.empty_state)
        fabAdd = findViewById(R.id.fab_add_doctor_visit)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup RecyclerView
        doctorVisitAdapter = DoctorVisitAdapter(doctorVisitList) { visit ->
            editDoctorVisit(visit)
        }
        rvDoctorVisits.layoutManager = LinearLayoutManager(this)
        rvDoctorVisits.adapter = doctorVisitAdapter

        // FAB click listener
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddDoctorVisitActivity::class.java))
        }

        // Load doctor visits
        loadDoctorVisits()
    }

    override fun onResume() {
        super.onResume()
        loadDoctorVisits()
    }

    private fun loadDoctorVisits() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("doctorVisits")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                doctorVisitList.clear()
                for (doc in documents) {
                    val visit = doc.toObject(DoctorVisit::class.java)
                    doctorVisitList.add(visit)
                }
                doctorVisitAdapter.notifyDataSetChanged()

                // Show/hide empty state
                if (doctorVisitList.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    rvDoctorVisits.visibility = View.GONE
                } else {
                    emptyState.visibility = View.GONE
                    rvDoctorVisits.visibility = View.VISIBLE
                }
            }
    }

    private fun editDoctorVisit(visit: DoctorVisit) {
        val intent = Intent(this, AddDoctorVisitActivity::class.java)
        intent.putExtra("DOCTOR_VISIT_ID", visit.id)
        startActivity(intent)
    }
}