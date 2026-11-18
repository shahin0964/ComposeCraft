package com.composecraft.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composecraft.app.engine.DynamicCanvasRenderer
import com.composecraft.app.model.ProjectTemplates
import com.composecraft.app.ui.components.*
import com.composecraft.app.viewmodel.DesignerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerScreen(
    viewModel: DesignerViewModel
) {
    val context = LocalContext.current
    val project by viewModel.project.collectAsState()
    val selectedNodeId by viewModel.selectedNodeId.collectAsState()
    val isDesignMode by viewModel.isDesignMode.collectAsState()
    val isFreeFormMode by viewModel.isFreeFormMode.collectAsState()

    var showPaletteSheet by remember { mutableStateOf(false) }
    var showLayerTree by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showAddScreenDialog by remember { mutableStateOf(false) }

    val currentScreen = project.currentScreen
    val selectedNode = viewModel.selectedNode

    Scaffold(
        containerColor = Color(0xFF090D16),
        topBar = {
            // Main Editor Top Toolbar (Android Studio Style)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back to Home Dashboard & Project Title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.navigateToHome() },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Home", tint = Color(0xFF00F2FE), modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = project.name,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isDesignMode) (if (isFreeFormMode) "🎯 Free-Form Drag Mode" else "🌲 Structured Layer Mode") else "👁️ Live Interactive Play",
                                color = if (isDesignMode) Color(0xFF00F2FE) else Color(0xFF38EF7D),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Top Action Controls: Undo, Redo, Mode Switch, Play, Export
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Undo
                        IconButton(
                            onClick = { viewModel.undo() },
                            enabled = viewModel.canUndo
                        ) {
                            Icon(
                                Icons.Default.Undo,
                                contentDescription = "Undo",
                                tint = if (viewModel.canUndo) Color.White else Color.DarkGray
                            )
                        }

                        // Redo
                        IconButton(
                            onClick = { viewModel.redo() },
                            enabled = viewModel.canRedo
                        ) {
                            Icon(
                                Icons.Default.Redo,
                                contentDescription = "Redo",
                                tint = if (viewModel.canRedo) Color.White else Color.DarkGray
                            )
                        }

                        // Canvas Mode Toggle (Structured vs Free-Form Drag)
                        IconButton(
                            onClick = { viewModel.toggleCanvasMode() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (isFreeFormMode) Color(0xFF4F46E5) else Color(0xFF1E293B)
                            )
                        ) {
                            Icon(
                                imageVector = if (isFreeFormMode) Icons.Default.OpenWith else Icons.Default.ViewStream,
                                contentDescription = "Canvas Mode",
                                tint = if (isFreeFormMode) Color(0xFF00F2FE) else Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Play / Edit Mode Toggle
                        Button(
                            onClick = { viewModel.toggleDesignMode() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDesignMode) Color(0xFF1E293B) else Color(0xFF059669)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isDesignMode) Icons.Default.PlayArrow else Icons.Default.Edit,
                                contentDescription = "Toggle Mode",
                                tint = if (isDesignMode) Color(0xFF38EF7D) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDesignMode) "Play" else "Edit",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Export Code Button
                        IconButton(
                            onClick = { showExportDialog = true },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF4F46E5))
                        ) {
                            Icon(Icons.Default.Code, contentDescription = "Export Code", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Screens Tabs Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    project.screens.forEach { screen ->
                        val isCurrent = screen.id == currentScreen.id
                        FilterChip(
                            selected = isCurrent,
                            onClick = { viewModel.switchScreen(screen.id) },
                            label = { Text(screen.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4F46E5),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color.LightGray
                            )
                        )
                    }

                    // Add Screen Chip
                    AssistChip(
                        onClick = { showAddScreenDialog = true },
                        label = { Text("+ Screen", fontSize = 12.sp, color = Color(0xFF00F2FE)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1E293B))
                    )

                    // Template Switcher Chip
                    AssistChip(
                        onClick = { showTemplateDialog = true },
                        label = { Text("⚡ Templates", fontSize = 12.sp, color = Color(0xFFFBBF24)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1E293B))
                    )
                }
            }
        },
        bottomBar = {
            // Main Bottom Action Bar
            Surface(
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Layer Hierarchy Tree Button
                    OutlinedButton(
                        onClick = { showLayerTree = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Layers, contentDescription = "Layers", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Layers", fontSize = 12.sp)
                    }

                    // Add Component Button
                    Button(
                        onClick = { showPaletteSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Component", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Add Widget", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // If node is selected, show "Properties" Button
                    if (selectedNode != null && isDesignMode) {
                        var showInspectorSheet by remember { mutableStateOf(false) }
                        Button(
                            onClick = { showInspectorSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F2FE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Style", tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Style", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (showInspectorSheet) {
                            PropertyInspectorSheet(
                                node = selectedNode,
                                onDismiss = { showInspectorSheet = false },
                                onUpdateProperties = { viewModel.updateNodeProperties(it) },
                                onUpdateName = { viewModel.updateNodeName(it) },
                                onDuplicate = { viewModel.duplicateSelectedNode() },
                                onDelete = { viewModel.deleteSelectedNode() }
                            )
                        }
                    } else {
                        Text(
                            text = if (isDesignMode) "Tap widget to style" else "Testing interactions",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // Main Canvas Workspace
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            DeviceFrame(
                screenName = currentScreen.name,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Check if canvas is completely empty
                    val isBlankCanvas = currentScreen.rootNode.children.isEmpty()

                    if (isBlankCanvas && isDesignMode) {
                        // Empty Canvas Guidance Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0F172A))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF00F2FE),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Blank Canvas",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Start designing your screen by adding widgets\n(3D Orb, Waveform, Buttons, Sliders, Cards...)",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { showPaletteSheet = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F2FE)),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text("+ Add First Component", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        // Render UI Element Hierarchy
                        DynamicCanvasRenderer(
                            node = currentScreen.rootNode,
                            isDesignMode = isDesignMode,
                            selectedNodeId = selectedNodeId,
                            isFreeFormMode = isFreeFormMode,
                            onSelectNode = { clickedNode ->
                                viewModel.selectNode(clickedNode.id)
                            },
                            onMoveNode = { id, x, y ->
                                viewModel.moveNodeFreeForm(id, x, y)
                            },
                            onActionTriggered = { actionType, actionTarget ->
                                when (actionType) {
                                    "TOAST" -> Toast.makeText(context, actionTarget.ifBlank { "Action Triggered!" }, Toast.LENGTH_SHORT).show()
                                    "NAVIGATE" -> {
                                        val targetScreen = project.screens.find { it.name.equals(actionTarget, true) || it.id == actionTarget }
                                        if (targetScreen != null) {
                                            viewModel.switchScreen(targetScreen.id)
                                            Toast.makeText(context, "Navigated to ${targetScreen.name} 📱", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Navigation target: $actionTarget", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }

                    // Floating quick '+' FAB on canvas
                    if (isDesignMode && !isBlankCanvas) {
                        FloatingActionButton(
                            onClick = { showPaletteSheet = true },
                            containerColor = Color(0xFF00F2FE),
                            contentColor = Color(0xFF0F172A),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Widget")
                        }
                    }
                }
            }
        }
    }

    // Modal Sheets & Dialogs
    if (showPaletteSheet) {
        ComponentPaletteSheet(
            onDismiss = { showPaletteSheet = false },
            onSelectComponent = { type ->
                viewModel.addElement(type)
            }
        )
    }

    if (showLayerTree) {
        LayerTreeDrawer(
            rootNode = currentScreen.rootNode,
            selectedNodeId = selectedNodeId,
            onDismiss = { showLayerTree = false },
            onSelectNode = { id ->
                viewModel.selectNode(id)
            }
        )
    }

    if (showExportDialog) {
        CodeExportDialog(
            project = project,
            onDismiss = { showExportDialog = false }
        )
    }

    if (showTemplateDialog) {
        TemplatePickerModal(
            onDismiss = { showTemplateDialog = false },
            onSelectTemplate = { t ->
                viewModel.cloneSampleProject(t)
                showTemplateDialog = false
            }
        )
    }

    if (showAddScreenDialog) {
        var newScreenName by remember { mutableStateOf("Screen ${project.screens.size + 1}") }
        AlertDialog(
            onDismissRequest = { showAddScreenDialog = false },
            containerColor = Color(0xFF1E293B),
            title = { Text("Add New Screen 📱", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = newScreenName,
                    onValueChange = { newScreenName = it },
                    label = { Text("Screen Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newScreenName.isNotBlank()) {
                            viewModel.addScreen(newScreenName)
                            showAddScreenDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Add Screen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddScreenDialog = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }
}

@Composable
fun TemplatePickerModal(
    onDismiss: () -> Unit,
    onSelectTemplate: (com.composecraft.app.model.AppProject) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Text("Starter Templates ⚡", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select a ready-made design to clone into your workspace:", color = Color.Gray, fontSize = 12.sp)

                // 3D AI Assistant Template (Highlight!)
                TemplateCard(
                    title = "Futuristic AI Voice (3D Orb) ✨",
                    subtitle = "Animated 3D Glowing Orb & live Audio Waveform",
                    badge = "3D Animated",
                    onClick = { onSelectTemplate(ProjectTemplates.createAIAssistantTemplate()) }
                )

                // Login Template
                TemplateCard(
                    title = "Modern Login Screen 🔑",
                    subtitle = "Header, text fields, CTA button, social login",
                    badge = "Flow",
                    onClick = { onSelectTemplate(ProjectTemplates.createLoginTemplate()) }
                )

                // Profile Template
                TemplateCard(
                    title = "Profile & Settings Screen 👤",
                    subtitle = "User card, switches, settings options",
                    badge = "Account",
                    onClick = { onSelectTemplate(ProjectTemplates.createProfileTemplate()) }
                )

                // E-commerce Template
                TemplateCard(
                    title = "Product Store Screen 🛍️",
                    subtitle = "Product card, banner, price, add to cart",
                    badge = "Store",
                    onClick = { onSelectTemplate(ProjectTemplates.createEcommerceTemplate()) }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.LightGray)
            }
        }
    )
}

@Composable
fun TemplateCard(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .clickable(onClick = onClick)
            .padding(12.dp),
        color = Color(0xFF1E293B)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(subtitle, color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
            Surface(
                color = if (badge.contains("3D")) Color(0xFF4F46E5) else Color(0xFF334155),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(badge, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
    }
}
