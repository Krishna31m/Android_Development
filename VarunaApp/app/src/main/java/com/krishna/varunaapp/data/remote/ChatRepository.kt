package com.krishna.varunaapp.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.krishna.varunaapp.data.local.DiseaseDao
import com.krishna.varunaapp.data.local.DiseaseEntity
import com.krishna.varunaapp.data.models.Disease
import com.krishna.varunaapp.data.models.FAQ
import com.krishna.varunaapp.data.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatRepository(
    private val firestore: FirebaseFirestore,
    private val diseaseDao: DiseaseDao
) {

    suspend fun fetchDiseases(): Result<List<Disease>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("diseases").get().await()
            val diseases = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Disease::class.java)?.copy(id = doc.id)
            }

            val entities = diseases.map { disease ->
                DiseaseEntity(
                    id = disease.id,
                    name = disease.name,
                    description = disease.description,
                    causes = disease.causes.joinToString("|"),
                    symptoms = disease.symptoms.joinToString("|"),
                    prevention = disease.prevention.joinToString("|"),
                    severity = disease.severity,
                    keywords = disease.keywords.joinToString("|")
                )
            }
            diseaseDao.insertDiseases(entities)

            Result.success(diseases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedDiseases(): List<Disease> = withContext(Dispatchers.IO) {
        diseaseDao.getAllDiseases().map { entity ->
            Disease(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                causes = entity.causes.split("|"),
                symptoms = entity.symptoms.split("|"),
                prevention = entity.prevention.split("|"),
                severity = entity.severity,
                keywords = entity.keywords.split("|")
            )
        }
    }

    suspend fun saveChatMessage(userId: String, message: Message): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val chatRef = firestore.collection("chat_logs").document(userId)
            chatRef.update("messages", FieldValue.arrayUnion(message)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            try {
                val chatRef = firestore.collection("chat_logs").document(userId)
                chatRef.set(mapOf("messages" to listOf(message))).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchFAQs(): Result<List<FAQ>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("faqs").get().await()
            val faqs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FAQ::class.java)
            }
            Result.success(faqs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}