package com.example.healthreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthreminder.data.model.UserProfile
import com.example.healthreminder.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    /**
     * Load user profile
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.getUserProfile()

            result.onSuccess { profile ->
                _userProfile.value = profile
                _error.value = null
            }.onFailure { exception ->
                _error.value = exception.message
            }

            _loading.value = false
        }
    }

    /**
     * Update user profile
     */
    fun updateProfile(updates: Map<String, Any>) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.updateUserProfile(updates)

            result.onSuccess {
                _operationSuccess.value = true
                loadUserProfile()
            }.onFailure { exception ->
                _error.value = exception.message
                _operationSuccess.value = false
            }

            _loading.value = false
        }
    }

    /**
     * Update emergency card
     */
    fun updateEmergencyCard(
        bloodGroup: String,
        allergies: String,
        medicalConditions: String,
        emergencyContactName: String,
        emergencyContact: String
    ) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.updateEmergencyCard(
                bloodGroup,
                allergies,
                medicalConditions,
                emergencyContactName,
                emergencyContact
            )

            result.onSuccess {
                _operationSuccess.value = true
                loadUserProfile()
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