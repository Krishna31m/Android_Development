package com.example.healthreminder.data.repository

import com.example.healthreminder.data.model.Meal
import com.example.healthreminder.data.model.MealHistory
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class MealRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    /**
     * Get all meals for current user
     */
    suspend fun getAllMeals(): Result<List<Meal>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("meals")
                .get()
                .await()

            val meals = snapshot.documents.mapNotNull { it.toObject(Meal::class.java) }
            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get meal by ID
     */
    suspend fun getMealById(mealId: String): Result<Meal> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val document = firestore.collection("users")
                .document(userId)
                .collection("meals")
                .document(mealId)
                .get()
                .await()

            val meal = document.toObject(Meal::class.java)
                ?: return Result.failure(Exception("Meal not found"))

            Result.success(meal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get meals by type
     */
    suspend fun getMealsByType(mealType: String): Result<List<Meal>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("meals")
                .whereEqualTo("mealType", mealType)
                .get()
                .await()

            val meals = snapshot.documents.mapNotNull { it.toObject(Meal::class.java) }
            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add new meal
     */
    suspend fun addMeal(meal: Meal): Result<String> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val docRef = firestore.collection("users")
                .document(userId)
                .collection("meals")
                .document()

            val mealWithId = meal.copy(id = docRef.id, userId = userId)

            docRef.set(mealWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update meal
     */
    suspend fun updateMeal(meal: Meal): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("meals")
                .document(meal.id)
                .set(meal)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete meal
     */
    suspend fun deleteMeal(mealId: String): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("meals")
                .document(mealId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add meal history entry
     */
    suspend fun addMealHistory(history: MealHistory): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("mealHistory")
                .add(history)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get meal history for specific meal
     */
    suspend fun getMealHistory(mealId: String): Result<List<MealHistory>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("mealHistory")
                .whereEqualTo("mealId", mealId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val history = snapshot.documents.mapNotNull { it.toObject(MealHistory::class.java) }
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark meal as completed
     */
    suspend fun markMealCompleted(
        mealId: String,
        actualCalories: Int = 0,
        note: String = ""
    ): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val history = MealHistory(
                mealId = mealId,
                date = Timestamp.now(),
                completed = true,
                actualCalories = actualCalories,
                note = note
            )

            firestore.collection("users")
                .document(userId)
                .collection("mealHistory")
                .add(history)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get today's completed meals count
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
                .collection("mealHistory")
                .whereEqualTo("completed", true)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .get()
                .await()

            Result.success(snapshot.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get total calories consumed today
     */
    suspend fun getTodayTotalCalories(): Result<Int> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            val startOfDay = Timestamp(calendar.time)

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("mealHistory")
                .whereEqualTo("completed", true)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .get()
                .await()

            var totalCalories = 0
            for (doc in snapshot.documents) {
                val calories = doc.getLong("actualCalories")?.toInt() ?: 0
                totalCalories += calories
            }

            Result.success(totalCalories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get meal plan for today (sorted by meal type)
     */
    suspend fun getTodayMealPlan(): Result<List<Meal>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("meals")
                .get()
                .await()

            val meals = snapshot.documents.mapNotNull { it.toObject(Meal::class.java) }

            // Sort by meal type order (Breakfast, Lunch, Dinner, Snack)
            val mealOrder = mapOf(
                "Breakfast" to 1,
                "Lunch" to 2,
                "Dinner" to 3,
                "Snack" to 4
            )

            val sortedMeals = meals.sortedBy { mealOrder[it.mealType] ?: 5 }

            Result.success(sortedMeals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}