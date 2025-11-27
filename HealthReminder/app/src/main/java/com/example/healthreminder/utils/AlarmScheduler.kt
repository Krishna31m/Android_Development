package com.example.healthreminder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.healthreminder.receivers.ExerciseAlarmReceiver
import com.example.healthreminder.receivers.MedicineAlarmReceiver
import com.example.healthreminder.receivers.WaterReminderReceiver
import java.util.*

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedule exact alarm for medicine reminder
     */
    fun scheduleMedicineReminder(
        medicineId: String,
        medicineName: String,
        dosage: String,
        time: String // Format: HH:mm
    ) {
        val calendar = Calendar.getInstance().apply {
            val timeParts = time.split(":")
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            putExtra("MEDICINE_ID", medicineId)
            putExtra("MEDICINE_NAME", medicineName)
            putExtra("DOSAGE", dosage)
            putExtra("TYPE", "MEDICINE")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicineId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule exact alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    /**
     * Schedule repeating water reminder
     */
    fun scheduleWaterReminder(intervalMinutes: Long = 120) { // Every 2 hours
        val intent = Intent(context, WaterReminderReceiver::class.java).apply {
            putExtra("TYPE", "WATER")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            WATER_REMINDER_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intervalMillis = intervalMinutes * 60 * 1000
        val triggerTime = System.currentTimeMillis() + intervalMillis

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            intervalMillis,
            pendingIntent
        )
    }

    /**
     * Schedule exercise reminder
     */
    fun scheduleExerciseReminder(
        exerciseId: String,
        exerciseName: String,
        time: String,
        days: List<String>
    ) {
        val calendar = Calendar.getInstance().apply {
            val timeParts = time.split(":")
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(context, ExerciseAlarmReceiver::class.java).apply {
            putExtra("EXERCISE_ID", exerciseId)
            putExtra("EXERCISE_NAME", exerciseName)
            putExtra("TYPE", "EXERCISE")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            exerciseId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule for each selected day
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    /**
     * Cancel alarm
     */
    fun cancelAlarm(requestCode: Int, receiverClass: Class<*>) {
        val intent = Intent(context, receiverClass)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val WATER_REMINDER_ID = 1001
        const val MORNING_MOTIVATION_ID = 1002
    }
}