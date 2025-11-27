package com.example.healthreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthreminder.data.model.WaterIntake
import com.example.healthreminder.data.repository.WaterRepository
import kotlinx.coroutines.launch

class WaterViewModel : ViewModel() {

    private val repository = WaterRepository()

    private val _waterIntake = MutableLiveData<WaterIntake>()
    val waterIntake: LiveData<WaterIntake> = _waterIntake

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    /**
     * Load today's water intake
     */
    fun loadTodayWaterIntake() {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.getTodayWaterIntake()

            result.onSuccess { intake ->
                _waterIntake.value = intake
                _error.value = null
            }.onFailure { exception ->
                _error.value = exception.message
            }

            _loading.value = false
        }
    }

    /**
     * Add water intake
     */
    fun addWater(amount: Int) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.addWaterIntake(amount)

            result.onSuccess {
                _operationSuccess.value = true
                loadTodayWaterIntake() // Reload
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Update water goal
     */
    fun updateGoal(goal: Int) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.updateWaterGoal(goal)

            result.onSuccess {
                _operationSuccess.value = true
                loadTodayWaterIntake()
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Reset operation success flag
     */
    fun resetOperationSuccess() {
        _operationSuccess.value = false
    }
}