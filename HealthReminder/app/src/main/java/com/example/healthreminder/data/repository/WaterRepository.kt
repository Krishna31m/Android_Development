package com.example.healthreminder.data.repository

import com.example.healthreminder.data.model.WaterIntake
import com.example.healthreminder.data.model.WaterLog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class WaterRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    /**
     * Get today's water intake
     */
    suspend fun getTodayWaterIntake(): Result<WaterIntake> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val document = firestore.collection("users")
                .document(userId)
                .collection("waterIntake")
                .document(today)
                .get()
                .await()

            if (document.exists()) {
                val waterIntake = document.toObject(WaterIntake::class.java)
                    ?: return Result.failure(Exception("Failed to parse water intake"))
                Result.success(waterIntake)
            } else {
                // Create new entry for today
                val newIntake = WaterIntake(
                    id = today,
                    userId = userId,
                    date = today,
                    goal = 3000,
                    consumed = 0,
                    logs = listOf()
                )
                firestore.collection("users")
                    .document(userId)
                    .collection("waterIntake")
                    .document(today)
                    .set(newIntake)
                    .await()

                Result.success(newIntake)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add water intake
     */
    suspend fun addWaterIntake(amount: Int): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val documentRef = firestore.collection("users")
                .document(userId)
                .collection("waterIntake")
                .document(today)

            val document = documentRef.get().await()

            val waterLog = WaterLog(amount = amount, timestamp = Timestamp.now())

            if (document.exists()) {
                val currentConsumed = document.getLong("consumed")?.toInt() ?: 0
                val logs = document.get("logs") as? List<Map<String, Any>> ?: listOf()

                val updatedLogs = logs.toMutableList()
                updatedLogs.add(
                    mapOf(
                        "amount" to amount,
                        "timestamp" to Timestamp.now()
                    )
                )

                documentRef.update(
                    mapOf(
                        "consumed" to (currentConsumed + amount),
                        "logs" to updatedLogs
                    )
                ).await()
            } else {
                val newIntake = WaterIntake(
                    id = today,
                    userId = userId,
                    date = today,
                    goal = 3000,
                    consumed = amount,
                    logs = listOf(waterLog)
                )
                documentRef.set(newIntake).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update water goal
     */
    suspend fun updateWaterGoal(goal: Int): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            firestore.collection("users")
                .document(userId)
                .collection("waterIntake")
                .document(today)
                .update("goal", goal)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get water intake history for a date range
     */
    suspend fun getWaterIntakeHistory(startDate: String, endDate: String): Result<List<WaterIntake>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("waterIntake")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .await()

            val history = snapshot.documents.mapNotNull { it.toObject(WaterIntake::class.java) }
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get weekly water intake
     */
    suspend fun getWeeklyWaterIntake(): Result<List<WaterIntake>> {
        return try {
            val calendar = Calendar.getInstance()
            val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            getWaterIntakeHistory(startDate, endDate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Reset today's water intake
     */
    suspend fun resetTodayWaterIntake(): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            firestore.collection("users")
                .document(userId)
                .collection("waterIntake")
                .document(today)
                .update(
                    mapOf(
                        "consumed" to 0,
                        "logs" to listOf<WaterLog>()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}