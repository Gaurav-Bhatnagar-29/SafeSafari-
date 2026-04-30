package com.example.safarisafe.ui.screens.profile

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safarisafe.models.EmergencyContact
import com.example.safarisafe.models.UserProfile
import com.example.safarisafe.ui.theme.SafariSafeTheme
import com.example.safarisafe.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    val context = androidx.compose.ui.platform.LocalContext.current
    
    var showAddContactDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }

    if (showAddContactDialog) {
        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Add Emergency Contact", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
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
                        android.widget.Toast.makeText(context, "Contact added", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(context, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Edit Profile", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.primary)
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        viewModel.saveProfile()
                        android.widget.Toast.makeText(context, "Profile updated successfully!", android.widget.Toast.LENGTH_SHORT).show()
                        onBack()
                    }) {
                        Text("Save", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface)
            )
        },
        containerColor = colors.surface
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
            Spacer(modifier = Modifier.height(40.dp))
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(128.dp),
                    shape = CircleShape,
                    border = BorderStroke(3.dp, Color.White),
                    shadowElevation = 8.dp
                ) {
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Replace with actual image loading
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                SmallFloatingActionButton(
                    onClick = { 
                        android.widget.Toast.makeText(context, "Photo editing coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    shape = CircleShape,
                    containerColor = colors.primary,
                    contentColor = Color.White,
                    modifier = Modifier.size(40.dp).offset(x = 4.dp, y = 4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Personal Details Section
            TonalIsland(title = "Personal Details") {
                ProfileTextField(
                    label = "FULL NAME", 
                    value = uiState.fullName,
                    placeholder = "e.g. John Doe",
                    onValueChange = { viewModel.updateFullName(it) }
                )
                ProfileTextField(
                    label = "EMAIL", 
                    value = uiState.email,
                    placeholder = "e.g. john@example.com",
                    onValueChange = { viewModel.updateEmail(it) }
                )
                PhoneInput(
                    label = "PHONE NUMBER", 
                    countryCode = uiState.countryCode, 
                    phoneNumber = uiState.phoneNumber,
                    placeholderCode = "+1",
                    placeholderNumber = "(555) 000-0000",
                    onCountryCodeChange = { viewModel.updateCountryCode(it) },
                    onPhoneNumberChange = { viewModel.updatePhoneNumber(it) }
                )
                LockedTextField(label = "PASSPORT NUMBER", value = uiState.passportNumber)
            }

            // Medical Information Section
            MedicalInformationSection(uiState, viewModel)

            // Emergency Contacts Section
            TonalIsland(title = "Emergency Contacts") {
                uiState.emergencyContacts.forEach { contact ->
                    EmergencyContactItem(
                        contact = contact,
                        color = colors.primaryContainer,
                        onDelete = { viewModel.removeEmergencyContact(contact) }
                    )
                }
                
                OutlinedButton(
                    onClick = { 
                        showAddContactDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.primary.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = colors.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("ADD EMERGENCY CONTACT", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun TonalIsland(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F3FD)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, placeholder: String = "", onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFE1E2EC),
                focusedContainerColor = Color(0xFFE1E2EC),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun PhoneInput(
    label: String, 
    countryCode: String, 
    phoneNumber: String,
    placeholderCode: String = "",
    placeholderNumber: String = "",
    onCountryCodeChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = countryCode,
                onValueChange = onCountryCodeChange,
                placeholder = { Text(placeholderCode, color = Color.Gray) },
                modifier = Modifier.width(80.dp).clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE1E2EC),
                    focusedContainerColor = Color(0xFFE1E2EC),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            TextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                placeholder = { Text(placeholderNumber, color = Color.Gray) },
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE1E2EC),
                    focusedContainerColor = Color(0xFFE1E2EC),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun LockedTextField(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { Icon(Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFE1E2EC),
                focusedContainerColor = Color(0xFFE1E2EC),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun MedicalInformationSection(uiState: UserProfile, viewModel: ProfileViewModel) {
    Box(modifier = Modifier.padding(vertical = 12.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF2F3FD))) {
        // Vertical Accent Bar
        Box(modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight().width(6.dp).background(MaterialTheme.colorScheme.secondary))
        
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)) {
            Text("Medical Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("BLOOD TYPE", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.padding(vertical = 12.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("A+", "B+", "O+", "AB+", "Other").forEach { type ->
                    val isSelected = type == uiState.bloodType
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateBloodType(type) },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            containerColor = Color(0xFFE1E2EC)
                        ),
                        shape = CircleShape
                    )
                }
            }
            ProfileTextField(
                label = "KNOWN ALLERGIES", 
                value = uiState.knownAllergies,
                placeholder = "e.g. Peanuts, Aspirin",
                onValueChange = { viewModel.updateAllergies(it) }
            )
            ProfileTextField(
                label = "CHRONIC CONDITIONS", 
                value = uiState.chronicConditions,
                placeholder = "e.g. Asthma, Diabetes",
                onValueChange = { viewModel.updateConditions(it) }
            )
        }
    }
}

@Composable
fun EmergencyContactItem(contact: EmergencyContact, color: Color, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).background(Color(0xFFE1E2EC), RoundedCornerShape(12.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(48.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
            Text(contact.initial, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("${contact.relation} • ${contact.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
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
