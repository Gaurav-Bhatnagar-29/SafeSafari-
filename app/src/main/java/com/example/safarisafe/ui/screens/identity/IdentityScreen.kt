package com.example.safarisafe.ui.screens.identity

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import com.example.safarisafe.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.safarisafe.models.IdentityUiState
import com.example.safarisafe.ui.components.*
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.viewmodel.SafetyViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch

/**
 * Expert Utility: Generates a Compose-compatible ImageBitmap from a URL string.
 * Uses setPixels for high performance.
 */
fun generateQrBitmap(content: String, sizePx: Int): ImageBitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
        
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE
            }
        }
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun AuthorityScanCard(documentNumber: String, googleDriveLink: String) {
    val qrUrl = if (googleDriveLink.isNotBlank()) googleDriveLink else "https://safarisafe.app/emergency/auth_scan?id=$documentNumber"
    
    // Performance: remember so it only regenerates if qrUrl changes
    val qrBitmap = remember(qrUrl) {
        generateQrBitmap(qrUrl, 512)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.identity_authority_scan),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.identity_authority_scan_desc),
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.size(200.dp),
            color = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = stringResource(R.string.identity_authority_scan),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = stringResource(R.string.identity_loading_qr),
                        modifier = Modifier.size(100.dp),
                        tint = BackgroundDark
                    )
                }
            }
        }
    }
}

@Composable
fun IdentityRoute(
    navController: NavController,
    viewModel: SafetyViewModel = viewModel()
) {
    val uiState by viewModel.identityState.collectAsState()
    val context = LocalContext.current

    IdentityScreen(
        uiState = uiState,
        navController = navController,
        onCallContact = { phone ->
            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                data = android.net.Uri.parse("tel:$phone")
            }
            context.startActivity(intent)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityScreen(
    uiState: IdentityUiState,
    navController: NavController,
    onCallContact: (String) -> Unit = {}
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
                    title = stringResource(R.string.nav_identity),
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = { navController.navigate("profile") }
                )
            },
            bottomBar = {
                FloatingBottomNav(
                    selectedTab = stringResource(R.string.nav_identity),
                    navController = navController,
                    onSosClick = { navController.navigate("sos") }
                )
            },
            containerColor = BackgroundDark
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.identity_verified_identity),
                    color = PrimaryAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = stringResource(R.string.identity_digital_id_triage),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 40.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )

                uiState.profile?.let { profile ->
                    // Main Profile Card
                    SafariCard {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHighest)
                            ) {
                                val imageUri = profile.profileImageUri
                                val context = LocalContext.current
                                val bitmap = remember(imageUri) {
                                    if (imageUri != null) {
                                        try {
                                            if (imageUri.startsWith("/")) {
                                                android.graphics.BitmapFactory.decodeFile(imageUri)
                                            } else {
                                                context.contentResolver.openInputStream(android.net.Uri.parse(imageUri))?.use {
                                                    android.graphics.BitmapFactory.decodeStream(it)
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            null
                                        }
                                    } else {
                                        null
                                    }
                                }

                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = stringResource(R.string.profile_picture),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(20.dp),
                                        tint = TextSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(profile.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = PrimaryAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    profile.nationality.uppercase(),
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.identity_passport, profile.documentNumber),
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Medical Info Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TriageCard(
                            title = stringResource(R.string.identity_blood_type),
                            value = profile.bloodType,
                            detail = profile.bloodTypeDetail,
                            icon = Icons.Default.WaterDrop,
                            contentColor = ErrorRed,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TriageCard(
                        title = stringResource(R.string.identity_known_allergies),
                        value = if (profile.allergies.isBlank()) stringResource(R.string.identity_none) else profile.allergies,
                        detail = profile.allergiesDetail,
                        icon = Icons.Default.Warning,
                        contentColor = PrimaryAccent,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TriageCard(
                        title = stringResource(R.string.identity_chronic_conditions),
                        value = if (profile.conditions.isBlank()) stringResource(R.string.identity_none) else profile.conditions,
                        detail = profile.conditionsDetail,
                        icon = Icons.Default.CheckCircle,
                        contentColor = SuccessGreen,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Emergency Contacts
                    Text(
                        stringResource(R.string.identity_emergency_contacts),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SafariCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryAccent.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryAccent
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    profile.emergencyContactName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    "${profile.emergencyContactRelation} • ${profile.emergencyContactPhone}",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                            IconButton(
                                onClick = { onCallContact(profile.emergencyContactPhone) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryAccent)
                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = stringResource(R.string.identity_call),
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Authority Scan QR Component
                    AuthorityScanCard(
                        documentNumber = profile.documentNumber, 
                        googleDriveLink = profile.googleDriveLink
                    )
                    
                    Spacer(modifier = Modifier.height(120.dp))
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
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    SafariCard(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            detail.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp),
            letterSpacing = 1.sp
        )
    }
}
