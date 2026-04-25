package com.example.safarisafe.ui.screens.explore

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
import com.example.safarisafe.ui.components.AppDrawer
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.ui.components.BottomNavBar
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
    val context = LocalContext.current // ADDED: Grab the context for the Geocoder

    ExploreScreen(
        uiState = uiState,
        navController = navController,
        onSearchChanged = { viewModel.onSearchQueryChanged(it) },
        onSearchTriggered = { viewModel.performSearch(context) } // ADDED: Pass context to ViewModel
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
    val focusManager = LocalFocusManager.current   // ✅ ADD THIS

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
                CenterAlignedTopAppBar(
                    title = { Text("Explore") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() } // 🔥 OPEN DRAWER
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },

            bottomBar = {
                BottomNavBar(
                    selectedTab = "Explore",
                    navController = navController
                )
            },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("sos") },
                    containerColor = TertiaryRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Emergency")
                }
            }

        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                // 👉 YOUR EXISTING MAP CODE (NO CHANGE)

                // Default starting location
                val startingLocation = LatLng(20.5937, 78.9629)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(startingLocation, 5f)
                }

                // Listen for changes to the currentLocation.
                // When it updates, animate the camera to the new coordinates!
                LaunchedEffect(uiState.currentLocation) {
                    uiState.currentLocation?.let { location ->
                        val newLatLng = LatLng(location.latitude, location.longitude)
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(newLatLng, 15f),
                            durationMs = 1500 // Smooth 1.5 second fly animation
                        )
                    }
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    // Add a marker if we have a current location
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

                Column(modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceBackground.copy(
                                alpha = 0.9f
                            )
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = OnSurfaceVariant
                            )

                            TextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchChanged,
                                placeholder = {
                                    Text(
                                        "Search safe areas...",
                                        color = OnSurfaceVariant
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f),
                                // Handle the keyboard search button
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        onSearchTriggered()
                                        focusManager.clearFocus() // Hide keyboard after searching
                                    }
                                )
                            )

                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Voice",
                                tint = PrimaryBlue
                            )
                        }
                    }
                }

                uiState.currentLocation?.let { location ->
                    Card(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceBackground.copy(
                                alpha = 0.95f
                            )
                        ),
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        location.locationName,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (location.isVerified) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Verified",
                                                tint = SecondaryGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "Verified Secure Zone",
                                                color = SecondaryGreen,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "${location.safetyScore}/100",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SecondaryGreen
                                    )
                                    Text(
                                        "SAFETY SCORE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceVariant
                                    )
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
}
