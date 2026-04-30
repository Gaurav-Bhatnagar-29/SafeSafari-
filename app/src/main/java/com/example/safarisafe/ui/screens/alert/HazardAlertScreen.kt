package com.example.safarisafe.ui.screens.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState // ⬅️ ADDED
import com.example.safarisafe.models.HazardUiState
import com.example.safarisafe.ui.theme.ErrorContainer
import com.example.safarisafe.ui.theme.ErrorRed
import com.example.safarisafe.ui.theme.SurfaceContainerHighest
import com.example.safarisafe.ui.components.AppDrawer // ⬅️ ADDED
import com.example.safarisafe.ui.components.BottomNavBar
import com.example.safarisafe.ui.components.SosFab
import com.example.safarisafe.viewmodel.SafetyViewModel
import kotlinx.coroutines.launch // ⬅️ ADDED

@Composable
fun HazardAlertRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.hazardState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    HazardAlertScreen(
        uiState = uiState,
        navController = navController,
        onGetRouteClicked = { 
            viewModel.getEvacuationRoute() 
            android.widget.Toast.makeText(context, "Calculating safest evacuation route...", android.widget.Toast.LENGTH_SHORT).show()
        },
        onSosClicked = { navController.navigate("sos") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazardAlertScreen(
    uiState: HazardUiState,
    navController: NavController,
    onGetRouteClicked: () -> Unit,
    onSosClicked: () -> Unit
) {
    // ⬅️ FIX 1: Added Drawer State & Scope
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ⬅️ FIX 2: Reactive navigation state for highlights
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
                // ⬅️ FIX 3: Replaced custom top bar to integrate Drawer & Warning Icon
                CenterAlignedTopAppBar(
                    title = { Text("The Vigilant Editorial") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // Replicates your showWarning logic natively
                        if (uiState.activeAlert?.isCritical == true) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Critical Warning",
                                tint = ErrorRed,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    }
                )
            },
            bottomBar = { BottomNavBar(selectedTab = "Alerts", navController = navController) },
            floatingActionButton = { SosFab(onClick = onSosClicked) },
            floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

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
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
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
}