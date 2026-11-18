package com.composecraft.app.engine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composecraft.app.model.ElementProperties
import com.composecraft.app.model.UIElementNode
import com.composecraft.app.model.UIElementType
import kotlin.math.roundToInt

@Composable
fun DynamicCanvasRenderer(
    node: UIElementNode,
    isDesignMode: Boolean,
    selectedNodeId: String?,
    isFreeFormMode: Boolean = false,
    onSelectNode: (UIElementNode) -> Unit,
    onMoveNode: (String, Float, Float) -> Unit = { _, _, _ -> },
    onActionTriggered: (actionType: String, actionTarget: String) -> Unit = { _, _ -> }
) {
    val isSelected = isDesignMode && (node.id == selectedNodeId)
    val props = node.properties

    // Base modifier with width & height sizing
    var baseModifier = Modifier.buildSizeModifier(props)

    // Position offset if in Free-Form Canvas mode
    if (isFreeFormMode && (props.posX != 0f || props.posY != 0f)) {
        baseModifier = baseModifier.offset {
            IntOffset(props.posX.roundToInt(), props.posY.roundToInt())
        }
    }

    // Drag gesture support for free-form mode
    if (isDesignMode && isFreeFormMode) {
        baseModifier = baseModifier.pointerInput(node.id) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                onMoveNode(node.id, props.posX + dragAmount.x, props.posY + dragAmount.y)
            }
        }
    }

    // Apply Elevation / Shadow
    if (props.elevation > 0) {
        baseModifier = baseModifier.shadow(
            elevation = props.elevation.dp,
            shape = RoundedCornerShape(props.cornerRadius.dp)
        )
    }

    // Apply Corner Radius Clip
    if (props.cornerRadius > 0) {
        baseModifier = baseModifier.clip(RoundedCornerShape(props.cornerRadius.dp))
    }

    // Apply Background Color
    val bgColor = parseHexColor(props.backgroundColorHex)
    if (bgColor != Color.Transparent) {
        baseModifier = baseModifier.background(bgColor)
    }

    // Apply Border
    if (props.borderWidth > 0) {
        val borderColor = parseHexColor(props.borderColorHex, Color.Gray)
        baseModifier = baseModifier.border(
            width = props.borderWidth.dp,
            color = borderColor,
            shape = RoundedCornerShape(props.cornerRadius.dp)
        )
    }

    // Apply Padding
    baseModifier = baseModifier.padding(
        horizontal = props.paddingHorizontal.dp,
        vertical = props.paddingVertical.dp
    )

    // Selection border and tap in Design Mode
    val nodeModifier = if (isDesignMode) {
        baseModifier
            .border(
                width = if (isSelected) 2.dp else 0.5.dp,
                color = if (isSelected) Color(0xFF00F2FE) else Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(props.cornerRadius.dp)
            )
            .clickable { onSelectNode(node) }
    } else {
        baseModifier
    }

    // Render element based on its UIElementType
    Box(modifier = nodeModifier) {
        when (node.type) {
            // 3D & Live Animation Elements ✨
            UIElementType.GLOWING_ORB -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GlowingOrbRenderer(
                        properties = props,
                        modifier = Modifier.clickable(enabled = isDesignMode) { onSelectNode(node) }
                    )
                }
            }

            UIElementType.WAVEFORM -> {
                LiveWaveformRenderer(
                    properties = props,
                    modifier = Modifier.clickable(enabled = isDesignMode) { onSelectNode(node) }
                )
            }

            UIElementType.PULSE_CONTAINER -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(props.spacingDp.dp),
                    horizontalAlignment = parseHorizontalAlignment(props.alignment)
                ) {
                    if (node.children.isEmpty() && isDesignMode) {
                        EmptyContainerPlaceholder("✨ Pulsing Container (Drop widgets here)")
                    } else {
                        node.children.forEach { child ->
                            DynamicCanvasRenderer(
                                node = child,
                                isDesignMode = isDesignMode,
                                selectedNodeId = selectedNodeId,
                                isFreeFormMode = isFreeFormMode,
                                onSelectNode = onSelectNode,
                                onMoveNode = onMoveNode,
                                onActionTriggered = onActionTriggered
                            )
                        }
                    }
                }
            }

            UIElementType.TEXT -> {
                Text(
                    text = props.text,
                    fontSize = props.fontSize.sp,
                    fontWeight = parseFontWeight(props.fontWeight),
                    color = parseHexColor(props.textColorHex, Color.DarkGray),
                    textAlign = parseTextAlign(props.textAlign),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            UIElementType.BUTTON -> {
                Button(
                    onClick = {
                        if (!isDesignMode) {
                            onActionTriggered(props.actionType, props.actionTarget)
                        } else {
                            onSelectNode(node)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = parseHexColor(props.backgroundColorHex, Color(0xFF4F46E5)),
                        contentColor = parseHexColor(props.textColorHex, Color.White)
                    ),
                    shape = RoundedCornerShape(props.cornerRadius.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = props.text,
                        fontSize = props.fontSize.sp,
                        fontWeight = parseFontWeight(props.fontWeight)
                    )
                }
            }

            UIElementType.OUTLINED_BUTTON -> {
                OutlinedButton(
                    onClick = {
                        if (!isDesignMode) {
                            onActionTriggered(props.actionType, props.actionTarget)
                        } else {
                            onSelectNode(node)
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = parseHexColor(props.textColorHex, Color(0xFF4F46E5))
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = (if (props.borderWidth > 0) props.borderWidth else 1).dp,
                        color = parseHexColor(props.borderColorHex, Color(0xFF4F46E5))
                    ),
                    shape = RoundedCornerShape(props.cornerRadius.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = props.text,
                        fontSize = props.fontSize.sp,
                        fontWeight = parseFontWeight(props.fontWeight)
                    )
                }
            }

            UIElementType.TEXT_FIELD -> {
                var inputText by remember(node.id) { mutableStateOf(props.text) }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = {
                        inputText = it
                    },
                    placeholder = { Text(props.placeholder, color = Color.Gray) },
                    shape = RoundedCornerShape(props.cornerRadius.dp),
                    enabled = !isDesignMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            UIElementType.SWITCH -> {
                var isChecked by remember(node.id, props.isChecked) { mutableStateOf(props.isChecked) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = props.text,
                        fontSize = props.fontSize.sp,
                        color = parseHexColor(props.textColorHex, Color.Black)
                    )
                    Switch(
                        checked = isChecked,
                        onCheckedChange = { if (!isDesignMode) isChecked = it },
                        enabled = !isDesignMode
                    )
                }
            }

            UIElementType.CHECKBOX -> {
                var isChecked by remember(node.id, props.isChecked) { mutableStateOf(props.isChecked) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { if (!isDesignMode) isChecked = it },
                        enabled = !isDesignMode
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = props.text,
                        fontSize = props.fontSize.sp,
                        color = parseHexColor(props.textColorHex, Color.Black)
                    )
                }
            }

            UIElementType.SLIDER -> {
                var sliderValue by remember(node.id, props.sliderValue) { mutableStateOf(props.sliderValue) }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Value: ${sliderValue.roundToInt()}%",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Slider(
                        value = sliderValue,
                        onValueChange = { if (!isDesignMode) sliderValue = it },
                        valueRange = 0f..100f,
                        enabled = !isDesignMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            UIElementType.IMAGE -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(props.fixedHeightDp.dp)
                        .background(parseHexColor(props.backgroundColorHex, Color(0xFFE2E8F0))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Image Banner",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            UIElementType.ICON -> {
                Icon(
                    imageVector = resolveMaterialIcon(props.iconName),
                    contentDescription = props.iconName,
                    tint = parseHexColor(props.textColorHex, Color(0xFF4F46E5)),
                    modifier = Modifier.size(props.fixedWidthDp.dp)
                )
            }

            UIElementType.SPACER -> {
                Spacer(modifier = Modifier.height(props.fixedHeightDp.dp))
            }

            UIElementType.DIVIDER -> {
                HorizontalDivider(
                    thickness = (if (props.fixedHeightDp > 0) props.fixedHeightDp else 1).dp,
                    color = parseHexColor(props.borderColorHex, Color(0xFFE2E8F0))
                )
            }

            UIElementType.TOP_APP_BAR -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(props.fixedHeightDp.dp)
                        .background(parseHexColor(props.backgroundColorHex, Color(0xFF1E293B)))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = resolveMaterialIcon(props.iconName),
                        contentDescription = "Nav Icon",
                        tint = parseHexColor(props.textColorHex, Color.White),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = props.text,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = parseHexColor(props.textColorHex, Color.White)
                    )
                }
            }

            UIElementType.BOTTOM_NAV_BAR -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(props.fixedHeightDp.dp)
                        .background(parseHexColor(props.backgroundColorHex, Color(0xFF0F172A))),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Home" to Icons.Default.Home, "Search" to Icons.Default.Search, "Profile" to Icons.Default.Person).forEach { (label, icon) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icon, contentDescription = label, tint = parseHexColor(props.textColorHex, Color(0xFF94A3B8)))
                            Text(label, fontSize = 11.sp, color = parseHexColor(props.textColorHex, Color(0xFF94A3B8)))
                        }
                    }
                }
            }

            UIElementType.FLOATING_ACTION_BUTTON -> {
                FloatingActionButton(
                    onClick = {
                        if (!isDesignMode) {
                            onActionTriggered(props.actionType, props.actionTarget)
                        } else {
                            onSelectNode(node)
                        }
                    },
                    containerColor = parseHexColor(props.backgroundColorHex, Color(0xFF4F46E5)),
                    contentColor = parseHexColor(props.textColorHex, Color.White)
                ) {
                    Icon(resolveMaterialIcon(props.iconName), contentDescription = "FAB Icon")
                }
            }

            // Containers: Column, Row, Box, Card
            UIElementType.COLUMN -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(props.spacingDp.dp),
                    horizontalAlignment = parseHorizontalAlignment(props.alignment)
                ) {
                    if (node.children.isEmpty() && isDesignMode) {
                        EmptyContainerPlaceholder("Empty Column (Drop widgets here)")
                    } else {
                        node.children.forEach { child ->
                            DynamicCanvasRenderer(
                                node = child,
                                isDesignMode = isDesignMode,
                                selectedNodeId = selectedNodeId,
                                isFreeFormMode = isFreeFormMode,
                                onSelectNode = onSelectNode,
                                onMoveNode = onMoveNode,
                                onActionTriggered = onActionTriggered
                            )
                        }
                    }
                }
            }

            UIElementType.ROW -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = parseHorizontalArrangement(props.arrangement, props.spacingDp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (node.children.isEmpty() && isDesignMode) {
                        EmptyContainerPlaceholder("Empty Row")
                    } else {
                        node.children.forEach { child ->
                            DynamicCanvasRenderer(
                                node = child,
                                isDesignMode = isDesignMode,
                                selectedNodeId = selectedNodeId,
                                isFreeFormMode = isFreeFormMode,
                                onSelectNode = onSelectNode,
                                onMoveNode = onMoveNode,
                                onActionTriggered = onActionTriggered
                            )
                        }
                    }
                }
            }

            UIElementType.BOX -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (node.children.isEmpty() && isDesignMode) {
                        EmptyContainerPlaceholder("Empty Box")
                    } else {
                        node.children.forEach { child ->
                            DynamicCanvasRenderer(
                                node = child,
                                isDesignMode = isDesignMode,
                                selectedNodeId = selectedNodeId,
                                isFreeFormMode = isFreeFormMode,
                                onSelectNode = onSelectNode,
                                onMoveNode = onMoveNode,
                                onActionTriggered = onActionTriggered
                            )
                        }
                    }
                }
            }

            UIElementType.CARD -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(props.spacingDp.dp),
                    horizontalAlignment = parseHorizontalAlignment(props.alignment)
                ) {
                    if (node.children.isEmpty() && isDesignMode) {
                        EmptyContainerPlaceholder("Empty Card")
                    } else {
                        node.children.forEach { child ->
                            DynamicCanvasRenderer(
                                node = child,
                                isDesignMode = isDesignMode,
                                selectedNodeId = selectedNodeId,
                                isFreeFormMode = isFreeFormMode,
                                onSelectNode = onSelectNode,
                                onMoveNode = onMoveNode,
                                onActionTriggered = onActionTriggered
                            )
                        }
                    }
                }
            }
        }

        // Active node label badge in design mode
        if (isSelected) {
            Surface(
                color = Color(0xFF00F2FE),
                shape = RoundedCornerShape(bottomEnd = 6.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = node.name,
                    color = Color(0xFF0F172A),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyContainerPlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.Gray, fontSize = 12.sp)
    }
}

