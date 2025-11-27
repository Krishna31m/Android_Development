package com.example.healthreminder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.healthreminder.ui.main.MainActivity
import com.example.healthreminder.R
import com.example.healthreminder.receivers.MedicineActionReceiver

class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Medicine Channel
            val medicineChannel = NotificationChannel(
                MEDICINE_CHANNEL_ID,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for medicine reminders"
                enableVibration(true)
            }

            // Water Channel
            val waterChannel = NotificationChannel(
                WATER_CHANNEL_ID,
                "Water Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for water intake reminders"
            }

            // Exercise Channel
            val exerciseChannel = NotificationChannel(
                EXERCISE_CHANNEL_ID,
                "Exercise Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for exercise reminders"
            }

            // Diet Channel
            val dietChannel = NotificationChannel(
                DIET_CHANNEL_ID,
                "Diet Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for meal reminders"
            }

            // Doctor Channel
            val doctorChannel = NotificationChannel(
                DOCTOR_CHANNEL_ID,
                "Doctor Appointments",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for doctor appointments"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(medicineChannel)
            manager?.createNotificationChannel(waterChannel)
            manager?.createNotificationChannel(exerciseChannel)
            manager?.createNotificationChannel(dietChannel)
            manager?.createNotificationChannel(doctorChannel)
        }
    }

    /**
     * Show medicine reminder notification
     */
    fun showMedicineNotification(
        medicineId: String,
        medicineName: String,
        dosage: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_MEDICINE", medicineId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            medicineId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action buttons
        val takenIntent = Intent(context, MedicineActionReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("MEDICINE_ID", medicineId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            medicineId.hashCode() + 1,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val skipIntent = Intent(context, MedicineActionReceiver::class.java).apply {
            action = "ACTION_SKIP"
            putExtra("MEDICINE_ID", medicineId)
        }
        val skipPendingIntent = PendingIntent.getBroadcast(
            context,
            medicineId.hashCode() + 2,
            skipIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, MEDICINE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle("💊 Medicine Reminder")
            .setContentText("Time to take $medicineName ($dosage)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Time to take $medicineName ($dosage)\n\nDon't forget your medication!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_check, "Taken", takenPendingIntent)
            .addAction(R.drawable.ic_close, "Skip", skipPendingIntent)
            .build()

        notificationManager.notify(medicineId.hashCode(), notification)
    }

    /**
     * Show water reminder notification
     */
    fun showWaterReminderNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_WATER", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            WATER_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val messages = listOf(
            "Stay hydrated! 💧",
            "Time to drink water! 🚰",
            "Don't forget to hydrate! 💦",
            "Your body needs water! 💙"
        )

        val notification = NotificationCompat.Builder(context, WATER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Water Reminder")
            .setContentText(messages.random())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(WATER_NOTIFICATION_ID, notification)
    }

    /**
     * Show exercise reminder notification
     */
    fun showExerciseNotification(exerciseId: String, exerciseName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_EXERCISE", exerciseId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            exerciseId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val motivationalMessages = listOf(
            "Let's get moving! 💪",
            "Your body will thank you! 🏃",
            "Time to workout! 🏋️",
            "Stay active, stay healthy! 🚴"
        )

        val notification = NotificationCompat.Builder(context, EXERCISE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_exercise)
            .setContentTitle("Exercise Time!")
            .setContentText("$exerciseName - ${motivationalMessages.random()}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(exerciseId.hashCode(), notification)
    }

    /**
     * Show diet/meal reminder notification
     */
    fun showMealNotification(mealId: String, mealType: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_DIET", mealId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            mealId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, DIET_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_food)
            .setContentTitle("Meal Reminder 🍽️")
            .setContentText("Time for your $mealType!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(mealId.hashCode(), notification)
    }

    /**
     * Show doctor appointment notification
     */
    fun showDoctorAppointmentNotification(
        appointmentId: String,
        doctorName: String,
        time: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_DOCTOR", appointmentId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            appointmentId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, DOCTOR_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_doctor)
            .setContentTitle("Doctor Appointment Reminder")
            .setContentText("Appointment with Dr. $doctorName at $time")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("You have an appointment with Dr. $doctorName at $time\n\nDon't forget to bring your medical records!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(appointmentId.hashCode(), notification)
    }

    companion object {
        const val MEDICINE_CHANNEL_ID = "medicine_channel"
        const val WATER_CHANNEL_ID = "water_channel"
        const val EXERCISE_CHANNEL_ID = "exercise_channel"
        const val DIET_CHANNEL_ID = "diet_channel"
        const val DOCTOR_CHANNEL_ID = "doctor_channel"

        const val WATER_NOTIFICATION_ID = 2001
    }
}