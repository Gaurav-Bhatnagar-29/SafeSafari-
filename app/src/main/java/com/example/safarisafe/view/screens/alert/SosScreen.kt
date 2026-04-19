package com.example.safarisafe.view.screens.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.safarisafe.ui.theme.TertiaryRed

@Composable
fun SosScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TertiaryRed) // The full-screen red takeover
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header
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

        // Live Badge
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

        // Coordinates
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LATITUDE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("34.0522°", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("N", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LONGITUDE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("118.2437°", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("W", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Hold to Cancel Button (Click to pop back for now)
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable {
                    // This cancels the SOS and goes back to the previous screen
                    navController.popBackStack()
                },
            contentAlignment = Alignment.Center
        ) {
            // Inner decorative ring
            Box(modifier = Modifier.size(144.dp).border(2.dp, TertiaryRed.copy(alpha = 0.2f), CircleShape))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TertiaryRed, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("HOLD TO\nCANCEL", color = TertiaryRed, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        Text("Contacting Emergency Services...", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(32.dp))
    }
}