// Helpers for modifier building and parsing
private fun Modifier.buildSizeModifier(props: ElementProperties): Modifier {
    var m = this
    m = when (props.widthMode) {
        "FILL" -> m.fillMaxWidth()
        "FIXED" -> m.width(props.fixedWidthDp.dp)
        else -> m.wrapContentWidth()
    }
    m = when (props.heightMode) {
        "FILL" -> m.fillMaxHeight()
        "FIXED" -> m.height(props.fixedHeightDp.dp)
        else -> m.wrapContentHeight()
    }
    return m
}

fun parseHexColor(hex: String, default: Color = Color.Transparent): Color {
    if (hex.isBlank()) return default
    return try {
        val clean = hex.removePrefix("#")
        val colorLong = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or colorLong)
        } else if (clean.length == 8) {
            Color(colorLong)
        } else {
            default
        }
    } catch (e: Exception) {
        default
    }
}

fun parseFontWeight(weight: String): FontWeight {
    return when (weight.lowercase()) {
        "bold" -> FontWeight.Bold
        "medium" -> FontWeight.Medium
        "light" -> FontWeight.Light
        "semibold" -> FontWeight.SemiBold
        else -> FontWeight.Normal
    }
}

fun parseTextAlign(align: String): TextAlign {
    return when (align.lowercase()) {
        "center" -> TextAlign.Center
        "end" -> TextAlign.End
        else -> TextAlign.Start
    }
}

