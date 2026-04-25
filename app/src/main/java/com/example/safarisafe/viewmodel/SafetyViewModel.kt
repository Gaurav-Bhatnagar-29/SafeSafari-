package com.example.safarisafe.viewmodel

import android.content.Context
import android.location.Geocoder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safarisafe.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import com.example.safarisafe.network.WeatherNetwork

class SafetyViewModel : ViewModel() {

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
                    // Added coordinate properties so your mock data matches your updated model!
                    latitude = 40.785091,
                    longitude = -73.968285
                )
            )
        }

        _identityState.update {
            it.copy(
                profile = IdentityProfile(
                    name = "Marcus Vance-Sterling",
                    nationality = "United Kingdom",
                    documentNumber = "UK *****492",
                    bloodType = "O+",
                    bloodTypeDetail = "Universal Donor",
                    allergies = "Penicillin",
                    allergiesDetail = "Severe Anaphylactic Risk",
                    conditions = "None",
                    conditionsDetail = "No medical history reported",
                    emergencyContactName = "Maria Sterling",
                    emergencyContactRelation = "Spouse / Next of Kin",
                    emergencyContactPhone = "+44 7700 900341"
                )
            )
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _exploreState.update { it.copy(searchQuery = newQuery) }
    }

    // ADDED: The missing function that ExploreScreen is looking for!
    fun performSearch(context: Context) {
        val query = exploreState.value.searchQuery
        if (query.isBlank()) return

        // Launch a coroutine so we don't freeze the app while searching
        viewModelScope.launch {
            try {
                // Switch to a background thread for the network/database lookup
                val addressList = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context)
                    // Ask the geocoder for 1 result matching the query
                    geocoder.getFromLocationName(query, 1)
                }

                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]

                    // We found coordinates! Update the state.
                    _exploreState.update { currentState ->
                        currentState.copy(
                            currentLocation = LocationSafety(
                                // Use the actual name returned, or fallback to their query
                                locationName = address.featureName ?: query,
                                safetyScore = (60..100).random(), // Mocking a new safety score
                                isVerified = true,
                                features = listOf("Searched Destination"),
                                latitude = address.latitude,
                                longitude = address.longitude
                            )
                        )
                    }
                } else {
                    println("SafariSafe: No coordinates found for $query")
                }
            } catch (e: IOException) {
                println("SafariSafe: Geocoding failed. Check internet connection.")
                e.printStackTrace()
            }
        }
    }

    fun fetchLiveWeather(lat: Double, lng: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                // Make the API call
                val response = WeatherNetwork.service.getCurrentConditions(apiKey, lat, lng)

                // Extract the data
                val realTemp = "${response.temperature.degrees}°C"
                val realCondition = response.weatherCondition.description.text

                // Push the real data to your UI state
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
}