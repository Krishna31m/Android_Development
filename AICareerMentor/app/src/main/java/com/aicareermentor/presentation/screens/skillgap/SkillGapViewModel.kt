package com.aicareermentor.presentation.screens.skillgap

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicareermentor.core.utils.PdfTextExtractor
import com.aicareermentor.domain.model.SkillGapAnalysis
import com.aicareermentor.domain.usecase.AnalyzeSkillGapUseCase
import com.aicareermentor.presentation.screens.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SkillGapUiState(
    val resumeText: String      = "",
    val resumeFileName: String  = "",
    val selectedRole: String    = "",
    val analysisState: UiState<SkillGapAnalysis> = UiState.Idle
)

@HiltViewModel
class SkillGapViewModel @Inject constructor(
    private val analyzeSkillGapUseCase: AnalyzeSkillGapUseCase,
    private val pdfExtractor: PdfTextExtractor
) : ViewModel() {

    private val _state = MutableStateFlow(SkillGapUiState())
    val state: StateFlow<SkillGapUiState> = _state.asStateFlow()

    fun onPdfSelected(uri: Uri) {
        viewModelScope.launch {
            val name = pdfExtractor.getFileName(uri)
            pdfExtractor.extractText(uri).onSuccess { text ->
                _state.update { it.copy(resumeText = text, resumeFileName = name) }
            }
        }
    }

    fun onRoleSelected(role: String) {
        _state.update { it.copy(selectedRole = role, analysisState = UiState.Idle) }
    }

    fun analyze() {
        val s = _state.value
        if (s.selectedRole.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(analysisState = UiState.Loading) }
            analyzeSkillGapUseCase(s.resumeText, s.selectedRole)
                .onSuccess { result -> _state.update { it.copy(analysisState = UiState.Success(result)) } }
                .onFailure { error  -> _state.update { it.copy(analysisState = UiState.Error(error.message ?: "Error")) } }
        }
    }
}
