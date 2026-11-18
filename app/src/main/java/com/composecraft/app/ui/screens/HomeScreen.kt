package com.composecraft.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composecraft.app.model.AppProject
import com.composecraft.app.model.ProjectTemplates
import com.composecraft.app.viewmodel.DesignerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DesignerViewModel
) {
    val context = LocalContext.current
    val savedProjects by viewModel.savedProjects.collectAsState()

    var selectedSection by remember { mutableIntStateOf(0) } // 0 = Recent Projects, 1 = Sample Templates
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF090D16)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Android Studio Style IDE Welcome Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF00F2FE), Color(0xFF4F46E5)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "ComposeCraft",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Android Visual UI/UX Studio",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "v1.0.3",
                        color = Color(0xFF00F2FE),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action Buttons Row (Create, Import, Settings)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Create New Blank Project
                ActionTile(
                    title = "New Project",
                    subtitle = "Blank Canvas",
                    icon = Icons.Default.Add,
                    accentColor = Color(0xFF00F2FE),
                    modifier = Modifier.weight(1f),
                    onClick = { showNewProjectDialog = true }
                )

                // 2. Import Project
                ActionTile(
                    title = "Open / Import",
                    subtitle = "JSON Project",
                    icon = Icons.Default.FolderOpen,
                    accentColor = Color(0xFF818CF8),
                    modifier = Modifier.weight(1f),
                    onClick = { showImportDialog = true }
                )

                // 3. Settings
                ActionTile(
                    title = "Settings",
                    subtitle = "Preferences",
                    icon = Icons.Default.Settings,
                    accentColor = Color(0xFFFBBF24),
                    modifier = Modifier.weight(1f),
                    onClick = { showSettingsDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Switcher: Recent Projects vs Starter Samples
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .padding(4.dp)
            ) {
                Button(
                    onClick = { selectedSection = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSection == 0) Color(0xFF4F46E5) else Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("My Projects (${savedProjects.size})", fontSize = 12.sp)
                }

                Button(
                    onClick = { selectedSection = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSection == 1) Color(0xFF00F2FE) else Color.Transparent,
                        contentColor = if (selectedSection == 1) Color(0xFF0F172A) else Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sample Showcase", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content Area
            if (selectedSection == 0) {
                // Section: Recent Projects
                if (savedProjects.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF131A2B))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.DashboardCustomize,
                                contentDescription = null,
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Recent Projects Yet",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap 'New Project' above to open a clean blank canvas\nand design your UI from scratch with the + button!",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showNewProjectDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F2FE)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+ Start New App", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(savedProjects, key = { it.id }) { project ->
                            ProjectListItem(
                                project = project,
                                onOpen = { viewModel.openProject(project) },
                                onDelete = { viewModel.deleteSavedProject(project.id) }
                            )
                        }
                    }
                }
            } else {
                // Section: Starter Samples / Inspiration Showcase
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Explore pre-designed architectures to learn or customize:",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    // 1. Futuristic 3D AI Voice Assistant
                    item {
                        SampleProjectCard(
                            title = "3D AI Voice Assistant ✨",
                            description = "Features live rotating 3D Glowing Orb, dancing Audio Waveform, and speech cards.",
                            badge = "3D & Live Motion",
                            badgeColor = Color(0xFF00F2FE),
                            onClick = { viewModel.cloneSampleProject(ProjectTemplates.createAIAssistantTemplate()) }
                        )
                    }

                    // 2. Modern Login & Auth Flow
                    item {
                        SampleProjectCard(
                            title = "Modern Authentication Flow 🔑",
                            description = "Includes email/password input fields, social login buttons, and validation placeholders.",
                            badge = "Auth Flow",
                            badgeColor = Color(0xFF818CF8),
                            onClick = { viewModel.cloneSampleProject(ProjectTemplates.createLoginTemplate()) }
                        )
                    }

                    // 3. User Profile & Settings
                    item {
                        SampleProjectCard(
                            title = "Profile & Settings Dashboard 👤",
                            description = "User avatar header, interactive switch toggles, notification cards, and edit buttons.",
                            badge = "Profile",
                            badgeColor = Color(0xFF38EF7D),
                            onClick = { viewModel.cloneSampleProject(ProjectTemplates.createProfileTemplate()) }
                        )
                    }

                    // 4. E-Commerce Product Store
                    item {
                        SampleProjectCard(
                            title = "E-Commerce Product Showcase 🛍️",
                            description = "Search bar, product card, banner image, pricing row, and Add to Cart CTA.",
                            badge = "Store",
                            badgeColor = Color(0xFFFBBF24),
                            onClick = { viewModel.cloneSampleProject(ProjectTemplates.createEcommerceTemplate()) }
                        )
                    }
                }
            }
        }
    }

    // Dialog: Create New Blank Project
    if (showNewProjectDialog) {
        var projName by remember { mutableStateOf("My Application") }
        var pkgName by remember { mutableStateOf("com.example.myapp") }

        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            containerColor = Color(0xFF1E293B),
            title = {
                Text("Create New Android App 📱", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "This will open a clean, 100% blank canvas. You can use the + button to build everything from scratch!",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = projName,
                        onValueChange = { projName = it },
                        label = { Text("Project / App Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00F2FE), unfocusedBorderColor = Color(0xFF475569)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pkgName,
                        onValueChange = { pkgName = it },
                        label = { Text("Package Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00F2FE), unfocusedBorderColor = Color(0xFF475569)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createNewBlankProject(projName, pkgName)
                        showNewProjectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F2FE)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Open Blank Canvas", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }

    // Dialog: Import Project
    if (showImportDialog) {
        var jsonText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            containerColor = Color(0xFF1E293B),
            title = {
                Text("Import Project JSON 📥", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Paste project JSON string to import and open in editor:", color = Color.Gray, fontSize = 12.sp)
                    OutlinedTextField(
                        value = jsonText,
                        onValueChange = { jsonText = it },
                        placeholder = { Text("Paste JSON here...") },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (jsonText.isNotBlank()) {
                            val success = viewModel.importProjectFromJson(jsonText)
                            if (success) {
                                showImportDialog = false
                                Toast.makeText(context, "Project imported successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Invalid JSON format!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Import & Open")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }

    // Dialog: Settings
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = Color(0xFF1E293B),
            title = {
                Text("ComposeCraft Settings ⚙️", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("• Target Framework: Jetpack Compose (Kotlin 2.0+)", color = Color(0xFFE2E8F0), fontSize = 13.sp)
                    Text("• Storage Export Directory: /sdcard/Download/", color = Color(0xFFE2E8F0), fontSize = 13.sp)
                    Text("• Output Compatibility: 100% Android Studio & AI Agent Ready", color = Color(0xFF38EF7D), fontSize = 13.sp)
                    Text("• Visual Canvas: Drag-and-drop & Layer Tree hierarchy", color = Color(0xFF00F2FE), fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131A2B))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        color = Color(0xFF131A2B)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
    }
}

@Composable
fun ProjectListItem(
    project: AppProject,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(project.updatedAtTimestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(project.updatedAtTimestamp))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131A2B))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .clickable(onClick = onOpen)
            .padding(14.dp),
        color = Color(0xFF131A2B)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF00F2FE))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(project.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${project.screens.size} screens • $dateStr", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }

            Row {
                IconButton(onClick = onOpen) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Open", tint = Color(0xFF00F2FE))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF43F5E))
                }
            }
        }
    }
}

@Composable
fun SampleProjectCard(
    title: String,
    description: String,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131A2B))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        color = Color(0xFF131A2B)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badge,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = Color(0xFF94A3B8), fontSize = 11.sp, lineHeight = 16.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Open", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
