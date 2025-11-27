package com.example.healthreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthreminder.data.model.Exercise
import com.example.healthreminder.data.repository.ExerciseRepository
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {

    private val repository = ExerciseRepository()

    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> = _exercises

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    /**
     * Load all exercises
     */
    fun loadExercises() {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.getAllExercises()

            result.onSuccess { exerciseList ->
                _exercises.value = exerciseList
                _error.value = null
            }.onFailure { exception ->
                _error.value = exception.message
            }

            _loading.value = false
        }
    }

    /**
     * Add new exercise
     */
    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.addExercise(exercise)

            result.onSuccess {
                _operationSuccess.value = true
                loadExercises()
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Update exercise
     */
    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.updateExercise(exercise)

            result.onSuccess {
                _operationSuccess.value = true
                loadExercises()
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Delete exercise
     */
    fun deleteExercise(exerciseId: String) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.deleteExercise(exerciseId)

            result.onSuccess {
                _operationSuccess.value = true
                loadExercises()
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetOperationSuccess() {
        _operationSuccess.value = false
    }
}