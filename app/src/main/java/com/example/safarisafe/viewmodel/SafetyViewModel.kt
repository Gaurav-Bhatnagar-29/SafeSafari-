package com.example.safarisafe.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.safarisafe.models.*
import com.example.safarisafe.network.WeatherNetwork
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

import android.telephony.SmsManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.widget.Toast

class SafetyViewModel(application: Application) : AndroidViewModel(application) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val sharedPreferences = application.getSharedPreferences("safari_safe_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _identityState = MutableStateFlow(IdentityUiState())
    val identityState: StateFlow<IdentityUiState> = _identityState.asStateFlow()

    private val _hazardState = MutableStateFlow(HazardUiState())
    val hazardState: StateFlow<HazardUiState> = _hazardState.asStateFlow()

    private val _safeStatusState = MutableStateFlow(SafeStatusUiState())
    val safeStatusState: StateFlow<SafeStatusUiState> = _safeStatusState.asStateFlow()

    private val _exploreState = MutableStateFlow(ExploreUiState())
    val exploreState: StateFlow<ExploreUiState> = _exploreState.asStateFlow()

    init {
        loadMockData()
        loadIdentityFromPrefs()
    }

    fun sendSosAlert(context: Context) {
        val contact = identityState.value.profile?.emergencyContactPhone
        if (contact.isNullOrBlank()) {
            Toast.makeText(context, "No emergency contact found! Update your profile.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                        val message = "EMERGENCY! I need help. My current location is: $mapsUrl"
                        
                        sendSilentSms(contact, message, context)
                        dispatchWhatsApp(context, message)
                    } else {
                        Toast.makeText(context, "Could not fetch location. Is GPS on?", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Location Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsAppChat(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
        val uri = android.net.Uri.parse("https://wa.me/$cleanNumber")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp is not installed!", Toast.LENGTH_SHORT).show()
        }
    }

    fun dispatchWhatsApp(context: Context, message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp is not installed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSilentSms(phoneNumber: String, message: String, context: Context) {
        try {
            val smsManager: SmsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "SOS Sent to $phoneNumber!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "SMS Failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadIdentityFromPrefs() {
        val json = sharedPreferences.getString("user_profile", null)
        if (json != null) {
            try {
                val userProfile = gson.fromJson(json, UserProfile::class.java)
                updateFromUserProfile(userProfile)
            } catch (e: Exception) {
                // Ignore or log
            }
        }
    }

    private fun loadMockData() {
        _hazardState.update {
            it.copy(
                activeAlert = Alert(
                    title = "Active Wildfire",
                    description = "Evacuate North",
                    distance = "500m",
                    isCritical = true,
                    recommendedActions = listOf(
                        ActionItemData(Icons.Default.Face, "Equip protective face mask"),
                        ActionItemData(Icons.Default.DirectionsCar, "Avoid Canyon Blvd route")
                    )
                )
            )
        }

        _exploreState.update {
            it.copy(
                currentLocation = LocationSafety(
                    locationName = "Central Park West",
                    safetyScore = 98,
                    isVerified = true,
                    features = listOf("Well-lit at night", "Regular police patrols"),
                    latitude = 40.785091,
                    longitude = -73.968285
                )
            )
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _exploreState.update { it.copy(searchQuery = newQuery) }
    }

    fun performSearch(context: Context) {
        val query = exploreState.value.searchQuery
        if (query.isBlank()) return

        viewModelScope.launch {
            try {
                val addressList = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context)
                    geocoder.getFromLocationName(query, 1)
                }

                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]

                    _exploreState.update { currentState ->
                        currentState.copy(
                            currentLocation = LocationSafety(
                                locationName = address.featureName ?: query,
                                safetyScore = (60..100).random(),
                                isVerified = true,
                                features = listOf("Searched Destination"),
                                latitude = address.latitude,
                                longitude = address.longitude
                            )
                        )
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun fetchLiveWeather(lat: Double, lng: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = WeatherNetwork.service.getCurrentConditions(apiKey, lat, lng)
                val realTemp = "${response.temperature.degrees}°C"
                val realCondition = response.weatherCondition.description.text

                _safeStatusState.update { it.copy(
                    temperature = realTemp,
                    weatherCondition = realCondition
                )}
            } catch (e: Exception) {
                println("Weather API Error: ${e.message}")
            }
        }
    }

    fun triggerSos() {
        println("SOS Triggered!")
    }

    fun getEvacuationRoute() {
        println("Calculating safe route...")
    }

    fun updateFromUserProfile(userProfile: UserProfile) {
        _identityState.update { state ->
            state.copy(
                profile = IdentityProfile(
                    name = userProfile.fullName,
                    nationality = userProfile.nationality,
                    documentNumber = userProfile.passportNumber,
                    bloodType = userProfile.bloodType,
                    bloodTypeDetail = "Verified",
                    allergies = userProfile.knownAllergies,
                    allergiesDetail = if (userProfile.knownAllergies.isNotBlank()) "Active Risk" else "None reported",
                    conditions = userProfile.chronicConditions,
                    conditionsDetail = if (userProfile.chronicConditions.isNotBlank()) "Managing" else "None reported",
                    emergencyContactName = userProfile.emergencyContacts.firstOrNull()?.name ?: "None",
                    emergencyContactRelation = userProfile.emergencyContacts.firstOrNull()?.relation ?: "Contact",
                    emergencyContactPhone = userProfile.emergencyContacts.firstOrNull()?.phone ?: "",
                    profileImageUri = userProfile.profileImageUri,
                    googleDriveLink = userProfile.googleDriveLink
                )
            )
        }
    }
}