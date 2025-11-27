package com.example.healthreminder.data.repository

import android.util.Log
import com.example.healthreminder.data.model.Medicine
import com.example.healthreminder.data.model.MedicineHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MedicineRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val TAG = "MedicineRepository"

    private fun getUserId(): String? = auth.currentUser?.uid

    /**
     * Get all active medicines for current user
     * ✅ FIXED: Query uses "active" because Firestore automatically converts isActive -> active
     */
    suspend fun getAllMedicines(): Result<List<Medicine>> {
        return try {
            val userId = getUserId()

            Log.d(TAG, "getAllMedicines: Getting medicines for user: $userId")

            if (userId == null) {
                Log.e(TAG, "getAllMedicines: User ID is null!")
                return Result.failure(Exception("Authentication Error: User not logged in or UID is null."))
            }

            // ✅ FIXED: Changed "isActive" to "active" - Firestore strips "is" prefix from Boolean fields
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("medicines")
                .whereEqualTo("active", true)  // Use "active" not "isActive"
                .get()
                .await()

            Log.d(TAG, "getAllMedicines: Retrieved ${snapshot.documents.size} documents from Firestore")

            // Log each document for debugging
            snapshot.documents.forEach { doc ->
                Log.d(TAG, "Document ID: ${doc.id}, Name: ${doc.get("name")}")
            }

            val medicines = snapshot.documents.mapNotNull { doc ->
                try {
                    val medicine = doc.toObject(Medicine::class.java)
                    Log.d(TAG, "Mapped medicine: ${medicine?.name}")
                    medicine
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping document ${doc.id}: ${e.message}", e)
                    null
                }
            }

            Log.d(TAG, "getAllMedicines: Returning ${medicines.size} medicines")
            Result.success(medicines)
        } catch (e: Exception) {
            Log.e(TAG, "getAllMedicines: Firestore Read Failed", e)
            Result.failure(Exception("Firestore Read Failed: ${e.message}"))
        }
    }

    /**
     * Add new medicine
     */
    suspend fun addMedicine(medicine: Medicine): Result<String> {
        return try {
            val userId = getUserId()

            if (userId == null) {
                Log.e(TAG, "addMedicine: User ID is null!")
                return Result.failure(Exception("User not logged in"))
            }

            Log.d(TAG, "addMedicine: Adding medicine for user: $userId")

            val docRef = firestore.collection("users")
                .document(userId)
                .collection("medicines")
                .document()

            val medicineWithId = medicine.copy(
                id = docRef.id,
                userId = userId,
                isActive = true
            )

            Log.d(TAG, "addMedicine: Saving medicine with ID: ${docRef.id}, Name: ${medicineWithId.name}")

            docRef.set(medicineWithId).await()

            Log.d(TAG, "addMedicine: Successfully saved medicine ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "addMedicine: Failed to save medicine", e)
            Result.failure(e)
        }
    }

    /**
     * Update medicine
     */
    suspend fun updateMedicine(medicine: Medicine): Result<Unit> {
        return try {
            val userId = getUserId()

            if (userId == null) {
                Log.e(TAG, "updateMedicine: User ID is null!")
                return Result.failure(Exception("User not logged in"))
            }

            Log.d(TAG, "updateMedicine: Updating medicine ${medicine.id}")

            val medicineWithUserId = medicine.copy(userId = userId)

            firestore.collection("users")
                .document(userId)
                .collection("medicines")
                .document(medicine.id)
                .set(medicineWithUserId)
                .await()

            Log.d(TAG, "updateMedicine: Successfully updated medicine ${medicine.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateMedicine: Failed to update medicine", e)
            Result.failure(e)
        }
    }

    /**
     * Delete medicine (soft delete)
     * ✅ FIXED: Update uses "active" field name in Firestore
     */
    suspend fun deleteMedicine(medicineId: String): Result<Unit> {
        return try {
            val userId = getUserId()

            if (userId == null) {
                Log.e(TAG, "deleteMedicine: User ID is null!")
                return Result.failure(Exception("User not logged in"))
            }

            Log.d(TAG, "deleteMedicine: Soft deleting medicine $medicineId")

            // ✅ FIXED: Changed "isActive" to "active"
            firestore.collection("users")
                .document(userId)
                .collection("medicines")
                .document(medicineId)
                .update("active", false)  // Use "active" not "isActive"
                .await()

            Log.d(TAG, "deleteMedicine: Successfully deleted medicine $medicineId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "deleteMedicine: Failed to delete medicine", e)
            Result.failure(e)
        }
    }

    /**
     * Get medicine by ID
     */
    suspend fun getMedicineById(medicineId: String): Result<Medicine> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val document = firestore.collection("users")
                .document(userId)
                .collection("medicines")
                .document(medicineId)
                .get()
                .await()

            val medicine = document.toObject(Medicine::class.java)
                ?: return Result.failure(Exception("Medicine not found or failed conversion"))

            Result.success(medicine)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMedicineHistory(history: MedicineHistory): Result<Unit> {
        return Result.success(Unit)
    }

    suspend fun getMedicineHistory(medicineId: String): Result<List<MedicineHistory>> {
        return Result.success(emptyList())
    }
}

