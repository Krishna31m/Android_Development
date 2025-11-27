package com.example.healthreminder.data.repository

import android.util.Log
import com.example.healthreminder.data.model.Exercise
import com.example.healthreminder.data.model.ExerciseHistory
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ExerciseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "ExerciseRepository"

    private fun getUserId(): String? = auth.currentUser?.uid

    /**
     * Get all active exercises for current user
     * ✅ FIXED: Changed "isActive" to "active"
     */
    suspend fun getAllExercises(): Result<List<Exercise>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            Log.d(TAG, "getAllExercises: Getting exercises for user: $userId")

            // ✅ FIXED: Changed "isActive" to "active"
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("exercises")
                .whereEqualTo("active", true)  // Changed from "isActive"
                .get()
                .await()

            Log.d(TAG, "getAllExercises: Retrieved ${snapshot.documents.size} documents")

            val exercises = snapshot.documents.mapNotNull { it.toObject(Exercise::class.java) }

            Log.d(TAG, "getAllExercises: Returning ${exercises.size} exercises")
            Result.success(exercises)
        } catch (e: Exception) {
            Log.e(TAG, "getAllExercises: Error - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get exercise by ID
     */
    suspend fun getExerciseById(exerciseId: String): Result<Exercise> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val document = firestore.collection("users")
                .document(userId)
                .collection("exercises")
                .document(exerciseId)
                .get()
                .await()

            val exercise = document.toObject(Exercise::class.java)
                ?: return Result.failure(Exception("Exercise not found"))

            Result.success(exercise)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add new exercise
     */
    suspend fun addExercise(exercise: Exercise): Result<String> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val docRef = firestore.collection("users")
                .document(userId)
                .collection("exercises")
                .document()

            val exerciseWithId = exercise.copy(
                id = docRef.id,
                userId = userId,
                isActive = true
            )

            Log.d(TAG, "addExercise: Saving exercise ${docRef.id}, Name: ${exerciseWithId.name}")

            docRef.set(exerciseWithId).await()

            Log.d(TAG, "addExercise: Successfully saved exercise ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "addExercise: Error - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Update exercise
     */
    suspend fun updateExercise(exercise: Exercise): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("exercises")
                .document(exercise.id)
                .set(exercise)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete exercise (soft delete)
     * ✅ FIXED: Changed "isActive" to "active"
     */
    suspend fun deleteExercise(exerciseId: String): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            Log.d(TAG, "deleteExercise: Soft deleting exercise $exerciseId")

            // ✅ FIXED: Changed "isActive" to "active"
            firestore.collection("users")
                .document(userId)
                .collection("exercises")
                .document(exerciseId)
                .update("active", false)  // Changed from "isActive"
                .await()

            Log.d(TAG, "deleteExercise: Successfully deleted exercise $exerciseId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "deleteExercise: Error - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Add exercise history entry
     */
    suspend fun addExerciseHistory(history: ExerciseHistory): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("exerciseHistory")
                .add(history)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get exercise history for specific exercise
     */
    suspend fun getExerciseHistory(exerciseId: String): Result<List<ExerciseHistory>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("exerciseHistory")
                .whereEqualTo("exerciseId", exerciseId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val history = snapshot.documents.mapNotNull { it.toObject(ExerciseHistory::class.java) }
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get exercises for specific day
     * ✅ FIXED: Changed "isActive" to "active"
     */
    suspend fun getExercisesForDay(dayOfWeek: String): Result<List<Exercise>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            // ✅ FIXED: Changed "isActive" to "active"
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("exercises")
                .whereEqualTo("active", true)  // Changed from "isActive"
                .whereArrayContains("days", dayOfWeek)
                .get()
                .await()

            val exercises = snapshot.documents.mapNotNull { it.toObject(Exercise::class.java) }
            Result.success(exercises)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark exercise as completed for today
     */
    suspend fun markExerciseCompleted(exerciseId: String, duration: Int, note: String = ""): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val history = ExerciseHistory(
                exerciseId = exerciseId,
                date = Timestamp.now(),
                completed = true,
                duration = duration,
                note = note
            )

            firestore.collection("users")
                .document(userId)
                .collection("exerciseHistory")
                .add(history)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get today's completed exercises count
     */
    suspend fun getTodayCompletedCount(): Result<Int> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            val startOfDay = Timestamp(calendar.time)

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("exerciseHistory")
                .whereEqualTo("completed", true)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .get()
                .await()

            Result.success(snapshot.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
