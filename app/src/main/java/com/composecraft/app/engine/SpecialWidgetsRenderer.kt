package com.composecraft.app.engine

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composecraft.app.model.ElementProperties
import kotlin.math.roundToInt

/**
 * 1. Slide-to-Action / Swipe Bar: Draggable track with live progress and unlock action
 */
@Composable
fun SlideToActionWidget(
    properties: ElementProperties,
    isDesignMode: Boolean,
    onActionCompleted: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trackWidthDp = 280.dp
    val thumbSizeDp = 48.dp
    val density = LocalDensity.current
    val maxDragPx = with(density) { (trackWidthDp - thumbSizeDp - 8.dp).toPx() }

    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    val progress = (dragOffsetPx / maxDragPx).coerceIn(0f, 1f)

    val trackColor = parseHexColor(properties.backgroundColorHex, Color(0xFF1E293B))
    val accentColor = parseHexColor(properties.orbColorPrimaryHex, Color(0xFF00F2FE))

    Box(
        modifier = modifier
            .width(trackWidthDp)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(trackColor)
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(28.dp))
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Track Highlight Fill based on drag progress
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(with(density) { (dragOffsetPx + thumbSizeDp.toPx()).toDp() })
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(accentColor.copy(alpha = 0.25f), accentColor.copy(alpha = 0.5f))
                    )
                )
        )

        // Track Text Hint
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (progress > 0.85f) "Release to Confirm" else properties.text.ifBlank { "Slide to confirm >>" },
                color = Color.White.copy(alpha = (1f - progress * 0.8f).coerceIn(0.2f, 1f)),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Draggable Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffsetPx.roundToInt(), 0) }
                .size(thumbSizeDp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White, accentColor)
                    )
                )
                .shadow(6.dp, CircleShape)
                .draggable(
                    enabled = !isDesignMode,
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val newOffset = (dragOffsetPx + delta).coerceIn(0f, maxDragPx)
                        dragOffsetPx = newOffset
                    },
                    onDragStopped = {
                        if (dragOffsetPx >= maxDragPx * 0.85f) {
                            dragOffsetPx = maxDragPx
                            onActionCompleted(properties.actionTarget.ifBlank { "Action Completed via Slide Bar!" })
                        } else {
                            dragOffsetPx = 0f
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (progress > 0.85f) Icons.Default.Check else Icons.Default.ArrowForward,
                contentDescription = "Slide Thumb",
                tint = Color(0xFF0F172A),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * 2. Floating Island Dock Navigation: Sleek floating pill navbar with neon indicator
 */
@Composable
fun FloatingIslandNavBarWidget(
    properties: ElementProperties,
    modifier: Modifier = Modifier,
    onTabSelected: (Int) -> Unit = {}
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val items = listOf("Home" to Icons.Default.Home, "Explore" to Icons.Default.Search, "AI Chat" to Icons.Default.AutoAwesome, "Profile" to Icons.Default.Person)

    val navBg = parseHexColor(properties.backgroundColorHex, Color(0xFF0F172A))
    val activeColor = parseHexColor(properties.orbColorPrimaryHex, Color(0xFF00F2FE))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(navBg.copy(alpha = 0.95f))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(32.dp))
                .shadow(12.dp, RoundedCornerShape(32.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (label, icon) ->
                val isSelected = selectedIndex == index
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) activeColor.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable {
                            selectedIndex = index
                            onTabSelected(index)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) activeColor else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            color = activeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. Segmented Pill Tab Switcher: Modern iOS / Material style category switcher
 */
@Composable
fun SegmentedTabsWidget(
    properties: ElementProperties,
    modifier: Modifier = Modifier,
    onTabSelected: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabLabels = listOf("Overview", "Features", "Reviews")

    val bg = parseHexColor(properties.backgroundColorHex, Color(0xFF1E293B))
    val activeBg = parseHexColor(properties.orbColorSecondaryHex, Color(0xFF4F46E5))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabLabels.forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) activeBg else Color.Transparent)
                    .clickable {
                        selectedTab = index
                        onTabSelected(index)
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color(0xFF94A3B8)
                )
            }
        }
    }
}

/**
 * 4. Shimmer Loading Skeleton: Real-time shining loading effect
 */
@Composable
fun ShimmerLoadingWidget(
    properties: ElementProperties,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    val shimmerColors = listOf(
        Color(0xFF1E293B),
        Color(0xFF334155),
        Color(0xFF1E293B)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 400f, translateAnim - 400f),
        end = Offset(translateAnim, translateAnim)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(properties.cornerRadius.dp))
            .background(Color(0xFF0F172A))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Simulated Shimmer Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(brush)
        )
        // Simulated Shimmer Title Line
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(brush)
        )
        // Simulated Shimmer Subtitle Line
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
    }
}

/**
 * 5. Animated Rotating Gradient Border Card
 */
@Composable
fun RotatingBorderCardWidget(
    properties: ElementProperties,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "BorderRotation")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Angle"
    )

    val primary = parseHexColor(properties.orbColorPrimaryHex, Color(0xFF00F2FE))
    val secondary = parseHexColor(properties.orbColorSecondaryHex, Color(0xFF7F00FF))

    val borderBrush = Brush.sweepGradient(
        listOf(primary, secondary, Color.Transparent, primary)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(properties.cornerRadius.dp))
            .background(borderBrush)
            .padding(2.dp) // Border thickness
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(properties.cornerRadius.dp - 2))
                .background(parseHexColor(properties.backgroundColorHex, Color(0xFF0F172A)))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * 6. Slide-Out Sidebar Drawer Preview (Mini drawer mockup)
 */
@Composable
fun SideDrawerPreviewWidget(
    properties: ElementProperties,
    modifier: Modifier = Modifier
) {
    val bg = parseHexColor(properties.backgroundColorHex, Color(0xFF0F172A))
    val accent = parseHexColor(properties.orbColorPrimaryHex, Color(0xFF00F2FE))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Drawer Header (User Avatar + Title)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = accent)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Workspace Drawer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Slide Navigation Menu", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(10.dp))

        // Menu items
        listOf("Dashboard" to Icons.Default.Dashboard, "Analytics" to Icons.Default.Insights, "Messages" to Icons.Default.Chat, "Settings" to Icons.Default.Settings).forEachIndexed { i, (label, icon) ->
            val isFirst = i == 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isFirst) accent.copy(alpha = 0.15f) else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = label, tint = if (isFirst) accent else Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = label, color = if (isFirst) accent else Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = if (isFirst) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}
