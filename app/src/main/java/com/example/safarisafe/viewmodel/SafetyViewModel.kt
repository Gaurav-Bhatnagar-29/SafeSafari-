package com.example.safarisafe.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.lifecycle.ViewModel
import com.example.safarisafe.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
                    features = listOf("Well-lit at night", "Regular police patrols")
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

    fun triggerSos() {
        println("SOS Triggered!")
    }

    fun getEvacuationRoute() {
        println("Calculating safe route...")
    }
}