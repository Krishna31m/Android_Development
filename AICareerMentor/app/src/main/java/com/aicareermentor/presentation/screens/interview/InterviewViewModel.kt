package com.aicareermentor.presentation.screens.interview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicareermentor.domain.model.AnswerEvaluation
import com.aicareermentor.domain.model.InterviewQuestion
import com.aicareermentor.domain.usecase.EvaluateAnswerUseCase
import com.aicareermentor.domain.usecase.GenerateInterviewQuestionsUseCase
import com.aicareermentor.presentation.screens.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterviewUiState(
    val selectedRole: String    = "",
    val questionsState: UiState<List<InterviewQuestion>> = UiState.Idle,
    val currentIndex: Int       = 0,
    val currentAnswer: String   = "",
    val evaluations: Map<Int, AnswerEvaluation> = emptyMap(),
    val evaluationState: UiState<AnswerEvaluation> = UiState.Idle,
    val sessionComplete: Boolean = false
)

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val generateQuestionsUseCase: GenerateInterviewQuestionsUseCase,
    private val evaluateAnswerUseCase: EvaluateAnswerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InterviewUiState())
    val state: StateFlow<InterviewUiState> = _state.asStateFlow()

    fun onRoleSelected(role: String) {
        _state.update { it.copy(selectedRole = role, questionsState = UiState.Idle) }
    }

    fun generateQuestions() {
        val role = _state.value.selectedRole
        if (role.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(questionsState = UiState.Loading) }
            generateQuestionsUseCase(role)
                .onSuccess { qs  -> _state.update { it.copy(questionsState = UiState.Success(qs), currentIndex = 0) } }
                .onFailure { err -> _state.update { it.copy(questionsState = UiState.Error(err.message ?: "Error")) } }
        }
    }

    fun onAnswerChanged(answer: String) {
        _state.update { it.copy(currentAnswer = answer) }
    }

    fun submitAnswer() {
        val s  = _state.value
        val qs = (s.questionsState as? UiState.Success)?.data ?: return
        val q  = qs.getOrNull(s.currentIndex) ?: return
        if (s.currentAnswer.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(evaluationState = UiState.Loading) }
            evaluateAnswerUseCase(q.question, s.currentAnswer, s.selectedRole)
                .onSuccess { eval ->
                    val updated = s.evaluations.toMutableMap().apply { put(s.currentIndex, eval) }
                    _state.update { it.copy(evaluationState = UiState.Success(eval), evaluations = updated) }
                }
                .onFailure { err -> _state.update { it.copy(evaluationState = UiState.Error(err.message ?: "Error")) } }
        }
    }

    fun nextQuestion() {
        val s  = _state.value
        val qs = (s.questionsState as? UiState.Success)?.data ?: return
        val next = s.currentIndex + 1
        if (next >= qs.size) {
            _state.update { it.copy(sessionComplete = true) }
        } else {
            _state.update { it.copy(currentIndex = next, currentAnswer = "", evaluationState = UiState.Idle) }
        }
    }

    fun averageScore(): Float {
        val evals = _state.value.evaluations
        return if (evals.isEmpty()) 0f else evals.values.map { it.score }.average().toFloat()
    }

    fun restart() { _state.value = InterviewUiState() }
}
