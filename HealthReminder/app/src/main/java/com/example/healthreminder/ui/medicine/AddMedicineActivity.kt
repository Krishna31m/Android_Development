package com.example.healthreminder.ui.medicine

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthreminder.R
import com.example.healthreminder.data.model.Medicine
import com.example.healthreminder.data.repository.MedicineRepository
import com.example.healthreminder.receivers.MedicineAlarmReceiver
import com.example.healthreminder.utils.AlarmScheduler
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.*

class AddMedicineActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etMedicineName: TextInputEditText
    private lateinit var etDosage: TextInputEditText
    private lateinit var btnSelectTime: MaterialButton
    private lateinit var rgFrequency: RadioGroup
    private lateinit var daysContainer: LinearLayout
    private lateinit var chipGroupDays: ChipGroup
    private lateinit var switchReminder: SwitchMaterial
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDelete: MaterialButton

    // Use the Repository
    private val medicineRepository = MedicineRepository()
    private lateinit var alarmScheduler: AlarmScheduler

    private var selectedTime = ""
    private var medicineId: String? = null
    private val selectedDays = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        alarmScheduler = AlarmScheduler(this)

        // Initialize views
        initializeViews()

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Check if editing existing medicine
        medicineId = intent.getStringExtra("MEDICINE_ID")
        if (medicineId != null) {
            toolbar.title = "Edit Medicine"
            btnDelete.visibility = View.VISIBLE
            loadMedicineData(medicineId!!)
        }

        // Setup listeners
        setupListeners()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        etMedicineName = findViewById(R.id.et_medicine_name)
        etDosage = findViewById(R.id.et_dosage)
        btnSelectTime = findViewById(R.id.btn_select_time)
        rgFrequency = findViewById(R.id.rg_frequency)
        daysContainer = findViewById(R.id.days_container)
        chipGroupDays = findViewById(R.id.chip_group_days)
        switchReminder = findViewById(R.id.switch_reminder)
        btnSave = findViewById(R.id.btn_save)
        btnDelete = findViewById(R.id.btn_delete)
    }

    private fun setupListeners() {
        btnSelectTime.setOnClickListener { showTimePicker() }

        rgFrequency.setOnCheckedChangeListener { _, checkedId ->
            daysContainer.visibility = if (checkedId == R.id.rb_weekly) View.VISIBLE else View.GONE
        }

        setupDayChips()
        btnSave.setOnClickListener { saveMedicine() }
        btnDelete.setOnClickListener { showDeleteConfirmation() }
    }

    private fun setupDayChips() {
        // ... (The implementation remains the same)
        val dayChips = listOf(
            findViewById<Chip>(R.id.chip_monday),
            findViewById<Chip>(R.id.chip_tuesday),
            findViewById<Chip>(R.id.chip_wednesday),
            findViewById<Chip>(R.id.chip_thursday),
            findViewById<Chip>(R.id.chip_friday),
            findViewById<Chip>(R.id.chip_saturday),
            findViewById<Chip>(R.id.chip_sunday)
        )

        dayChips.forEachIndexed { index, chip ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                val day = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[index]
                if (isChecked) {
                    if (!selectedDays.contains(day)) selectedDays.add(day)
                } else {
                    selectedDays.remove(day)
                }
            }
        }
    }

    private fun showTimePicker() {
        // ... (Implementation remains the same)
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            btnSelectTime.text = selectedTime
        }, hour, minute, true).show()
    }

    private fun saveMedicine() {
        val name = etMedicineName.text.toString().trim()
        val dosage = etDosage.text.toString().trim()

        if (name.isEmpty() || dosage.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields (Name, Dosage, Time)", Toast.LENGTH_SHORT).show()
            if (name.isEmpty()) etMedicineName.error = "Required"
            if (dosage.isEmpty()) etDosage.error = "Required"
            return
        }

        val frequency = when (rgFrequency.checkedRadioButtonId) {
            R.id.rb_daily -> "Daily"
            R.id.rb_weekly -> "Weekly"
            else -> "Custom"
        }

        if (frequency == "Weekly" && selectedDays.isEmpty()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the base Medicine object
        val baseMedicine = Medicine(
            id = medicineId ?: "", // If new, repository will assign ID
            name = name,
            dosage = dosage,
            time = selectedTime,
            frequency = frequency,
            days = if (frequency == "Weekly") selectedDays.toList() else emptyList(),
            reminderEnabled = switchReminder.isChecked,
            isActive = true,
            createdAt = Timestamp.now()
        )

        lifecycleScope.launch {
            if (medicineId == null) {
                // Add new medicine (Repository handles ID and userId)
                val result = medicineRepository.addMedicine(baseMedicine)
                handleSaveResult(result, name, dosage, baseMedicine.reminderEnabled, baseMedicine.time)
            } else {
                // Update existing medicine
                val result = medicineRepository.updateMedicine(baseMedicine)
                handleUpdateResult(result, medicineId!!, name, dosage, baseMedicine.reminderEnabled, baseMedicine.time)
            }
        }
    }

    private fun handleSaveResult(result: Result<String>, name: String, dosage: String, reminderEnabled: Boolean, time: String) {
        result.onSuccess { newMedicineId ->
            if (reminderEnabled) {
                alarmScheduler.scheduleMedicineReminder(newMedicineId, name, dosage, time)
            }
            Toast.makeText(this@AddMedicineActivity, "Medicine saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }.onFailure { e ->
            Toast.makeText(this@AddMedicineActivity, "Error saving medicine: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleUpdateResult(result: Result<Unit>, id: String, name: String, dosage: String, reminderEnabled: Boolean, time: String) {
        result.onSuccess {
            // Re-schedule or cancel alarm based on reminder switch
            if (reminderEnabled) {
                alarmScheduler.scheduleMedicineReminder(id, name, dosage, time)
            } else {
                alarmScheduler.cancelAlarm(id.hashCode(), MedicineAlarmReceiver::class.java)
            }
            Toast.makeText(this@AddMedicineActivity, "Medicine updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        }.onFailure { e ->
            Toast.makeText(this@AddMedicineActivity, "Error updating medicine: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Medicine")
            .setMessage("Are you sure you want to delete this medicine?")
            .setPositiveButton("Delete") { _, _ ->
                deleteMedicine()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMedicine() {
        medicineId?.let { id ->
            lifecycleScope.launch {
                val result = medicineRepository.deleteMedicine(id)

                result.onSuccess {
                    // Cancel alarm
                    alarmScheduler.cancelAlarm(id.hashCode(), MedicineAlarmReceiver::class.java)

                    Toast.makeText(this@AddMedicineActivity, "Medicine deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure { e ->
                    Toast.makeText(this@AddMedicineActivity, "Error deleting medicine: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadMedicineData(medicineId: String) {
        lifecycleScope.launch {
            val result = medicineRepository.getMedicineById(medicineId)

            result.onSuccess { medicine ->
                etMedicineName.setText(medicine.name)
                etDosage.setText(medicine.dosage)
                selectedTime = medicine.time
                btnSelectTime.text = selectedTime
                switchReminder.isChecked = medicine.reminderEnabled

                // ... (Logic to set frequency and day chips remains the same)
                when (medicine.frequency) {
                    "Daily" -> rgFrequency.check(R.id.rb_daily)
                    "Weekly" -> {
                        rgFrequency.check(R.id.rb_weekly)
                        daysContainer.visibility = View.VISIBLE
                        // Check appropriate day chips
                        medicine.days.forEach { day ->
                            val chipId = when (day) {
                                "Mon" -> R.id.chip_monday
                                "Tue" -> R.id.chip_tuesday
                                "Wed" -> R.id.chip_wednesday
                                "Thu" -> R.id.chip_thursday
                                "Fri" -> R.id.chip_friday
                                "Sat" -> R.id.chip_saturday
                                "Sun" -> R.id.chip_sunday
                                else -> null
                            }
                            chipId?.let { findViewById<Chip>(it).isChecked = true }
                        }
                    }
                    else -> rgFrequency.check(R.id.rb_custom)
                }
            }.onFailure { e ->
                Toast.makeText(this@AddMedicineActivity, "Error loading medicine data: ${e.message}", Toast.LENGTH_LONG).show()
                finish() // Close if data can't be loaded
            }
        }
    }
}

//package com.example.healthreminder.ui.medicine
//
//import android.app.TimePickerDialog
//import android.os.Bundle
//import android.view.View
//import android.widget.LinearLayout
//import android.widget.RadioGroup
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import com.example.healthreminder.R
//import com.example.healthreminder.data.model.Medicine
//import com.example.healthreminder.utils.AlarmScheduler
//import com.google.android.material.appbar.MaterialToolbar
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.chip.Chip
//import com.google.android.material.chip.ChipGroup
//import com.google.android.material.switchmaterial.SwitchMaterial
//import com.google.android.material.textfield.TextInputEditText
//import com.google.firebase.Timestamp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import java.util.*
//
//class AddMedicineActivity : AppCompatActivity() {
//
//    private lateinit var toolbar: MaterialToolbar
//    private lateinit var etMedicineName: TextInputEditText
//    private lateinit var etDosage: TextInputEditText
//    private lateinit var btnSelectTime: MaterialButton
//    private lateinit var rgFrequency: RadioGroup
//    private lateinit var daysContainer: LinearLayout
//    private lateinit var chipGroupDays: ChipGroup
//    private lateinit var switchReminder: SwitchMaterial
//    private lateinit var btnSave: MaterialButton
//    private lateinit var btnDelete: MaterialButton
//
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var auth: FirebaseAuth
//    private lateinit var alarmScheduler: AlarmScheduler
//
//    private var selectedTime = ""
//    private var medicineId: String? = null
//    private val selectedDays = mutableListOf<String>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_medicine)
//
//        // Initialize Firebase
//        firestore = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//        alarmScheduler = AlarmScheduler(this)
//
//        // Initialize views
//        initializeViews()
//
//        // Setup toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbar.setNavigationOnClickListener { finish() }
//
//        // Check if editing existing medicine
//        medicineId = intent.getStringExtra("MEDICINE_ID")
//        if (medicineId != null) {
//            toolbar.title = "Edit Medicine"
//            btnDelete.visibility = View.VISIBLE
//            loadMedicineData(medicineId!!)
//        }
//
//        // Setup listeners
//        setupListeners()
//    }
//
//    private fun initializeViews() {
//        toolbar = findViewById(R.id.toolbar)
//        etMedicineName = findViewById(R.id.et_medicine_name)
//        etDosage = findViewById(R.id.et_dosage)
//        btnSelectTime = findViewById(R.id.btn_select_time)
//        rgFrequency = findViewById(R.id.rg_frequency)
//        daysContainer = findViewById(R.id.days_container)
//        chipGroupDays = findViewById(R.id.chip_group_days)
//        switchReminder = findViewById(R.id.switch_reminder)
//        btnSave = findViewById(R.id.btn_save)
//        btnDelete = findViewById(R.id.btn_delete)
//    }
//
//    private fun setupListeners() {
//        // Time picker
//        btnSelectTime.setOnClickListener {
//            showTimePicker()
//        }
//
//        // Frequency radio group
//        rgFrequency.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.rb_weekly -> {
//                    daysContainer.visibility = View.VISIBLE
//                }
//                else -> {
//                    daysContainer.visibility = View.GONE
//                }
//            }
//        }
//
//        // Days chips
//        setupDayChips()
//
//        // Save button
//        btnSave.setOnClickListener {
//            saveMedicine()
//        }
//
//        // Delete button
//        btnDelete.setOnClickListener {
//            showDeleteConfirmation()
//        }
//    }
//
//    private fun setupDayChips() {
//        val dayChips = listOf(
//            findViewById<Chip>(R.id.chip_monday),
//            findViewById<Chip>(R.id.chip_tuesday),
//            findViewById<Chip>(R.id.chip_wednesday),
//            findViewById<Chip>(R.id.chip_thursday),
//            findViewById<Chip>(R.id.chip_friday),
//            findViewById<Chip>(R.id.chip_saturday),
//            findViewById<Chip>(R.id.chip_sunday)
//        )
//
//        dayChips.forEachIndexed { index, chip ->
//            chip.setOnCheckedChangeListener { _, isChecked ->
//                val day = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[index]
//                if (isChecked) {
//                    if (!selectedDays.contains(day)) selectedDays.add(day)
//                } else {
//                    selectedDays.remove(day)
//                }
//            }
//        }
//    }
//
//    private fun showTimePicker() {
//        val calendar = Calendar.getInstance()
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//
//        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
//            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
//            btnSelectTime.text = selectedTime
//        }, hour, minute, true).show()
//    }
//
//    private fun saveMedicine() {
//        val name = etMedicineName.text.toString().trim()
//        val dosage = etDosage.text.toString().trim()
//
//        // Validation
//        if (name.isEmpty()) {
//            etMedicineName.error = "Medicine name is required"
//            etMedicineName.requestFocus()
//            return
//        }
//
//        if (dosage.isEmpty()) {
//            etDosage.error = "Dosage is required"
//            etDosage.requestFocus()
//            return
//        }
//
//        if (selectedTime.isEmpty()) {
//            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val frequency = when (rgFrequency.checkedRadioButtonId) {
//            R.id.rb_daily -> "Daily"
//            R.id.rb_weekly -> "Weekly"
//            else -> "Custom"
//        }
//
//        if (frequency == "Weekly" && selectedDays.isEmpty()) {
//            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val userId = auth.currentUser?.uid ?: return
//        val docId = medicineId ?: firestore.collection("users")
//            .document(userId)
//            .collection("medicines")
//            .document().id
//
//        val medicine = Medicine(
//            id = docId,
//            userId = userId,
//            name = name,
//            dosage = dosage,
//            time = selectedTime,
//            frequency = frequency,
//            days = if (frequency == "Weekly") selectedDays else listOf(),
//            reminderEnabled = switchReminder.isChecked,
//            isActive = true,
//            createdAt = Timestamp.now()
//        )
//
//        firestore.collection("users")
//            .document(userId)
//            .collection("medicines")
//            .document(docId)
//            .set(medicine)
//            .addOnSuccessListener {
//                // Schedule alarm
//                if (switchReminder.isChecked) {
//                    alarmScheduler.scheduleMedicineReminder(docId, name, dosage, selectedTime)
//                }
//
//                Toast.makeText(this, "Medicine saved successfully", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun showDeleteConfirmation() {
//        AlertDialog.Builder(this)
//            .setTitle("Delete Medicine")
//            .setMessage("Are you sure you want to delete this medicine?")
//            .setPositiveButton("Delete") { _, _ ->
//                deleteMedicine()
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    private fun deleteMedicine() {
//        val userId = auth.currentUser?.uid ?: return
//        medicineId?.let { id ->
//            firestore.collection("users")
//                .document(userId)
//                .collection("medicines")
//                .document(id)
//                .update("isActive", false)
//                .addOnSuccessListener {
//                    // Cancel alarm
//                    alarmScheduler.cancelAlarm(id.hashCode(),
//                        com.example.healthreminder.receivers.MedicineAlarmReceiver::class.java)
//
//                    Toast.makeText(this, "Medicine deleted", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    private fun loadMedicineData(medicineId: String) {
//        val userId = auth.currentUser?.uid ?: return
//
//        firestore.collection("users")
//            .document(userId)
//            .collection("medicines")
//            .document(medicineId)
//            .get()
//            .addOnSuccessListener { document ->
//                val medicine = document.toObject(Medicine::class.java) ?: return@addOnSuccessListener
//
//                etMedicineName.setText(medicine.name)
//                etDosage.setText(medicine.dosage)
//                selectedTime = medicine.time
//                btnSelectTime.text = selectedTime
//                switchReminder.isChecked = medicine.reminderEnabled
//
//                when (medicine.frequency) {
//                    "Daily" -> rgFrequency.check(R.id.rb_daily)
//                    "Weekly" -> {
//                        rgFrequency.check(R.id.rb_weekly)
//                        daysContainer.visibility = View.VISIBLE
//                        // Check appropriate day chips
//                        medicine.days.forEach { day ->
//                            val chipId = when (day) {
//                                "Mon" -> R.id.chip_monday
//                                "Tue" -> R.id.chip_tuesday
//                                "Wed" -> R.id.chip_wednesday
//                                "Thu" -> R.id.chip_thursday
//                                "Fri" -> R.id.chip_friday
//                                "Sat" -> R.id.chip_saturday
//                                "Sun" -> R.id.chip_sunday
//                                else -> null
//                            }
//                            chipId?.let { findViewById<Chip>(it).isChecked = true }
//                        }
//                    }
//                    else -> rgFrequency.check(R.id.rb_custom)
//                }
//            }
//    }
//}