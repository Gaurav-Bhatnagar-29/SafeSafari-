package com.example.safarisafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.safarisafe.models.EmergencyContact
import com.example.safarisafe.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfile())
    val uiState: StateFlow<UserProfile> = _uiState.asStateFlow()

    fun updateFullName(newName: String) {
        _uiState.update { it.copy(fullName = newName) }
    }

    fun updateEmail(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun updateCountryCode(newCode: String) {
        _uiState.update { it.copy(countryCode = newCode) }
    }

    fun updatePhoneNumber(newNumber: String) {
        _uiState.update { it.copy(phoneNumber = newNumber) }
    }

    fun updateBloodType(newType: String) {
        _uiState.update { it.copy(bloodType = newType) }
    }

    fun updateAllergies(newAllergies: String) {
        _uiState.update { it.copy(knownAllergies = newAllergies) }
    }

    fun updateConditions(newConditions: String) {
        _uiState.update { it.copy(chronicConditions = newConditions) }
    }

    fun addEmergencyContact(contact: EmergencyContact) {
        _uiState.update { it.copy(emergencyContacts = it.emergencyContacts + contact) }
    }

    fun removeEmergencyContact(contact: EmergencyContact) {
        _uiState.update { it.copy(emergencyContacts = it.emergencyContacts - contact) }
    }

    fun saveProfile() {
        // Here you would typically call a repository to save data to Firebase or a database
        println("Saving profile: ${_uiState.value}")
    }
}
