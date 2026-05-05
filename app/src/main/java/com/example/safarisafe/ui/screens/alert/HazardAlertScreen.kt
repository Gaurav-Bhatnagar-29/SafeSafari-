package com.example.safarisafe.ui.screens.alert

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.example.safarisafe.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.safarisafe.models.HazardUiState
import com.example.safarisafe.ui.components.*
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.viewmodel.SafetyViewModel
import kotlinx.coroutines.launch

@Composable
fun HazardAlertRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.hazardState.collectAsState()
    val context = LocalContext.current

    HazardAlertScreen(
        uiState = uiState,
        navController = navController,
        onGetRouteClicked = { 
            viewModel.getEvacuationRoute() 
            android.widget.Toast.makeText(context, context.getString(R.string.hazard_calculating_route), android.widget.Toast.LENGTH_SHORT).show()
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
                SafariTopBar(
                    title = stringResource(R.string.hazard_active_hazards),
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = { navController.navigate("profile") }
                )
            },
            bottomBar = {
                FloatingBottomNav(
                    selectedTab = stringResource(R.string.nav_alerts),
                    navController = navController,
                    onSosClick = onSosClicked
                )
            },
            containerColor = BackgroundDark
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                uiState.activeAlert?.let { alert ->
                    // High-Priority Hazard Card
                    Surface(
                        color = ErrorRed,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    stringResource(R.string.hazard_critical_detected),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "${alert.title} (${alert.distance})",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            
                            Text(
                                text = alert.description,
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            SafariButton(
                                text = stringResource(R.string.hazard_get_evacuation_route),
                                onClick = onGetRouteClicked,
                                containerColor = Color.White,
                                contentColor = ErrorRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Map Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SurfaceDark)
                    ) {
                        Text(stringResource(R.string.hazard_interactive_map), color = TextSecondary, modifier = Modifier.align(Alignment.Center))
                        
                        Surface(
                            color = ErrorRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
                        ) {
                            Text(
                                stringResource(R.string.hazard_restricted_zone),
                                color = ErrorRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        stringResource(R.string.hazard_immediate_actions),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    alert.recommendedActions.forEach { action ->
                        SafariCard(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryAccent.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(action.icon, contentDescription = null, tint = PrimaryAccent, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(action.text, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
