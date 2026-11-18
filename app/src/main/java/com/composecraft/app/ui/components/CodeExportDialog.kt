package com.composecraft.app.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.composecraft.app.engine.ComposeCodeGenerator
import com.composecraft.app.engine.ProjectExporter
import com.composecraft.app.model.AppProject

@Composable
fun CodeExportDialog(
    project: AppProject,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedView by remember { mutableStateOf(0) } // 0 = Compose Code, 1 = AI Agent Prompt

    val currentScreen = project.currentScreen
    val screenCode = remember(currentScreen, project.packageName) {
        ComposeCodeGenerator.generateScreenCode(currentScreen, project.packageName)
    }
    val aiPrompt = remember(project) {
        ComposeCodeGenerator.generateAIPrompt(project)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(24.dp)),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Export Project & Code 🚀",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Screen: ${currentScreen.name} (${project.screens.size} screens total)",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Tabs: Compose Code vs AI Agent Prompt
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = { selectedView = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedView == 0) Color(0xFF4F46E5) else Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kotlin Compose Code", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = { selectedView = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedView == 1) Color(0xFF00F2FE) else Color.Transparent,
                            contentColor = if (selectedView == 1) Color(0xFF0F172A) else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🤖 AI Agent Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Code Display Box
                val contentToShow = if (selectedView == 0) screenCode else aiPrompt
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF050811))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    val vScroll = rememberScrollState()
                    val hScroll = rememberScrollState()

                    Text(
                        text = contentToShow,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = if (selectedView == 0) Color(0xFF38EF7D) else Color(0xFFE2E8F0),
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .verticalScroll(vScroll)
                            .horizontalScroll(hScroll)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons Row
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Copy Button
                        Button(
                            onClick = {
                                if (selectedView == 0) {
                                    ProjectExporter.copyToClipboard(context, "Jetpack Compose Code", screenCode)
                                } else {
                                    ProjectExporter.copyToClipboard(context, "AI Agent Prompt", aiPrompt)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (selectedView == 0) "Copy Code" else "Copy Prompt", fontSize = 12.sp)
                        }

                        // Share Button
                        OutlinedButton(
                            onClick = {
                                val title = if (selectedView == 0) "${currentScreen.name}View.kt" else "AI_Agent_Prompt.md"
                                ProjectExporter.shareText(context, title, contentToShow)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp)
                        }
                    }

                    // Save Entire Project to Phone Storage (Downloads)
                    Button(
                        onClick = {
                            val result = ProjectExporter.exportFullProjectToDownloads(context, project)
                            result.onSuccess { dir ->
                                Toast.makeText(context, "Saved to ${dir.absolutePath}! 💾", Toast.LENGTH_LONG).show()
                            }.onFailure { err ->
                                Toast.makeText(context, "Export error: ${err.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("💾 Download Full Project to Storage (/sdcard/Download)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
