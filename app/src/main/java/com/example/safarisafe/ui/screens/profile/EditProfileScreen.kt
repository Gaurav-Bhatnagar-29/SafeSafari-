package com.example.safarisafe.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safarisafe.models.EmergencyContact
import com.example.safarisafe.models.UserProfile
import com.example.safarisafe.ui.components.SafariButton
import com.example.safarisafe.ui.components.SafariCard
import com.example.safarisafe.ui.theme.*
import com.example.safarisafe.viewmodel.ProfileViewModel
import com.example.safarisafe.viewmodel.SafetyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(),
    safetyViewModel: SafetyViewModel? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var showAddContactDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }

    var showPhotoDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val path = viewModel.saveBitmapAndGetUri(bitmap)
            if (path != null) {
                viewModel.updateProfileImage(path)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.updateProfileImage(uri.toString())
        }
    }

    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Update Profile Photo", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Take Photo", color = TextPrimary) },
                        leadingContent = { Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = PrimaryAccent) },
                        modifier = Modifier.clickable { 
                            showPhotoDialog = false
                            cameraLauncher.launch(null)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    ListItem(
                        headlineContent = { Text("Choose from Gallery", color = TextPrimary) },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PrimaryAccent) },
                        modifier = Modifier.clickable { 
                            showPhotoDialog = false
                            galleryLauncher.launch("image/*")
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoDialog = false }) {
                    Text("Cancel", color = PrimaryAccent)
                }
            }
        )
    }

    if (showAddContactDialog) {
        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Add Emergency Contact", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = SurfaceContainerHighest,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = PrimaryAccent,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = SurfaceContainerHighest,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = PrimaryAccent,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newContactName.isNotBlank() && newContactPhone.isNotBlank()) {
                        viewModel.addEmergencyContact(EmergencyContact(newContactName, "Contact", newContactPhone))
                        newContactName = ""
                        newContactPhone = ""
                        showAddContactDialog = false
                    }
                }) {
                    Text("Add", color = PrimaryAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Edit Profile", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        viewModel.saveProfile()
                        safetyViewModel?.updateFromUserProfile(uiState)
                        onBack()
                    }) {
                        Text("Save", color = PrimaryAccent, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo Section
            Spacer(modifier = Modifier.height(32.dp))
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(140.dp),
                    shape = CircleShape,
                    border = BorderStroke(3.dp, PrimaryAccent.copy(alpha = 0.5f)),
                    color = SurfaceDark
                ) {
                    val imageUri = uiState.profileImageUri
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
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            tint = TextSecondary
                        )
                    }
                }
                SmallFloatingActionButton(
                    onClick = { showPhotoDialog = true },
                    shape = CircleShape,
                    containerColor = PrimaryAccent,
                    contentColor = Color.White,
                    modifier = Modifier.size(44.dp).offset(x = (-4).dp, y = (-4).dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Personal Details Section
            SafariCard {
                Text("Personal Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(24.dp))
                
                ProfileTextField(
                    label = "FULL NAME", 
                    value = uiState.fullName,
                    onValueChange = { viewModel.updateFullName(it) }
                )
                ProfileTextField(
                    label = "EMAIL", 
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) }
                )
                PhoneInput(
                    label = "PHONE NUMBER", 
                    countryCode = uiState.countryCode, 
                    phoneNumber = uiState.phoneNumber,
                    onCountryCodeChange = { viewModel.updateCountryCode(it) },
                    onPhoneNumberChange = { viewModel.updatePhoneNumber(it) }
                )
                ProfileTextField(
                    label = "NATIONALITY", 
                    value = uiState.nationality,
                    onValueChange = { viewModel.updateNationality(it) }
                )
                ProfileTextField(
                    label = "PASSPORT NUMBER", 
                    value = uiState.passportNumber,
                    onValueChange = { viewModel.updatePassportNumber(it) }
                )
                ProfileTextField(
                    label = "GOOGLE DRIVE FOLDER LINK", 
                    value = uiState.googleDriveLink,
                    onValueChange = { viewModel.updateGoogleDriveLink(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Medical Information Section
            MedicalInformationSection(uiState, viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Emergency Contacts Section
            SafariCard {
                Text("Emergency Contacts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(24.dp))
                
                uiState.emergencyContacts.forEach { contact ->
                    EmergencyContactItem(
                        contact = contact,
                        onDelete = { viewModel.removeEmergencyContact(contact) }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SafariButton(
                    text = "+ ADD CONTACT",
                    onClick = { showAddContactDialog = true },
                    containerColor = PrimaryAccent.copy(alpha = 0.1f),
                    contentColor = PrimaryAccent
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = BackgroundDark,
                focusedContainerColor = BackgroundDark,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = PrimaryAccent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Composable
fun PhoneInput(
    label: String, 
    countryCode: String, 
    phoneNumber: String,
    onCountryCodeChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextField(
                value = countryCode,
                onValueChange = onCountryCodeChange,
                modifier = Modifier.width(80.dp).clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundDark,
                    focusedContainerColor = BackgroundDark,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = PrimaryAccent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
            TextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundDark,
                    focusedContainerColor = BackgroundDark,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = PrimaryAccent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalInformationSection(uiState: UserProfile, viewModel: ProfileViewModel) {
    SafariCard {
        Text("Medical Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("BLOOD TYPE", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Row(
            modifier = Modifier.padding(vertical = 12.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("A+", "B+", "O+", "AB+", "Other").forEach { type ->
                val isSelected = type == uiState.bloodType
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateBloodType(type) },
                    label = { Text(type) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryAccent.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryAccent,
                        containerColor = BackgroundDark,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) PrimaryAccent else Color.Transparent,
                        borderWidth = 1.dp
                    ),
                    shape = CircleShape
                )
            }
        }
        
        ProfileTextField(
            label = "KNOWN ALLERGIES", 
            value = uiState.knownAllergies,
            onValueChange = { viewModel.updateAllergies(it) }
        )
        ProfileTextField(
            label = "CHRONIC CONDITIONS", 
            value = uiState.chronicConditions,
            onValueChange = { viewModel.updateConditions(it) }
        )
    }
}

@Composable
fun EmergencyContactItem(contact: EmergencyContact, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(BackgroundDark, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(PrimaryAccent.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(contact.initial, fontWeight = FontWeight.Bold, color = PrimaryAccent)
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text("${contact.relation} • ${contact.phone}", fontSize = 12.sp, color = TextSecondary)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed.copy(alpha = 0.7f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditProfile() {
    SafariSafeTheme {
        EditProfileScreen()
    }
}
