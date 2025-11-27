package com.example.healthreminder.data.repository

import com.example.healthreminder.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    /**
     * Get user profile
     */
    suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val profile = document.toObject(UserProfile::class.java)
                ?: return Result.failure(Exception("Profile not found"))

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create user profile
     */
    suspend fun createUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val profileWithId = profile.copy(id = userId)

            firestore.collection("users")
                .document(userId)
                .set(profileWithId)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(updates: Map<String, Any>): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update emergency card info
     */
    suspend fun updateEmergencyCard(
        bloodGroup: String,
        allergies: String,
        medicalConditions: String,
        emergencyContactName: String,
        emergencyContact: String
    ): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val updates = hashMapOf(
                "bloodGroup" to bloodGroup,
                "allergies" to allergies,
                "medicalConditions" to medicalConditions,
                "emergencyContactName" to emergencyContactName,
                "emergencyContact" to emergencyContact
            )

            firestore.collection("users")
                .document(userId)
                .update(updates as Map<String, Any>)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if user profile exists
     */
    suspend fun profileExists(): Result<Boolean> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            Result.success(document.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete user account and all data
     */
    suspend fun deleteUserAccount(): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            // Delete all subcollections
            val collections = listOf(
                "medicines", "medicineHistory", "waterIntake",
                "exercises", "exerciseHistory", "meals", "mealHistory",
                "mood", "doctorVisits", "challenges", "healthStats"
            )

            for (collection in collections) {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection(collection)
                    .get()
                    .await()

                for (doc in snapshot.documents) {
                    doc.reference.delete().await()
                }
            }

            // Delete user profile
            firestore.collection("users")
                .document(userId)
                .delete()
                .await()

            // Delete authentication
            auth.currentUser?.delete()?.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}