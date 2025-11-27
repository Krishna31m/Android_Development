package com.example.healthreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthreminder.utils.AlarmScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Receiver to reschedule alarms after device reboot
 */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            // Reschedule all alarms
            rescheduleAllAlarms(context)
        }
    }

    private fun rescheduleAllAlarms(context: Context) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return

        val firestore = FirebaseFirestore.getInstance()
        val alarmScheduler = AlarmScheduler(context)

        // Reschedule medicine reminders
        firestore.collection("users")
            .document(userId)
            .collection("medicines")
            .whereEqualTo("isActive", true)
            .whereEqualTo("reminderEnabled", true)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val medicineId = doc.id
                    val name = doc.getString("name") ?: ""
                    val dosage = doc.getString("dosage") ?: ""
                    val time = doc.getString("time") ?: ""

                    if (time.isNotEmpty()) {
                        alarmScheduler.scheduleMedicineReminder(medicineId, name, dosage, time)
                    }
                }
            }

        // Reschedule exercise reminders
        firestore.collection("users")
            .document(userId)
            .collection("exercises")
            .whereEqualTo("isActive", true)
            .whereEqualTo("reminderEnabled", true)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val exerciseId = doc.id
                    val name = doc.getString("name") ?: ""
                    val time = doc.getString("time") ?: ""
                    val days = doc.get("days") as? List<String> ?: listOf()

                    if (time.isNotEmpty()) {
                        alarmScheduler.scheduleExerciseReminder(exerciseId, name, time, days)
                    }
                }
            }

        // Reschedule water reminder
        alarmScheduler.scheduleWaterReminder()
    }
}