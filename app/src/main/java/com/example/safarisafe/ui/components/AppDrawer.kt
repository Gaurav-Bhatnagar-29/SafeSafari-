package com.example.safarisafe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.safarisafe.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppDrawer(
    navController: NavController,
    currentRoute: String?
) {
    val auth = FirebaseAuth.getInstance()
    
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFFFDFDFF),
        modifier = Modifier.width(320.dp)
    ) {

        // -------- HEADER --------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 48.dp, bottom = 24.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Clickable Profile Icon
            IconButton(onClick = {
                navigateTo(navController, "profile")
            }) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = auth.currentUser?.displayName ?: "Arman",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Verified",
                        tint = SecondaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Verified Tourist",
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // -------- NAV ITEMS --------
        DrawerItem(
            icon = Icons.Outlined.Map,
            label = "Explore Map",
            isSelected = currentRoute == "explore",
            onClick = { navigateTo(navController, "explore") }
        )

        DrawerItem(
            icon = Icons.Outlined.Person,
            label = "Profile",
            isSelected = currentRoute == "profile",
            onClick = { navigateTo(navController, "profile") }
        )

        DrawerItem(
            icon = Icons.Outlined.Warning,
            label = "Active Hazards",
            isSelected = currentRoute == "alert",
            onClick = { navigateTo(navController, "alert") }
        )

        DrawerItem(
            icon = Icons.Outlined.Shield,
            label = "My Safe Zones",
            isSelected = currentRoute == "safezones",
            onClick = { /* Currently no route defined for safezones */ }
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(8.dp))

        DrawerItem(
            icon = Icons.Outlined.Badge,
            label = "Digital Identity",
            isSelected = currentRoute == "identity",
            onClick = { navigateTo(navController, "identity") }
        )

        DrawerItem(
            icon = Icons.Outlined.Contacts,
            label = "Emergency Contacts",
            isSelected = currentRoute == "contacts",
            onClick = { /* Currently no route defined for contacts */ }
        )

        DrawerItem(
            icon = Icons.Outlined.EventNote,
            label = "Trip Itinerary",
            isSelected = currentRoute == "itinerary",
            onClick = { /* Currently no route defined for itinerary */ }
        )

        Spacer(modifier = Modifier.weight(1f))

        // -------- FOOTER --------
        DrawerItem(
            icon = Icons.Outlined.Settings,
            label = "Settings",
            isSelected = currentRoute == "settings",
            onClick = { /* Currently no route defined for settings */ }
        )

        DrawerItem(
            icon = Icons.AutoMirrored.Outlined.ExitToApp,
            label = "Sign Out",
            onClick = {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            labelColor = OnSurfaceVariant,
            iconTint = OnSurfaceVariant
        )

        Text(
            text = "v1.0.42",
            fontSize = 12.sp,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(start = 24.dp, bottom = 24.dp, top = 8.dp)
        )
    }
}

// -------- Drawer Item --------
@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    labelColor: Color = if (isSelected) PrimaryBlue else TextPrimary,
    iconTint: Color = if (isSelected) PrimaryBlue else TextPrimary.copy(alpha = 0.8f)
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
        label = {
            Text(
                label,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = PrimaryBlue.copy(alpha = 0.1f),
            unselectedContainerColor = Color.Transparent,
            selectedIconColor = PrimaryBlue,
            unselectedIconColor = iconTint,
            selectedTextColor = PrimaryBlue,
            unselectedTextColor = labelColor
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
    )
}

// -------- Navigation Helper --------
fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo("status") { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}