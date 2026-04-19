package com.example.safarisafe

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
import com.example.safarisafe.ui.theme.SafariSafeTheme
import com.example.safarisafe.view.screens.alert.HazardAlertRoute
import com.example.safarisafe.view.screens.alert.SosScreen
import com.example.safarisafe.view.screens.explore.ExploreRoute
import com.example.safarisafe.view.screens.identity.IdentityRoute
import com.example.safarisafe.view.screens.status.SafeStatusRoute

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

                    NavHost(navController = navController, startDestination = "status") {
                        composable("status") { SafeStatusRoute(navController) }
                        composable("explore") { ExploreRoute(navController) }
                        composable("alert") { HazardAlertRoute(navController) }
                        composable("identity") { IdentityRoute(navController) }
                        composable("sos") { SosScreen(navController) }
                    }
                }
            }
        }
    }
}