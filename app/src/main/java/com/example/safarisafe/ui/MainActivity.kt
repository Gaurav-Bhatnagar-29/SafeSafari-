package com.example.safarisafe.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safarisafe.ui.screens.Login.LoginRoute
import com.example.safarisafe.ui.screens.SplashScreen
import com.example.safarisafe.ui.screens.alert.HazardAlertRoute
import com.example.safarisafe.ui.screens.alert.SosRoute
import com.example.safarisafe.ui.screens.explore.ExploreRoute
import com.example.safarisafe.ui.screens.identity.IdentityRoute
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
                    }
                }
            }
        }
    }
}