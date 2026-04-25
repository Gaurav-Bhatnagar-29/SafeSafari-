package com.example.safarisafe.models

import androidx.compose.ui.graphics.vector.ImageVector

data class Alert(
    val title: String,
    val description: String,
    val distance: String,
    val isCritical: Boolean,
    val recommendedActions: List<ActionItemData>
)

data class ActionItemData(
    val icon: ImageVector,
    val text: String
)

data class LocationSafety(
    val locationName: String,
    val safetyScore: Int,
    val isVerified: Boolean,
    val features: List<String>,
    val latitude: Double,
    val longitude: Double
)

data class HazardUiState(
    val isLoading: Boolean = false,
    val activeAlert: Alert? = null
)

data class SafeStatusUiState(
    val isMonitoringActive: Boolean = true,
    val temperature: String = "72°F",
    val weatherCondition: String = "Sunny"
)

data class ExploreUiState(
    val currentLocation: LocationSafety? = null,
    val searchQuery: String = ""
)

data class IdentityProfile(
    val name: String,
    val nationality: String,
    val documentNumber: String,
    val bloodType: String,
    val bloodTypeDetail: String,
    val allergies: String,
    val allergiesDetail: String,
    val conditions: String,
    val conditionsDetail: String,
    val emergencyContactName: String,
    val emergencyContactRelation: String,
    val emergencyContactPhone: String
)

data class IdentityUiState(
    val isLoading: Boolean = false,
    val profile: IdentityProfile? = null
)

data class WeatherInfo(
    val temperature: String,
    val condition: String,
    val city: String,
    val iconRes: Int // In a real app, use an async image loader like Coil for API icon URLs
)