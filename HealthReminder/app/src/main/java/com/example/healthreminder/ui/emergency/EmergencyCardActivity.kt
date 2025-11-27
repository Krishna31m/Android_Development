package com.example.healthreminder.ui.emergency

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmergencyCardActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etBloodGroup: TextInputEditText
    private lateinit var etAllergies: TextInputEditText
    private lateinit var etMedicalConditions: TextInputEditText
    private lateinit var etEmergencyContactName: TextInputEditText
    private lateinit var etEmergencyContact: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var cardView: View
    private lateinit var tvDisplayName: TextView
    private lateinit var tvDisplayBloodGroup: TextView
    private lateinit var tvDisplayAllergies: TextView
    private lateinit var tvDisplayConditions: TextView
    private lateinit var tvDisplayEmergencyContact: TextView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_card)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Load existing data
        loadEmergencyData()

        // Save button
        btnSave.setOnClickListener {
            saveEmergencyData()
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        etBloodGroup = findViewById(R.id.et_blood_group)
        etAllergies = findViewById(R.id.et_allergies)
        etMedicalConditions = findViewById(R.id.et_medical_conditions)
        etEmergencyContactName = findViewById(R.id.et_emergency_contact_name)
        etEmergencyContact = findViewById(R.id.et_emergency_contact)
        btnSave = findViewById(R.id.btn_save)
        cardView = findViewById(R.id.card_view_display)
        tvDisplayName = findViewById(R.id.tv_display_name)
        tvDisplayBloodGroup = findViewById(R.id.tv_display_blood_group)
        tvDisplayAllergies = findViewById(R.id.tv_display_allergies)
        tvDisplayConditions = findViewById(R.id.tv_display_conditions)
        tvDisplayEmergencyContact = findViewById(R.id.tv_display_emergency_contact)
    }

    private fun loadEmergencyData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val bloodGroup = document.getString("bloodGroup") ?: ""
                    val allergies = document.getString("allergies") ?: ""
                    val conditions = document.getString("medicalConditions") ?: ""
                    val emergencyContactName = document.getString("emergencyContactName") ?: ""
                    val emergencyContact = document.getString("emergencyContact") ?: ""

                    // Fill form
                    etBloodGroup.setText(bloodGroup)
                    etAllergies.setText(allergies)
                    etMedicalConditions.setText(conditions)
                    etEmergencyContactName.setText(emergencyContactName)
                    etEmergencyContact.setText(emergencyContact)

                    // Display card
                    tvDisplayName.text = name
                    tvDisplayBloodGroup.text = if (bloodGroup.isEmpty()) "Not set" else bloodGroup
                    tvDisplayAllergies.text = if (allergies.isEmpty()) "None" else allergies
                    tvDisplayConditions.text = if (conditions.isEmpty()) "None" else conditions
                    tvDisplayEmergencyContact.text =
                        if (emergencyContact.isEmpty()) "Not set"
                        else "$emergencyContactName: $emergencyContact"

                    if (bloodGroup.isNotEmpty()) {
                        cardView.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun saveEmergencyData() {
        val bloodGroup = etBloodGroup.text.toString().trim()
        val allergies = etAllergies.text.toString().trim()
        val conditions = etMedicalConditions.text.toString().trim()
        val emergencyContactName = etEmergencyContactName.text.toString().trim()
        val emergencyContact = etEmergencyContact.text.toString().trim()

        val userId = auth.currentUser?.uid ?: return

        val updateData = hashMapOf(
            "bloodGroup" to bloodGroup,
            "allergies" to allergies,
            "medicalConditions" to conditions,
            "emergencyContactName" to emergencyContactName,
            "emergencyContact" to emergencyContact
        )

        firestore.collection("users")
            .document(userId)
            .update(updateData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Emergency card updated!", Toast.LENGTH_SHORT).show()
                loadEmergencyData()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}