package com.example.safarisafe.view.screens.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safarisafe.models.HazardUiState
import com.example.safarisafe.ui.theme.ErrorContainer
import com.example.safarisafe.ui.theme.ErrorRed
import com.example.safarisafe.ui.theme.SurfaceContainerHighest
import com.example.safarisafe.view.components.BottomNavBar
import com.example.safarisafe.view.components.SosFab
import com.example.safarisafe.view.components.TopNavBar
import com.example.safarisafe.viewmodel.SafetyViewModel

@Composable
fun HazardAlertRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.hazardState.collectAsState()

    // Pass the logic HERE, including the SOS navigation
    HazardAlertScreen(
        uiState = uiState,
        navController = navController,
        onGetRouteClicked = { viewModel.getEvacuationRoute() },
        onSosClicked = { navController.navigate("sos") }
    )
}

// The blueprint expects TYPES, not logic
@Composable
fun HazardAlertScreen(
    uiState: HazardUiState,
    navController: NavController,
    onGetRouteClicked: () -> Unit,
    onSosClicked: () -> Unit
) {
    Scaffold(
        topBar = { TopNavBar(title = "The Vigilant Editorial", showWarning = uiState.activeAlert?.isCritical == true) },
        bottomBar = { BottomNavBar(selectedTab = "Alerts", navController = navController) },
        floatingActionButton = { SosFab(onClick = onSosClicked) },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {

            uiState.activeAlert?.let { alert ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed, contentColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("HAZARD WARNING", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                            Text("${alert.title} ${alert.distance} - ${alert.description}", fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
                        }
                        Button(
                            onClick = onGetRouteClicked,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ErrorRed)
                        ) {
                            Text("GET ROUTE", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerHighest)
                ) {
                    Text("Map Visualization", modifier = Modifier.align(Alignment.Center))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ErrorContainer),
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("BREACH WARNING", color = ErrorRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Entering Restricted Zone", color = Color(0xFF93000A), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Immediate Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                alert.recommendedActions.forEach { action ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(action.icon, contentDescription = null, tint = Color(0xFFD97706))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(action.text, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}