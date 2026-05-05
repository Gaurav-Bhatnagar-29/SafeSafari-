package com.example.safarisafe.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.safarisafe.models.EmergencyContact
import com.example.safarisafe.models.UserProfile
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("safari_safe_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _uiState = MutableStateFlow(loadProfileFromPrefs())
    val uiState: StateFlow<UserProfile> = _uiState.asStateFlow()

    private fun loadProfileFromPrefs(): UserProfile {
        val json = sharedPreferences.getString("user_profile", null)
        return if (json != null) {
            try {
                gson.fromJson(json, UserProfile::class.java)
            } catch (e: Exception) {
                UserProfile()
            }
        } else {
            UserProfile()
        }
    }

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

    fun updatePassportNumber(newNumber: String) {
        _uiState.update { it.copy(passportNumber = newNumber) }
    }

    fun updateNationality(newNationality: String) {
        _uiState.update { it.copy(nationality = newNationality) }
    }

    fun updateGoogleDriveLink(newLink: String) {
        _uiState.update { it.copy(googleDriveLink = newLink) }
    }

    fun addEmergencyContact(contact: EmergencyContact) {
        _uiState.update { it.copy(emergencyContacts = it.emergencyContacts + contact) }
    }

    fun removeEmergencyContact(contact: EmergencyContact) {
        _uiState.update { it.copy(emergencyContacts = it.emergencyContacts - contact) }
    }

    fun updateProfileImage(uri: String) {
        _uiState.update { it.copy(profileImageUri = uri) }
    }

    fun saveBitmapAndGetUri(bitmap: android.graphics.Bitmap): String? {
        val filename = "profile_image_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(getApplication<Application>().filesDir, filename)
        return try {
            java.io.FileOutputStream(file).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveProfile() {
        val json = gson.toJson(_uiState.value)
        sharedPreferences.edit().putString("user_profile", json).apply()
        println("Profile saved to SharedPreferences: ${_uiState.value}")
    }
}
