package com.example.safarisafe.view.screens.status


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safarisafe.ui.theme.OnSurfaceVariant
import com.example.safarisafe.ui.theme.PrimaryBlue
import com.example.safarisafe.ui.theme.SecondaryContainerGreen
import com.example.safarisafe.ui.theme.SurfaceContainerLow
import com.example.safarisafe.view.components.BottomNavBar
import com.example.safarisafe.view.components.SosFab
import com.example.safarisafe.view.components.TopNavBar
import com.example.safarisafe.viewmodel.SafetyViewModel

@Composable
fun SafeStatusRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.safeStatusState.collectAsState()

    // We pass the navigation logic HERE, when we are calling the screen
    SafeStatusScreen(
        isMonitoringActive = uiState.isMonitoringActive,
        navController = navController,
        onSosClicked = { navController.navigate("sos") }
    )
}

// This is the blueprint, so it just needs to know the TYPES of data it will receive
@Composable
fun SafeStatusScreen(
    isMonitoringActive: Boolean,
    navController: NavController,
    onSosClicked: () -> Unit
) {
    Scaffold(
        topBar = { TopNavBar(title = "The Vigilant Editorial", showWarning = false) },
        bottomBar = { BottomNavBar(selectedTab = "Safety", navController = navController) },
        floatingActionButton = { SosFab(onClick = onSosClicked) },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("You are safe.", fontSize = 48.sp, fontWeight = FontWeight.Bold, lineHeight = 50.sp)
            Text("All systems operational.", color = OnSurfaceVariant, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(24.dp))

            if (isMonitoringActive) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = "Active", tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                            Surface(color = SecondaryContainerGreen, shape = RoundedCornerShape(4.dp)) {
                                Text("ACTIVE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF217128))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Foreground Monitoring", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("System is continuously tracking environmental cues for your protection.", fontSize = 14.sp, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}