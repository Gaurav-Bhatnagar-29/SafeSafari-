package com.example.safarisafe.ui.screens.status

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safarisafe.BuildConfig
import com.example.safarisafe.models.SafeStatusUiState
import com.example.safarisafe.ui.theme.OnSurfaceVariant
import com.example.safarisafe.ui.theme.PrimaryBlue
import com.example.safarisafe.ui.theme.SecondaryContainerGreen
import com.example.safarisafe.ui.theme.SurfaceContainerLow
import com.example.safarisafe.ui.components.AppDrawer
import com.example.safarisafe.ui.components.BottomNavBar
import com.example.safarisafe.ui.components.SosFab
import com.example.safarisafe.viewmodel.SafetyViewModel
import kotlinx.coroutines.launch

@Composable
fun SafeStatusRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.safeStatusState.collectAsState()

    // Trigger the API call when the screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchLiveWeather(
            lat = 28.6139,
            lng = 77.2090,
            apiKey = BuildConfig.WEATHER_API_KEY
        )
    }

    SafeStatusScreen(
        uiState = uiState,
        navController = navController,
        onSosClicked = { navController.navigate("sos") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeStatusScreen(
    uiState: SafeStatusUiState,
    navController: NavController,
    onSosClicked: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("The Vigilant Editorial") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(selectedTab = "Safety", navController = navController)
            },
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

                WeatherCard(
                    temperature = uiState.temperature,
                    condition = uiState.weatherCondition
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isMonitoringActive) {
                    MonitoringCard()
                }
            }
        }
    }
}

@Composable
fun WeatherCard(temperature: String, condition: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Current Location",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = temperature,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = condition,
                        fontSize = 16.sp,
                        color = OnSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = "Weather Icon",
                modifier = Modifier.size(48.dp),
                tint = PrimaryBlue
            )
        }
    }
}

@Composable
fun MonitoringCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.VerifiedUser, contentDescription = "Active", tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                Surface(color = SecondaryContainerGreen, shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = "ACTIVE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF217128)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Foreground Monitoring", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("System is continuously tracking environmental cues for your protection.", fontSize = 14.sp, color = OnSurfaceVariant)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SafeStatusScreenPreview_Active() {
    SafeStatusScreen(
        uiState = SafeStatusUiState(
            isMonitoringActive = true,
            temperature = "22°C",
            weatherCondition = "Partly Cloudy"
        ),
        navController = rememberNavController(),
        onSosClicked = {}
    )
}