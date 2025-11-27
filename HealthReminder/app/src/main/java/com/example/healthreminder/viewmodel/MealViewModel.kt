package com.example.healthreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthreminder.data.model.Meal
import com.example.healthreminder.data.repository.MealRepository
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {

    private val repository = MealRepository()

    private val _meals = MutableLiveData<List<Meal>>()
    val meals: LiveData<List<Meal>> = _meals

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    /**
     * Load all meals
     */
    fun loadMeals() {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.getAllMeals()

            result.onSuccess { mealList ->
                _meals.value = mealList
                _error.value = null
            }.onFailure { exception ->
                _error.value = exception.message
            }

            _loading.value = false
        }
    }

    /**
     * Add new meal
     */
    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.addMeal(meal)

            result.onSuccess {
                _operationSuccess.value = true
                loadMeals()
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Update meal
     */
    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.updateMeal(meal)

            result.onSuccess {
                _operationSuccess.value = true
                loadMeals()
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Delete meal
     */
    fun deleteMeal(mealId: String) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.deleteMeal(mealId)

            result.onSuccess {
                _operationSuccess.value = true
                loadMeals()
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