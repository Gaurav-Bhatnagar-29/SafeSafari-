package com.example.safarisafe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import com.example.safarisafe.R
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
        drawerContainerColor = BackgroundDark,
        modifier = Modifier.width(320.dp),
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
    ) {

        // -------- HEADER --------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 64.dp, bottom = 32.dp, end = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.nav_profile),
                    tint = PrimaryAccent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = auth.currentUser?.displayName ?: "Arman",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.profile_verified),
                    tint = SuccessGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.profile_verified_explorer),
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // -------- NAV ITEMS --------
        DrawerItem(
            icon = Icons.Outlined.Shield,
            label = stringResource(R.string.nav_safety_status),
            isSelected = currentRoute == "status",
            onClick = { navigateTo(navController, "status") }
        )

        DrawerItem(
            icon = Icons.Outlined.Map,
            label = stringResource(R.string.nav_explore_map),
            isSelected = currentRoute == "explore",
            onClick = { navigateTo(navController, "explore") }
        )

        DrawerItem(
            icon = Icons.Outlined.Warning,
            label = stringResource(R.string.nav_active_hazards),
            isSelected = currentRoute == "alert",
            onClick = { navigateTo(navController, "alert") }
        )

        DrawerItem(
            icon = Icons.Outlined.Badge,
            label = stringResource(R.string.nav_digital_identity),
            isSelected = currentRoute == "identity",
            onClick = { navigateTo(navController, "identity") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            color = SurfaceContainerHighest.copy(alpha = 0.5f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(16.dp))

        DrawerItem(
            icon = Icons.Outlined.Person,
            label = stringResource(R.string.nav_edit_profile),
            isSelected = currentRoute == "profile" || currentRoute == "edit_profile",
            onClick = { navigateTo(navController, "edit_profile") }
        )

        DrawerItem(
            icon = Icons.Outlined.EventNote,
            label = stringResource(R.string.nav_trip_itinerary),
            isSelected = currentRoute == "itinerary",
            onClick = { navigateTo(navController, "itinerary") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // -------- FOOTER --------
        DrawerItem(
            icon = Icons.Outlined.Settings,
            label = stringResource(R.string.nav_settings),
            isSelected = currentRoute == "settings",
            onClick = { navigateTo(navController, "settings") }
        )

        DrawerItem(
            icon = Icons.AutoMirrored.Outlined.ExitToApp,
            label = stringResource(R.string.nav_sign_out),
            onClick = {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            labelColor = TextSecondary,
            iconTint = TextSecondary
        )

        Text(
            text = "v1.1.0",
            fontSize = 12.sp,
            color = TextSecondary.copy(alpha = 0.5f),
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
    labelColor: Color = if (isSelected) PrimaryAccent else TextPrimary,
    iconTint: Color = if (isSelected) PrimaryAccent else TextSecondary
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
        label = {
            Text(
                label,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = PrimaryAccent.copy(alpha = 0.1f),
            unselectedContainerColor = Color.Transparent,
            selectedIconColor = PrimaryAccent,
            unselectedIconColor = iconTint,
            selectedTextColor = PrimaryAccent,
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
