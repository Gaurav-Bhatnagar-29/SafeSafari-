package com.example.safarisafe.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.example.safarisafe.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safarisafe.ui.components.SafariCard
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.viewmodel.SafetyViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    safetyViewModel: SafetyViewModel = viewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val identityState by safetyViewModel.identityState.collectAsState()
    val profile = identityState.profile

    var tripTrackingEnabled by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profile_account),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.profile_back), tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- Profile Header ---
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryAccent.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.profile_picture),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        tint = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = profile?.name ?: auth.currentUser?.displayName ?: "Arman",
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary
            )
            Text(
                text = auth.currentUser?.email ?: "traveler@safari.com",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = CircleShape,
                color = PrimaryAccent.copy(alpha = 0.1f),
                onClick = { navController.navigate("edit_profile") },
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.5f))
            ) {
                Text(
                    text = stringResource(R.string.nav_edit_profile),
                    color = PrimaryAccent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- Tracking Control Card ---
            SafariCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.profile_active_trip_tracking), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (tripTrackingEnabled) stringResource(R.string.profile_tracking_online) else stringResource(R.string.profile_tracking_offline),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = tripTrackingEnabled,
                        onCheckedChange = { 
                            tripTrackingEnabled = it
                            if (it) {
                                profile?.emergencyContactPhone?.let { phone ->
                                    if (phone.isNotBlank()) {
                                        safetyViewModel.openWhatsAppChat(context, phone)
                                    }
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SuccessGreen,
                            uncheckedTrackColor = SurfaceDark,
                            uncheckedBorderColor = TextSecondary.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Safety & Privacy Section ---
            SectionHeader(stringResource(R.string.profile_safety_privacy))
            SafariCard(modifier = Modifier.padding(vertical = 8.dp)) {
                Column {
                    ProfileListItem(Icons.Filled.Shield, stringResource(R.string.profile_sos_preferences)) {
                        navController.navigate("sos_preferences")
                    }
                    ProfileListItem(Icons.Filled.LocationOn, stringResource(R.string.profile_location_permissions)) {
                         navController.navigate("location_permissions")
                    }
                    ProfileListItem(Icons.Filled.Notifications, stringResource(R.string.profile_notification_settings)) {
                         navController.navigate("notifications_settings")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- General Section ---
            SectionHeader(stringResource(R.string.profile_general))
            SafariCard(modifier = Modifier.padding(vertical = 8.dp)) {
                Column {
                    ProfileListItem(Icons.Filled.Public, stringResource(R.string.profile_language_region)) {
                         navController.navigate("language_settings")
                    }
                    ProfileListItem(Icons.Filled.Description, stringResource(R.string.profile_terms_privacy)) {
                         navController.navigate("terms_privacy")
                    }
                    ProfileListItem(Icons.Filled.HelpOutline, stringResource(R.string.profile_help_center)) {
                         navController.navigate("help_center")
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // --- Sign Out Button ---
            TextButton(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.nav_sign_out),
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        letterSpacing = 1.5.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun ProfileListItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryAccent.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryAccent, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Filled.ChevronRight, contentDescription = "Go", tint = TextSecondary.copy(alpha = 0.5f))
    }
}
