package com.example.healthreminder.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.healthreminder.R
import com.example.healthreminder.utils.NotificationHelper

class ExerciseAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val exerciseId = intent.getStringExtra("EXERCISE_ID") ?: return
        val exerciseName = intent.getStringExtra("EXERCISE_NAME") ?: "Exercise"

        val notificationHelper = NotificationHelper(context)
        notificationHelper.showExerciseNotification(exerciseId, exerciseName)
    }
}
//class ExerciseAlarmReceiver : BroadcastReceiver() {
//
//    companion object {
//        private const val CHANNEL_ID = "exercise_reminder_channel"
//    }
//
//    override fun onReceive(context: Context, intent: Intent) {
//
//        val exerciseName = intent.getStringExtra("exerciseName") ?: "Workout"
//
//        val messages = listOf(
//            "Time to move your body!",
//            "Stay strong, stay healthy!",
//            "Small steps lead to big changes!",
//            "Keep going, you're doing great!",
//            "Your body will thank you later!",
//            "Push yourself, no one else will!"
//        )
//
//        val randomMessage = messages.random()
//
//        createNotificationChannel(context)
//
//        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_exercise)
//            .setContentTitle("Exercise Reminder")
//            .setContentText("$exerciseName • $randomMessage")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//
//        val manager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        manager.notify(System.currentTimeMillis().toInt(), builder.build())
//    }
//
//    private fun createNotificationChannel(context: Context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "Exercise Reminders",
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = "Daily exercise workout reminder notifications"
//            }
//
//            val manager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            manager.createNotificationChannel(channel)
//        }
//    }
//}
