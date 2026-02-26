package com.example.healthreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MedicineActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getStringExtra("MEDICINE_ID") ?: return
        val action = intent.action

        // Here you would log the action (Taken/Skipped) to Firestore
        // Implementation depends on your data structure

        // Cancel the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as android.app.NotificationManager
        notificationManager.cancel(medicineId.hashCode())
    }
}