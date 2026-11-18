package com.composecraft.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composecraft.app.model.UIElementType
import com.composecraft.app.model.WidgetCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentPaletteSheet(
    onDismiss: () -> Unit,
    onSelectComponent: (UIElementType) -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Component 🧩",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                }
            }

            Text(
                text = "Tap any element to add it directly to your current container or canvas",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WidgetCategory.values().forEach { category ->
                    val categoryItems = UIElementType.values().filter { it.category == category }
                    if (categoryItems.isNotEmpty()) {
                        item {
                            Text(
                                text = category.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (category == WidgetCategory.ANIMATIONS) Color(0xFF00F2FE) else Color(0xFF818CF8),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(categoryItems.chunked(2)) { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                pair.forEach { type ->
                                    ComponentTile(
                                        type = type,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            onSelectComponent(type)
                                            onDismiss()
                                        }
                                    )
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComponentTile(
    type: UIElementType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isSpecial = type.category == WidgetCategory.ANIMATIONS
    val bgColor = if (isSpecial) Color(0xFF1E1B4B) else Color(0xFF1E293B)
    val borderColor = if (isSpecial) Color(0xFF00F2FE).copy(alpha = 0.6f) else Color(0xFF334155)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSpecial) Color(0xFF00F2FE).copy(alpha = 0.2f) else Color(0xFF334155)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = resolvePaletteIcon(type),
                contentDescription = type.displayName,
                tint = if (isSpecial) Color(0xFF00F2FE) else Color(0xFF818CF8),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = type.displayName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            if (isSpecial) {
                Text(
                    text = "Live Interactive",
                    fontSize = 10.sp,
                    color = Color(0xFF38EF7D)
                )
            }
        }
    }
}

fun resolvePaletteIcon(type: UIElementType): ImageVector {
    return when (type) {
        UIElementType.GLOWING_ORB -> Icons.Default.Animation
        UIElementType.WAVEFORM -> Icons.Default.GraphicEq
        UIElementType.PULSE_CONTAINER -> Icons.Default.BlurOn
        UIElementType.COLUMN -> Icons.Default.ViewColumn
        UIElementType.ROW -> Icons.Default.ViewStream
        UIElementType.BOX -> Icons.Default.Layers
        UIElementType.CARD -> Icons.Default.CropOriginal
        UIElementType.TEXT -> Icons.Default.Title
        UIElementType.BUTTON -> Icons.Default.SmartButton
        UIElementType.OUTLINED_BUTTON -> Icons.Default.CheckBoxOutlineBlank
        UIElementType.TEXT_FIELD -> Icons.Default.Edit
        UIElementType.IMAGE -> Icons.Default.Image
        UIElementType.ICON -> Icons.Default.Star
        UIElementType.SWITCH -> Icons.Default.ToggleOn
        UIElementType.CHECKBOX -> Icons.Default.CheckBox
        UIElementType.SLIDER -> Icons.Default.LinearScale
        UIElementType.SPACER -> Icons.Default.SpaceBar
        UIElementType.DIVIDER -> Icons.Default.HorizontalRule
        UIElementType.TOP_APP_BAR -> Icons.Default.WebAsset
        UIElementType.BOTTOM_NAV_BAR -> Icons.Default.CallToAction
        UIElementType.FLOATING_ACTION_BUTTON -> Icons.Default.AddCircle
    }
}
