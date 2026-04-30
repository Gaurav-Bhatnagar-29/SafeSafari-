package com.example.safarisafe.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safarisafe.ui.screens.Login.LoginRoute
import com.example.safarisafe.ui.screens.SplashScreen
import com.example.safarisafe.ui.screens.alert.HazardAlertRoute
import com.example.safarisafe.ui.screens.alert.SosRoute
import com.example.safarisafe.ui.screens.explore.ExploreRoute
import com.example.safarisafe.ui.screens.identity.IdentityRoute
import com.example.safarisafe.ui.screens.profile.EditProfileScreen
import com.example.safarisafe.ui.screens.profile.ProfileScreen
import com.example.safarisafe.ui.screens.status.SafeStatusRoute
import com.example.safarisafe.ui.theme.SafariSafeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafariSafeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Start destination is now ALWAYS "splash"
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") { SplashScreen(navController) }
                        composable("login") { LoginRoute(navController) }
                        composable("status") { SafeStatusRoute(navController) }
                        composable("explore") { ExploreRoute(navController) }
                        composable("alert") { HazardAlertRoute(navController) }
                        composable("identity") { IdentityRoute(navController) }
                        composable("sos") { SosRoute(navController) }
                        composable("profile") { ProfileScreen(navController) }
                        composable("edit_profile") { EditProfileScreen(onBack = { navController.popBackStack() }) }
                        
                        // Profile Section Routes
                        composable("sos_preferences") { PlaceholderScreen("SOS Preferences", navController) }
                        composable("location_permissions") { PlaceholderScreen("Location Permissions", navController) }
                        composable("notifications_settings") { PlaceholderScreen("Notification Settings", navController) }
                        composable("language_settings") { PlaceholderScreen("Language Settings", navController) }
                        composable("terms_privacy") { PlaceholderScreen("Terms & Privacy Policy", navController) }
                        composable("help_center") { PlaceholderScreen("Help Center", navController) }

                        // Added placeholder routes for missing features
                        composable("safezones") { PlaceholderScreen("My Safe Zones", navController) }
                        composable("contacts") { IdentityRoute(navController) } // Reuse identity for contacts
                        composable("itinerary") { PlaceholderScreen("Trip Itinerary", navController) }
                        composable("settings") { PlaceholderScreen("Settings", navController) }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String, navController: androidx.navigation.NavController) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text(title) },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { navController.popBackStack() }) {
                        androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(padding),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text("This feature ($title) is coming soon!")
        }
    }
}