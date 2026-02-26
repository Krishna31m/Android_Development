package com.aicareermentor.presentation.screens.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicareermentor.domain.model.CareerRoadmap
import com.aicareermentor.domain.usecase.GenerateCareerRoadmapUseCase
import com.aicareermentor.presentation.screens.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoadmapUiState(
    val selectedDomain: String = "",
    val selectedLevel: String  = "Beginner",
    val roadmapState: UiState<CareerRoadmap> = UiState.Idle
)

@HiltViewModel
class CareerRoadmapViewModel @Inject constructor(
    private val generateRoadmapUseCase: GenerateCareerRoadmapUseCase
) : ViewModel() {

    val levels = listOf("Beginner", "Intermediate", "Advanced")

    private val _state = MutableStateFlow(RoadmapUiState())
    val state: StateFlow<RoadmapUiState> = _state.asStateFlow()

    fun onDomainSelected(domain: String) { _state.update { it.copy(selectedDomain = domain, roadmapState = UiState.Idle) } }
    fun onLevelSelected(level: String)   { _state.update { it.copy(selectedLevel = level) } }

    fun generateRoadmap() {
        val s = _state.value
        if (s.selectedDomain.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(roadmapState = UiState.Loading) }
            generateRoadmapUseCase(s.selectedDomain, s.selectedLevel)
                .onSuccess { r   -> _state.update { it.copy(roadmapState = UiState.Success(r)) } }
                .onFailure { err -> _state.update { it.copy(roadmapState = UiState.Error(err.message ?: "Error")) } }
        }
    }
}
