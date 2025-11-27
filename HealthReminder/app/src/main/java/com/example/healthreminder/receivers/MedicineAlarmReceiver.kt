    package com.example.healthreminder.receivers

    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent
    import com.example.healthreminder.utils.NotificationHelper

    class MedicineAlarmReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val medicineId = intent.getStringExtra("MEDICINE_ID") ?: return
            val medicineName = intent.getStringExtra("MEDICINE_NAME") ?: "Medicine"
            val dosage = intent.getStringExtra("DOSAGE") ?: ""

            val notificationHelper = NotificationHelper(context)
            notificationHelper.showMedicineNotification(medicineId, medicineName, dosage)
        }
    }