fun parseHorizontalAlignment(align: String): Alignment.Horizontal {
    return when (align.lowercase()) {
        "start" -> Alignment.Start
        "end" -> Alignment.End
        else -> Alignment.CenterHorizontally
    }
}

fun parseHorizontalArrangement(arr: String, spacingDp: Int): Arrangement.Horizontal {
    return when (arr.lowercase()) {
        "spacebetween" -> Arrangement.SpaceBetween
        "spacearound" -> Arrangement.SpaceAround
        "center" -> Arrangement.Center
        "end" -> Arrangement.End
        else -> Arrangement.spacedBy(spacingDp.dp)
    }
}

fun resolveMaterialIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "star" -> Icons.Default.Star
        "favorite" -> Icons.Default.Favorite
        "share" -> Icons.Default.Share
        "home" -> Icons.Default.Home
        "settings" -> Icons.Default.Settings
        "person" -> Icons.Default.Person
        "notifications" -> Icons.Default.Notifications
        "search" -> Icons.Default.Search
        "check" -> Icons.Default.Check
        "add" -> Icons.Default.Add
        "lock" -> Icons.Default.Lock
        "shoppingcart", "shoppingbag" -> Icons.Default.ShoppingCart
        "arrowback" -> Icons.Default.ArrowBack
        else -> Icons.Default.Star
    }
}
