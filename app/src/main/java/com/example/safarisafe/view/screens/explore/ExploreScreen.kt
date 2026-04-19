package com.example.safarisafe.view.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safarisafe.models.ExploreUiState
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.view.components.BottomNavBar
import com.example.safarisafe.viewmodel.SafetyViewModel

@Composable
fun ExploreRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.exploreState.collectAsState()

    ExploreScreen(
        uiState = uiState,
        navController = navController,
        onSearchChanged = { viewModel.onSearchQueryChanged(it) }
    )
}

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    navController: NavController,
    onSearchChanged: (String) -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavBar(selectedTab = "Explore", navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Emergency */ },
                containerColor = TertiaryRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.AddAlert, contentDescription = "Emergency")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            Box(modifier = Modifier.fillMaxSize().background(SurfaceContainerHighest)) {
                Text("Interactive Map Layer", modifier = Modifier.align(Alignment.Center))
            }

            Column(modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = OnSurfaceVariant)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.searchQuery.ifEmpty { "Search safe areas..." },
                            color = OnSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = PrimaryBlue)
                    }
                }
            }

            uiState.currentLocation?.let { location ->
                Card(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground.copy(alpha = 0.95f)),
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(location.locationName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                if (location.isVerified) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = SecondaryGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verified Secure Zone", color = SecondaryGreen, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    }
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${location.safetyScore}/100", fontSize = 24.sp, fontWeight = FontWeight.Black, color = SecondaryGreen)
                                Text("SAFETY SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { /* Navigation logic */ },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Navigate Safely", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}