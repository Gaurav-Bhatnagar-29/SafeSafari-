package com.example.safarisafe.ui.screens.status

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.example.safarisafe.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safarisafe.BuildConfig
import com.example.safarisafe.models.SafeStatusUiState
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.ui.components.*
import com.example.safarisafe.viewmodel.SafetyViewModel
import kotlinx.coroutines.launch

@Composable
fun SafeStatusRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.safeStatusState.collectAsState()
    val exploreState by viewModel.exploreState.collectAsState()

    LaunchedEffect(exploreState.currentLocation) {
        val lat = exploreState.currentLocation?.latitude ?: 28.6139
        val lng = exploreState.currentLocation?.longitude ?: 77.2090
        
        viewModel.fetchLiveWeather(
            lat = lat,
            lng = lng,
            apiKey = BuildConfig.WEATHER_API_KEY
        )
    }

    SafeStatusScreen(
        uiState = uiState,
        locationName = exploreState.currentLocation?.locationName ?: stringResource(R.string.status_current_location),
        navController = navController,
        onSosClicked = { navController.navigate("sos") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeStatusScreen(
    uiState: SafeStatusUiState,
    locationName: String,
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
                SafariTopBar(
                    title = stringResource(R.string.app_name),
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = { navController.navigate("profile") }
                )
            },
            bottomBar = {
                FloatingBottomNav(
                    selectedTab = stringResource(R.string.nav_safety),
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

                Text(
                    text = stringResource(R.string.status_you_are_safe),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 48.sp
                )
                Text(
                    text = stringResource(R.string.status_all_systems_operational),
                    color = TextSecondary,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                PulsingLocationMap(locationName = locationName)

                Spacer(modifier = Modifier.height(16.dp))

                WeatherCard(
                    locationName = locationName,
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
fun WeatherCard(locationName: String, temperature: String, condition: String) {
    SafariCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = locationName.uppercase(),
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = temperature,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = condition,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = stringResource(R.string.status_weather_icon),
                modifier = Modifier.size(48.dp),
                tint = PrimaryAccent
            )
        }
    }
}

@Composable
fun MonitoringCard() {
    SafariCard {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = stringResource(R.string.status_active),
                tint = PrimaryAccent,
                modifier = Modifier.size(32.dp)
            )
            Surface(
                color = SuccessGreen.copy(alpha = 0.2f),
                shape = RoundedCornerShape(99.dp)
            ) {
                Text(
                    text = stringResource(R.string.status_active),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.status_foreground_monitoring),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Text(
            text = stringResource(R.string.status_monitoring_desc),
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SafeStatusScreenPreview_Active() {
    SafariSafeTheme {
        SafeStatusScreen(
            uiState = SafeStatusUiState(
                isMonitoringActive = true,
                temperature = "22°C",
                weatherCondition = "Partly Cloudy"
            ),
            locationName = "New Delhi, India",
            navController = rememberNavController(),
            onSosClicked = {}
        )
    }
}

@Composable
fun PulsingLocationMap(locationName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark.copy(alpha = 0.5f))
    ) {
        Surface(
            color = SurfaceContainerHighest,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = locationName,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size((pulseRadius * 2).dp)
                    .clip(CircleShape)
                    .background(SuccessGreen.copy(alpha = pulseAlpha))
            )
            
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

