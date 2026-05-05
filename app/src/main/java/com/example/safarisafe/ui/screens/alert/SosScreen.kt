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
import androidx.compose.ui.res.stringResource
import com.example.safarisafe.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safarisafe.ui.theme.TertiaryRed
import com.example.safarisafe.viewmodel.SafetyViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

// 1. The Route grabs the live location data from your ViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@Composable
fun SosRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.exploreState.collectAsState()
    val context = LocalContext.current

    val lat = uiState.currentLocation?.latitude ?: 0.0
    val lng = uiState.currentLocation?.longitude ?: 0.0

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.sendSosAlert(context)
        } else {
            android.widget.Toast.makeText(context, context.getString(R.string.sos_permissions_required), android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    SosScreen(
        latitude = lat,
        longitude = lng,
        navController = navController,
        onTriggerSos = {
            val permissions = arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            
            val missingPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

            if (missingPermissions.isEmpty()) {
                viewModel.sendSosAlert(context)
            } else {
                permissionsLauncher.launch(permissions)
            }
        },
        onWhatsAppDispatch = {
            val mapsUrl = "https://www.google.com/maps/search/?api=1&query=$lat,$lng"
            val message = "EMERGENCY! I need help. My current location is: $mapsUrl"
            viewModel.dispatchWhatsApp(context, message)
        }
    )
}

@Composable
fun SosScreen(
    latitude: Double,
    longitude: Double,
    navController: NavController,
    onTriggerSos: () -> Unit = {},
    onWhatsAppDispatch: () -> Unit = {}
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(5) }
    var sosTriggered by remember { mutableStateOf(false) }

    // ... (formatting logic)
    val latText = "%.4f°".format(abs(latitude))
    val latDirection = if (latitude >= 0) "N" else "S"

    val lngText = "%.4f°".format(abs(longitude))
    val lngDirection = if (longitude >= 0) "E" else "W"

    val progress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (isPressed) 2000 else 500,
            easing = LinearEasing
        ),
        label = "cancel_progress"
    )

    LaunchedEffect(progress) {
        if (progress >= 1f) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }

        if (countdown == 0 && !sosTriggered) {
            sosTriggered = true
            onTriggerSos()
            
            // Also keep the voice call as a fallback
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:112")
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
            Text(stringResource(R.string.sos_high_precision), color = Color.White, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.sos_emergency_sending),
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
                Text(stringResource(R.string.sos_live_transmission), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Dynamic Coordinates Layout
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.sos_latitude), color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(latText, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(latDirection, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.sos_longitude), color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.sos_cancel), tint = TertiaryRed, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.sos_hold_to_cancel), color = TertiaryRed, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Dynamic visual countdown
        Text(stringResource(R.string.sos_contacting_services, countdown), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onWhatsAppDispatch,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(stringResource(R.string.sos_dispatch_whatsapp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
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