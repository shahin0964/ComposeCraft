package com.composecraft.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composecraft.app.engine.parseHexColor
import com.composecraft.app.model.ElementProperties
import com.composecraft.app.model.UIElementNode
import com.composecraft.app.model.UIElementType
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyInspectorSheet(
    node: UIElementNode,
    onDismiss: () -> Unit,
    onUpdateProperties: (ElementProperties) -> Unit,
    onUpdateName: (String) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Content", "Style & Color", "Layout", "Interactions")

    var nameText by remember(node.id) { mutableStateOf(node.name) }
    val props = node.properties

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header with Node Name, Duplicate, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Edit: ${node.type.displayName}",
                        fontSize = 12.sp,
                        color = Color(0xFF00F2FE),
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = {
                            nameText = it
                            onUpdateName(it)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00F2FE),
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row {
                    IconButton(
                        onClick = onDuplicate,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color.LightGray)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF3F1D28))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF43F5E))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                edgePadding = 8.dp,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF00F2FE) else Color.Gray
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    0 -> ContentTab(node, props, onUpdateProperties)
                    1 -> StyleColorTab(node, props, onUpdateProperties)
                    2 -> LayoutTab(props, onUpdateProperties)
                    3 -> InteractionsTab(props, onUpdateProperties)
                }
            }
        }
    }
}

@Composable
fun ContentTab(
    node: UIElementNode,
    props: ElementProperties,
    onUpdate: (ElementProperties) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Text / Label input
        if (node.type in listOf(UIElementType.TEXT, UIElementType.BUTTON, UIElementType.OUTLINED_BUTTON, UIElementType.SWITCH, UIElementType.CHECKBOX, UIElementType.TOP_APP_BAR)) {
            var textVal by remember(node.id, props.text) { mutableStateOf(props.text) }
            Text("Display Text", fontSize = 12.sp, color = Color.Gray)
            OutlinedTextField(
                value = textVal,
                onValueChange = {
                    textVal = it
                    onUpdate(props.copy(text = it))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
        }

        // Placeholder for Text Field
        if (node.type == UIElementType.TEXT_FIELD) {
            var phVal by remember(node.id, props.placeholder) { mutableStateOf(props.placeholder) }
            Text("Placeholder Hint", fontSize = 12.sp, color = Color.Gray)
            OutlinedTextField(
                value = phVal,
                onValueChange = {
                    phVal = it
                    onUpdate(props.copy(placeholder = it))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
        }

        // Typography: Font Size
        if (node.type in listOf(UIElementType.TEXT, UIElementType.BUTTON, UIElementType.OUTLINED_BUTTON, UIElementType.TOP_APP_BAR)) {
            Text("Font Size: ${props.fontSize} sp", fontSize = 12.sp, color = Color.Gray)
            Slider(
                value = props.fontSize.toFloat(),
                onValueChange = { onUpdate(props.copy(fontSize = it.roundToInt())) },
                valueRange = 10f..40f
            )

            // Font Weight
            Text("Font Weight", fontSize = 12.sp, color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Normal", "Medium", "Bold").forEach { weight ->
                    FilterChip(
                        selected = props.fontWeight.equals(weight, true),
                        onClick = { onUpdate(props.copy(fontWeight = weight)) },
                        label = { Text(weight) }
                    )
                }
            }
        }

        // Icon Selector
        if (node.type in listOf(UIElementType.ICON, UIElementType.TOP_APP_BAR, UIElementType.FLOATING_ACTION_BUTTON)) {
            Text("Material Icon", fontSize = 12.sp, color = Color.Gray)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Star", "Favorite", "Share", "Home", "Settings", "Person", "Notifications", "Search", "Check", "Add", "Lock", "ArrowBack")) { iconName ->
                    FilterChip(
                        selected = props.iconName.equals(iconName, true),
                        onClick = { onUpdate(props.copy(iconName = iconName)) },
                        label = { Text(iconName) }
                    )
                }
            }
        }
    }
}

