package com.example.safarisafe.ui.screens.alert

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safarisafe.ui.theme.TertiaryRed
import com.example.safarisafe.viewmodel.SafetyViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

// 1. The Route grabs the live location data from your ViewModel
@Composable
fun SosRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    // Reusing the exploreState since it holds the currentLocation!
    val uiState by viewModel.exploreState.collectAsState()

    val lat = uiState.currentLocation?.latitude ?: 0.0
    val lng = uiState.currentLocation?.longitude ?: 0.0

    SosScreen(
        latitude = lat,
        longitude = lng,
        navController = navController
    )
}

// 2. The main UI with the timer and dynamic coordinates
@Composable
fun SosScreen(
    latitude: Double,
    longitude: Double,
    navController: NavController
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(10) } // 10-second timer

    // Format the coordinates properly (N/S, E/W)
    val latText = "%.4f°".format(abs(latitude))
    val latDirection = if (latitude >= 0) "N" else "S"

    val lngText = "%.4f°".format(abs(longitude))
    val lngDirection = if (longitude >= 0) "E" else "W"

    // The Cancel Button Animation Progress
    val progress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (isPressed) 2000 else 500,
            easing = LinearEasing
        ),
        label = "cancel_progress"
    )

    // Trigger the cancel action if the user holds the button
    LaunchedEffect(progress) {
        if (progress >= 1f) {
            navController.popBackStack()
        }
    }

    // The Auto-Call Countdown Timer
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }

        // When timer hits 0, trigger the emergency call
        if (countdown == 0) {
            // Using ACTION_DIAL is safer for hackathon testing as it doesn't require runtime permissions.
            // (Change to ACTION_CALL if you want it to dial instantly without user confirmation).
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:112") // 112 is the standard emergency number in India
            }
            context.startActivity(callIntent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TertiaryRed)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("HIGH PRECISION", color = Color.White, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "EMERGENCY:\nSENDING\nCOORDINATES",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                Spacer(modifier = Modifier.width(8.dp))
                Text("LIVE TRANSMISSION ACTIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Dynamic Coordinates Layout
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LATITUDE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(latText, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(latDirection, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LONGITUDE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(lngText, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(lngDirection, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(180.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.2f),
                strokeWidth = 6.dp
            )

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(144.dp).border(2.dp, TertiaryRed.copy(alpha = 0.2f), CircleShape))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TertiaryRed, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("HOLD TO\nCANCEL", color = TertiaryRed, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Dynamic visual countdown
        Text("Contacting Emergency Services in $countdown...", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SosScreenPreview() {
    SosScreen(
        latitude = 28.6139,
        longitude = 77.2090,
        navController = androidx.navigation.compose.rememberNavController()
    )
}