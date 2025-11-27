package com.example.healthreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthreminder.data.model.Medicine
import com.example.healthreminder.data.repository.MedicineRepository
import kotlinx.coroutines.launch

class MedicineViewModel : ViewModel() {

    private val repository = MedicineRepository()

    private val _medicines = MutableLiveData<List<Medicine>>()
    val medicines: LiveData<List<Medicine>> = _medicines

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    /**
     * Load all medicines
     */
    fun loadMedicines() {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.getAllMedicines()

            result.onSuccess { medicineList ->
                _medicines.value = medicineList
                _error.value = null
            }.onFailure { exception ->
                _error.value = exception.message
            }

            _loading.value = false
        }
    }

    /**
     * Add new medicine
     */
    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.addMedicine(medicine)

            result.onSuccess {
                _operationSuccess.value = true
                loadMedicines() // Reload list
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Update medicine
     */
    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.updateMedicine(medicine)

            result.onSuccess {
                _operationSuccess.value = true
                loadMedicines()
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Delete medicine
     */
    fun deleteMedicine(medicineId: String) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.deleteMedicine(medicineId)

            result.onSuccess {
                _operationSuccess.value = true
                loadMedicines()
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