@Composable
fun StyleColorTab(
    node: UIElementNode,
    props: ElementProperties,
    onUpdate: (ElementProperties) -> Unit
) {
    val presetColors = listOf(
        "#4F46E5", "#00F2FE", "#7F00FF", "#10B981", "#F59E0B",
        "#EF4444", "#EC4899", "#0F172A", "#1E293B", "#FFFFFF"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 3D Orb specific gradient colors
        if (node.type == UIElementType.GLOWING_ORB) {
            Text("Primary Orb Color (Neon)", fontSize = 12.sp, color = Color(0xFF00F2FE), fontWeight = FontWeight.Bold)
            ColorRow(presetColors, props.orbColorPrimaryHex) { onUpdate(props.copy(orbColorPrimaryHex = it)) }

            Text("Secondary Orb Color (Deep)", fontSize = 12.sp, color = Color(0xFF818CF8), fontWeight = FontWeight.Bold)
            ColorRow(presetColors, props.orbColorSecondaryHex) { onUpdate(props.copy(orbColorSecondaryHex = it)) }

            Text("Outer Glow Aura Color", fontSize = 12.sp, color = Color(0xFF38EF7D), fontWeight = FontWeight.Bold)
            ColorRow(presetColors, props.orbGlowColorHex) { onUpdate(props.copy(orbGlowColorHex = it)) }

            Text("Orb Size: ${props.orbSizeDp} dp", fontSize = 12.sp, color = Color.Gray)
            Slider(
                value = props.orbSizeDp.toFloat(),
                onValueChange = { onUpdate(props.copy(orbSizeDp = it.roundToInt())) },
                valueRange = 80f..260f
            )
            return
        }

        // Waveform specific
        if (node.type == UIElementType.WAVEFORM) {
            Text("Waveform Color", fontSize = 12.sp, color = Color(0xFF00F2FE), fontWeight = FontWeight.Bold)
            ColorRow(presetColors, props.waveformColorHex) { onUpdate(props.copy(waveformColorHex = it)) }

            Text("Bar Count: ${props.waveformBars}", fontSize = 12.sp, color = Color.Gray)
            Slider(
                value = props.waveformBars.toFloat(),
                onValueChange = { onUpdate(props.copy(waveformBars = it.roundToInt())) },
                valueRange = 8f..32f
            )
            return
        }

        // Standard Background Color
        Text("Background Color", fontSize = 12.sp, color = Color.Gray)
        ColorRow(presetColors, props.backgroundColorHex) { onUpdate(props.copy(backgroundColorHex = it)) }

        // Text / Foreground Color
        Text("Text & Icon Color", fontSize = 12.sp, color = Color.Gray)
        ColorRow(presetColors, props.textColorHex) { onUpdate(props.copy(textColorHex = it)) }

        // Corner Radius
        Text("Corner Radius: ${props.cornerRadius} dp", fontSize = 12.sp, color = Color.Gray)
        Slider(
            value = props.cornerRadius.toFloat(),
            onValueChange = { onUpdate(props.copy(cornerRadius = it.roundToInt())) },
            valueRange = 0f..32f
        )

        // Elevation / Shadow
        Text("Elevation Shadow: ${props.elevation} dp", fontSize = 12.sp, color = Color.Gray)
        Slider(
            value = props.elevation.toFloat(),
            onValueChange = { onUpdate(props.copy(elevation = it.roundToInt())) },
            valueRange = 0f..20f
        )

        // Border Width & Color
        Text("Border Width: ${props.borderWidth} dp", fontSize = 12.sp, color = Color.Gray)
        Slider(
            value = props.borderWidth.toFloat(),
            onValueChange = { onUpdate(props.copy(borderWidth = it.roundToInt())) },
            valueRange = 0f..8f
        )
        if (props.borderWidth > 0) {
            Text("Border Color", fontSize = 12.sp, color = Color.Gray)
            ColorRow(presetColors, props.borderColorHex) { onUpdate(props.copy(borderColorHex = it)) }
        }
    }
}

@Composable
fun LayoutTab(
    props: ElementProperties,
    onUpdate: (ElementProperties) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Width Mode
        Text("Width Mode", fontSize = 12.sp, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("FILL" to "Match Parent", "WRAP" to "Wrap Content", "FIXED" to "Fixed Dp").forEach { (mode, label) ->
                FilterChip(
                    selected = props.widthMode == mode,
                    onClick = { onUpdate(props.copy(widthMode = mode)) },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        // Padding
        Text("Horizontal Padding: ${props.paddingHorizontal} dp", fontSize = 12.sp, color = Color.Gray)
        Slider(
            value = props.paddingHorizontal.toFloat(),
            onValueChange = { onUpdate(props.copy(paddingHorizontal = it.roundToInt())) },
            valueRange = 0f..40f
        )

        Text("Vertical Padding: ${props.paddingVertical} dp", fontSize = 12.sp, color = Color.Gray)
        Slider(
            value = props.paddingVertical.toFloat(),
            onValueChange = { onUpdate(props.copy(paddingVertical = it.roundToInt())) },
            valueRange = 0f..40f
        )

        // Container Spacing
        Text("Inner Items Spacing: ${props.spacingDp} dp", fontSize = 12.sp, color = Color.Gray)
        Slider(
            value = props.spacingDp.toFloat(),
            onValueChange = { onUpdate(props.copy(spacingDp = it.roundToInt())) },
            valueRange = 0f..32f
        )
    }
}

@Composable
fun InteractionsTab(
    props: ElementProperties,
    onUpdate: (ElementProperties) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("On Tap / Click Action", fontSize = 12.sp, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("NONE" to "None", "TOAST" to "Show Toast", "NAVIGATE" to "Navigate Screen").forEach { (action, label) ->
                FilterChip(
                    selected = props.actionType == action,
                    onClick = { onUpdate(props.copy(actionType = action)) },
                    label = { Text(label) }
                )
            }
        }

        if (props.actionType != "NONE") {
            var targetVal by remember(props.actionTarget) { mutableStateOf(props.actionTarget) }
            Text(if (props.actionType == "TOAST") "Toast Message" else "Target Screen Name", fontSize = 12.sp, color = Color.Gray)
            OutlinedTextField(
                value = targetVal,
                onValueChange = {
                    targetVal = it
                    onUpdate(props.copy(actionTarget = it))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
        }
    }
}

@Composable
fun ColorRow(
    colors: List<String>,
    selectedHex: String,
    onSelect: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(colors) { hex ->
            val color = parseHexColor(hex)
            val isSelected = selectedHex.equals(hex, true)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color(0xFF00F2FE) else Color(0xFF475569),
                        shape = CircleShape
                    )
                    .clickable { onSelect(hex) }
            )
        }
    }
}
