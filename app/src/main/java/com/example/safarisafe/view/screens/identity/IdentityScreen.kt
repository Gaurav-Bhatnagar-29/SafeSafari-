package com.example.safarisafe.view.screens.identity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safarisafe.models.IdentityUiState
import com.example.safarisafe.view.components.BottomNavBar
import com.example.safarisafe.view.components.TopNavBar
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.viewmodel.SafetyViewModel

@Composable
fun IdentityRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.identityState.collectAsState()

    IdentityScreen(
        uiState = uiState,
        navController = navController
    )
}

@Composable
fun IdentityScreen(
    uiState: IdentityUiState,
    navController: NavController
) {
    Scaffold(
        topBar = { TopNavBar(title = "The Vigilant Editorial", showWarning = false) },
        bottomBar = { BottomNavBar(selectedTab = "Identity", navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VERIFIED IDENTITY",
                color = PrimaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Digital ID &\nTriage Profile",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 36.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            uiState.profile?.let { profile ->
                // Main Profile Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerHighest),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(profile.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("NATIONALITY: ${profile.nationality}", fontSize = 10.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        Text("PASSPORT NO: ${profile.documentNumber}", fontSize = 10.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Medical Info Section
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Blood Type
                    TriageCard(
                        title = "BLOOD TYPE",
                        value = profile.bloodType,
                        detail = profile.bloodTypeDetail,
                        icon = Icons.Default.WaterDrop,
                        containerColor = ErrorContainer,
                        contentColor = TertiaryRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Allergies
                TriageCard(
                    title = "KNOWN ALLERGIES",
                    value = profile.allergies,
                    detail = profile.allergiesDetail,
                    icon = Icons.Default.Warning,
                    containerColor = SurfaceContainer,
                    contentColor = TertiaryRed,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Chronic Conditions
                TriageCard(
                    title = "CHRONIC CONDITIONS",
                    value = profile.conditions,
                    detail = profile.conditionsDetail,
                    icon = Icons.Default.CheckCircle,
                    containerColor = SurfaceContainer,
                    contentColor = SecondaryGreen,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Emergency Contacts
                Text("EMERGENCY CONTACTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerHighest),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = PrimaryBlue)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile.emergencyContactName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(profile.emergencyContactRelation, fontSize = 12.sp, color = OnSurfaceVariant)
                            Text(profile.emergencyContactPhone, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))
                        }
                        IconButton(
                            onClick = { /* Call Action */ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // System Status
                Card(
                    colors = CardDefaults.cardColors(containerColor = SecondaryContainerGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SecondaryGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("SYSTEM STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                            Text("Active & Encrypted", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Authority Scan QR Placeholder
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Authority Scan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "In the event of an emergency, first responders can scan this encrypted code to access your full medical dossier.",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(SurfaceContainerHighest, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Code", modifier = Modifier.size(80.dp), tint = PrimaryBlue)
                    }
                    Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom nav
                }
            }
        }
    }
}

@Composable
fun TriageCard(
    title: String,
    value: String,
    detail: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            }
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = contentColor, modifier = Modifier.padding(top = 8.dp))
            Text(detail, fontSize = 12.sp, color = OnSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }
    }
}