package com.aicareermentor.core.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfTextExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun extractText(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Cannot open file"))
            inputStream.use { stream ->
                val document = PDDocument.load(stream)
                val text = PDFTextStripper().getText(document)
                document.close()
                if (text.isBlank()) Result.failure(Exception("No readable text found in PDF. It may be image-based."))
                else Result.success(text.trim())
            }
        } catch (e: Exception) {
            Timber.e(e, "PDF extraction failed")
            Result.failure(Exception("Failed to read PDF: ${e.localizedMessage}"))
        }
    }

    fun getFileName(uri: Uri): String {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val col = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(col)
            } ?: "resume.pdf"
        } catch (e: Exception) { "resume.pdf" }
    }
}
