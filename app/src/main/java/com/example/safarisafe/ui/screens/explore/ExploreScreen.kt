package com.example.safarisafe.ui.screens.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safarisafe.models.ExploreUiState
import com.example.safarisafe.ui.components.*
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.viewmodel.SafetyViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun ExploreRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.exploreState.collectAsState()
    val context = LocalContext.current

    ExploreScreen(
        uiState = uiState,
        navController = navController,
        onSearchChanged = { viewModel.onSearchQueryChanged(it) },
        onSearchTriggered = { viewModel.performSearch(context) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    navController: NavController,
    onSearchChanged: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val currentRoute = navController.currentBackStackEntry?.destination?.route

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
                    title = "Explore",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = { navController.navigate("profile") }
                )
            },
            bottomBar = {
                FloatingBottomNav(
                    selectedTab = "Explore",
                    navController = navController,
                    onSosClick = { navController.navigate("sos") }
                )
            },
            containerColor = BackgroundDark
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val startingLocation = LatLng(20.5937, 78.9629)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(startingLocation, 5f)
                }

                LaunchedEffect(uiState.currentLocation) {
                    uiState.currentLocation?.let { location ->
                        val newLatLng = LatLng(location.latitude, location.longitude)
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(newLatLng, 15f),
                            durationMs = 1500
                        )
                    }
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    uiState.currentLocation?.let { location ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    location.latitude,
                                    location.longitude
                                )
                            ),
                            title = location.locationName,
                            snippet = "Safety Score: ${location.safetyScore}"
                        )
                    }
                }

                // Search Bar Overlay
                Column(modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        color = SurfaceDark.copy(alpha = 0.9f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.2f)),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = PrimaryAccent
                            )

                            TextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchChanged,
                                placeholder = {
                                    Text(
                                        "Search safe areas...",
                                        color = TextSecondary,
                                        fontSize = 14.sp
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        onSearchTriggered()
                                        focusManager.clearFocus()
                                    }
                                )
                            )

                            IconButton(onClick = { 
                                android.widget.Toast.makeText(context, "Voice search coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = "Voice",
                                    tint = PrimaryAccent
                                )
                            }
                        }
                    }
                }

                // Location Details Bottom Sheet (Static variant for now)
                uiState.currentLocation?.let { location ->
                    SafariCard(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    location.locationName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (location.isVerified) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                        Icon(
                                            Icons.Default.VerifiedUser,
                                            contentDescription = "Verified",
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "VERIFIED SECURE ZONE",
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${location.safetyScore}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SuccessGreen
                                )
                                Text(
                                    "SAFETY SCORE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        SafariButton(
                            text = "NAVIGATE SAFELY",
                            onClick = { 
                                val gmmIntentUri = android.net.Uri.parse("google.navigation:q=${location.latitude},${location.longitude}")
                                val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            }
                        )
                        
                        // Added buffer for floating nav
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
