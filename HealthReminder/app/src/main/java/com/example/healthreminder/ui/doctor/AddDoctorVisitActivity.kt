package com.example.healthreminder.ui.doctor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthreminder.R
import com.example.healthreminder.data.model.DoctorVisit
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddDoctorVisitActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etDoctorName: TextInputEditText
    private lateinit var etSpecialty: TextInputEditText
    private lateinit var btnSelectDate: MaterialButton
    private lateinit var btnSelectTime: MaterialButton
    private lateinit var etLocation: TextInputEditText
    private lateinit var etReason: TextInputEditText
    private lateinit var switchReminder: SwitchMaterial
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDelete: MaterialButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var selectedDate: Date? = null
    private var selectedTime = ""
    private var doctorVisitId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_doctor_visit)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Check if editing
        doctorVisitId = intent.getStringExtra("DOCTOR_VISIT_ID")
        if (doctorVisitId != null) {
            toolbar.title = "Edit Doctor Visit"
            btnDelete.visibility = View.VISIBLE
            loadDoctorVisitData(doctorVisitId!!)
        }

        // Setup listeners
        setupListeners()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        etDoctorName = findViewById(R.id.et_doctor_name)
        etSpecialty = findViewById(R.id.et_specialty)
        btnSelectDate = findViewById(R.id.btn_select_date)
        btnSelectTime = findViewById(R.id.btn_select_time)
        etLocation = findViewById(R.id.et_location)
        etReason = findViewById(R.id.et_reason)
        switchReminder = findViewById(R.id.switch_reminder)
        btnSave = findViewById(R.id.btn_save)
        btnDelete = findViewById(R.id.btn_delete)
    }

    private fun setupListeners() {
        btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        btnSelectTime.setOnClickListener {
            showTimePicker()
        }

        btnSave.setOnClickListener {
            saveDoctorVisit()
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.time
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                btnSelectDate.text = dateFormat.format(selectedDate!!)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
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

    private fun saveDoctorVisit() {
        val doctorName = etDoctorName.text.toString().trim()
        val specialty = etSpecialty.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val reason = etReason.text.toString().trim()

        // Validation
        if (doctorName.isEmpty()) {
            etDoctorName.error = "Doctor name is required"
            etDoctorName.requestFocus()
            return
        }

        if (specialty.isEmpty()) {
            etSpecialty.error = "Specialty is required"
            etSpecialty.requestFocus()
            return
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val docId = doctorVisitId ?: firestore.collection("users")
            .document(userId)
            .collection("doctorVisits")
            .document().id

        val doctorVisit = DoctorVisit(
            id = docId,
            userId = userId,
            doctorName = doctorName,
            specialty = specialty,
            date = Timestamp(selectedDate!!),
            time = selectedTime,
            location = location,
            reason = reason,
            reminderEnabled = switchReminder.isChecked,
            completed = false,
            createdAt = Timestamp.now()
        )

        firestore.collection("users")
            .document(userId)
            .collection("doctorVisits")
            .document(docId)
            .set(doctorVisit)
            .addOnSuccessListener {
                Toast.makeText(this, "Doctor visit saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Doctor Visit")
            .setMessage("Are you sure you want to delete this appointment?")
            .setPositiveButton("Delete") { _, _ ->
                deleteDoctorVisit()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteDoctorVisit() {
        val userId = auth.currentUser?.uid ?: return
        doctorVisitId?.let { id ->
            firestore.collection("users")
                .document(userId)
                .collection("doctorVisits")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Doctor visit deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadDoctorVisitData(visitId: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("doctorVisits")
            .document(visitId)
            .get()
            .addOnSuccessListener { document ->
                val visit = document.toObject(DoctorVisit::class.java) ?: return@addOnSuccessListener

                etDoctorName.setText(visit.doctorName)
                etSpecialty.setText(visit.specialty)
                etLocation.setText(visit.location)
                etReason.setText(visit.reason)

                visit.date?.let {
                    selectedDate = it.toDate()
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    btnSelectDate.text = dateFormat.format(selectedDate!!)
                }

                selectedTime = visit.time
                btnSelectTime.text = selectedTime
                switchReminder.isChecked = visit.reminderEnabled
            }
    }
}