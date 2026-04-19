package com.example.safarisafe.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.safarisafe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(title: String, showWarning: Boolean = false) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, color = TextPrimary) },
        navigationIcon = {
            IconButton(onClick = { /* Open Drawer */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = PrimaryBlue)
            }
        },
        actions = {
            if (showWarning) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFD97706)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WARNING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerHighest)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBackground)
    )
}

@Composable
fun BottomNavBar(selectedTab: String, navController: NavController) {
    NavigationBar(
        containerColor = SurfaceContainerHighest.copy(alpha = 0.8f),
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        // ADDED: Identity to the list of tabs
        val tabs = listOf(
            "Safety" to Icons.Default.Shield,
            "Explore" to Icons.Default.Map,
            "Alerts" to Icons.Default.Notifications,
            "Identity" to Icons.Default.Fingerprint
        )

        tabs.forEach { (label, icon) ->
            val isSelected = selectedTab == label
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                selected = isSelected,
                onClick = {
                    // ADDED: Identity routing logic
                    val route = when(label) {
                        "Safety" -> "status"
                        "Explore" -> "explore"
                        "Alerts" -> "alert"
                        "Identity" -> "identity"
                        else -> "status"
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PrimaryBlue,
                    unselectedIconColor = OnSurfaceVariant,
                    unselectedTextColor = OnSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun SosFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(TertiaryRed.copy(alpha = 0.1f))
        )
        FloatingActionButton(
            onClick = onClick,
            containerColor = TertiaryRed,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Warning, contentDescription = "SOS", modifier = Modifier.size(32.dp))
                Text("SOS", fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }
    }
}