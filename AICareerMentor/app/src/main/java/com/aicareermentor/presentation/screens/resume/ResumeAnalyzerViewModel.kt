package com.aicareermentor.presentation.screens.resume

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicareermentor.core.utils.PdfTextExtractor
import com.aicareermentor.domain.model.ResumeAnalysis
import com.aicareermentor.domain.usecase.AnalyzeResumeUseCase
import com.aicareermentor.presentation.screens.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResumeUiState(
    val fileName: String        = "",
    val extractedText: String   = "",
    val extractionError: String? = null,
    val analysisState: UiState<ResumeAnalysis> = UiState.Idle
)

@HiltViewModel
class ResumeAnalyzerViewModel @Inject constructor(
    private val analyzeResumeUseCase: AnalyzeResumeUseCase,
    private val pdfExtractor: PdfTextExtractor
) : ViewModel() {

    private val _state = MutableStateFlow(ResumeUiState())
    val state: StateFlow<ResumeUiState> = _state.asStateFlow()

    fun onPdfSelected(uri: Uri) {
        viewModelScope.launch {
            val name = pdfExtractor.getFileName(uri)
            _state.update { it.copy(fileName = name, extractionError = null) }
            pdfExtractor.extractText(uri)
                .onSuccess { text  -> _state.update { it.copy(extractedText = text) } }
                .onFailure { error -> _state.update { it.copy(extractionError = error.message) } }
        }
    }

    fun analyzeResume() {
        val text = _state.value.extractedText
        if (text.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(analysisState = UiState.Loading) }
            analyzeResumeUseCase(text)
                .onSuccess { result -> _state.update { it.copy(analysisState = UiState.Success(result)) } }
                .onFailure { error  -> _state.update { it.copy(analysisState = UiState.Error(error.message ?: "Unknown error")) } }
        }
    }

    fun reset() { _state.value = ResumeUiState() }
}
