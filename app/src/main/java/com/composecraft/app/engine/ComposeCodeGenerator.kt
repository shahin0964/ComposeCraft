package com.composecraft.app.engine

import com.composecraft.app.model.AppProject
import com.composecraft.app.model.AppScreen
import com.composecraft.app.model.ElementProperties
import com.composecraft.app.model.UIElementNode
import com.composecraft.app.model.UIElementType

object ComposeCodeGenerator {

    /**
     * Generates a fully self-contained Kotlin file for an entire AppScreen,
     * including imports, the main Composable, 3D Orb/Waveform helpers if needed, and a Preview.
     */
    fun generateScreenCode(screen: AppScreen, packageName: String = "com.example.myapp"): String {
        val functionName = screen.name.replace(" ", "") + "View"
        val hasOrb = screen.rootNode.findNodeByType(UIElementType.GLOWING_ORB) != null
        val hasWaveform = screen.rootNode.findNodeByType(UIElementType.WAVEFORM) != null

        val sb = StringBuilder()
        sb.appendLine("package $packageName.ui.screens")
        sb.appendLine()
        sb.appendLine("import androidx.compose.animation.core.*")
        sb.appendLine("import androidx.compose.foundation.Canvas")
        sb.appendLine("import androidx.compose.foundation.background")
        sb.appendLine("import androidx.compose.foundation.border")
        sb.appendLine("import androidx.compose.foundation.clickable")
        sb.appendLine("import androidx.compose.foundation.layout.*")
        sb.appendLine("import androidx.compose.foundation.shape.RoundedCornerShape")
        sb.appendLine("import androidx.compose.material.icons.Icons")
        sb.appendLine("import androidx.compose.material.icons.filled.*")
        sb.appendLine("import androidx.compose.material3.*")
        sb.appendLine("import androidx.compose.runtime.*")
        sb.appendLine("import androidx.compose.ui.Alignment")
        sb.appendLine("import androidx.compose.ui.Modifier")
        sb.appendLine("import androidx.compose.ui.draw.clip")
        sb.appendLine("import androidx.compose.ui.draw.shadow")
        sb.appendLine("import androidx.compose.ui.geometry.Offset")
        sb.appendLine("import androidx.compose.ui.graphics.Brush")
        sb.appendLine("import androidx.compose.ui.graphics.Color")
        sb.appendLine("import androidx.compose.ui.text.font.FontWeight")
        sb.appendLine("import androidx.compose.ui.text.style.TextAlign")
        sb.appendLine("import androidx.compose.ui.tooling.preview.Preview")
        sb.appendLine("import androidx.compose.ui.unit.dp")
        sb.appendLine("import androidx.compose.ui.unit.sp")
        sb.appendLine("import kotlin.math.sin")
        sb.appendLine()
        sb.appendLine("/**")
        sb.appendLine(" * Generated with ComposeCraft - Android Visual UI/UX Designer")
        sb.appendLine(" * Screen: ${screen.name}")
        sb.appendLine(" */")
        sb.appendLine("@Composable")
        sb.appendLine("fun $functionName(")
        sb.appendLine("    modifier: Modifier = Modifier,")
        sb.appendLine("    onNavigate: (String) -> Unit = {},")
        sb.appendLine("    onShowToast: (String) -> Unit = {}")
        sb.appendLine(") {")

        // Screen Container with Background
        val bgColor = if (screen.backgroundColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${screen.backgroundColorHex}\"))" else "MaterialTheme.colorScheme.background"
        sb.appendLine("    Surface(")
        sb.appendLine("        modifier = modifier.fillMaxSize(),")
        sb.appendLine("        color = $bgColor")
        sb.appendLine("    ) {")

        // Render Tree Root
        sb.append(generateNodeCode(screen.rootNode, indentLevel = 2))

        sb.appendLine("    }")
        sb.appendLine("}")
        sb.appendLine()

        // Include 3D Glowing Orb helper if used
        if (hasOrb) {
            sb.appendLine(generateGlowingOrbHelperCode())
        }

        // Include Waveform helper if used
        if (hasWaveform) {
            sb.appendLine(generateWaveformHelperCode())
        }

        // Preview
        sb.appendLine("@Preview(showBackground = true, device = \"id:pixel_8\")")
        sb.appendLine("@Composable")
        sb.appendLine("fun ${functionName}Preview() {")
        sb.appendLine("    MaterialTheme {")
        sb.appendLine("        $functionName()")
        sb.appendLine("    }")
        sb.appendLine("}")

        return sb.toString()
    }

    private fun generateNodeCode(node: UIElementNode, indentLevel: Int): String {
        val indent = "    ".repeat(indentLevel)
        val props = node.properties
        val sb = StringBuilder()

        val modifierChain = buildModifierChain(props)

        when (node.type) {
            UIElementType.TEXT -> {
                val weight = if (props.fontWeight.equals("bold", true)) "FontWeight.Bold" else "FontWeight.Normal"
                val align = if (props.textAlign.equals("center", true)) "TextAlign.Center" else "TextAlign.Start"
                val textColor = if (props.textColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.textColorHex}\"))" else "Color.Unspecified"
                sb.appendLine("${indent}Text(")
                sb.appendLine("${indent}    text = \"${props.text}\",")
                sb.appendLine("${indent}    fontSize = ${props.fontSize}.sp,")
                sb.appendLine("${indent}    fontWeight = $weight,")
                sb.appendLine("${indent}    textAlign = $align,")
                sb.appendLine("${indent}    color = $textColor,")
                sb.appendLine("${indent}    modifier = $modifierChain")
                sb.appendLine("${indent})")
            }

            UIElementType.BUTTON -> {
                val btnColor = if (props.backgroundColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.backgroundColorHex}\"))" else "MaterialTheme.colorScheme.primary"
                val txtColor = if (props.textColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.textColorHex}\"))" else "Color.White"
                val action = when (props.actionType) {
                    "NAVIGATE" -> "onNavigate(\"${props.actionTarget}\")"
                    "TOAST" -> "onShowToast(\"${props.actionTarget}\")"
                    else -> "/* Handle Button Click */"
                }
                sb.appendLine("${indent}Button(")
                sb.appendLine("${indent}    onClick = { $action },")
                sb.appendLine("${indent}    colors = ButtonDefaults.buttonColors(containerColor = $btnColor, contentColor = $txtColor),")
                sb.appendLine("${indent}    shape = RoundedCornerShape(${props.cornerRadius}.dp),")
                sb.appendLine("${indent}    modifier = $modifierChain")
                sb.appendLine("${indent}) {")
                sb.appendLine("${indent}    Text(text = \"${props.text}\", fontSize = ${props.fontSize}.sp, fontWeight = FontWeight.Bold)")
                sb.appendLine("${indent}}")
            }

            UIElementType.OUTLINED_BUTTON -> {
                val strokeColor = if (props.borderColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.borderColorHex}\"))" else "MaterialTheme.colorScheme.primary"
                val txtColor = if (props.textColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.textColorHex}\"))" else "MaterialTheme.colorScheme.primary"
                val action = when (props.actionType) {
                    "NAVIGATE" -> "onNavigate(\"${props.actionTarget}\")"
                    "TOAST" -> "onShowToast(\"${props.actionTarget}\")"
                    else -> "/* Handle Outlined Click */"
                }
                sb.appendLine("${indent}OutlinedButton(")
                sb.appendLine("${indent}    onClick = { $action },")
                sb.appendLine("${indent}    border = androidx.compose.foundation.BorderStroke(${props.borderWidth.coerceAtLeast(1)}.dp, $strokeColor),")
                sb.appendLine("${indent}    colors = ButtonDefaults.outlinedButtonColors(contentColor = $txtColor),")
                sb.appendLine("${indent}    shape = RoundedCornerShape(${props.cornerRadius}.dp),")
                sb.appendLine("${indent}    modifier = $modifierChain")
                sb.appendLine("${indent}) {")
                sb.appendLine("${indent}    Text(text = \"${props.text}\", fontSize = ${props.fontSize}.sp)")
                sb.appendLine("${indent}}")
            }

            UIElementType.TEXT_FIELD -> {
                sb.appendLine("${indent}var input_${node.id.take(4)} by remember { mutableStateOf(\"${props.text}\") }")
                sb.appendLine("${indent}OutlinedTextField(")
                sb.appendLine("${indent}    value = input_${node.id.take(4)},")
                sb.appendLine("${indent}    onValueChange = { input_${node.id.take(4)} = it },")
                sb.appendLine("${indent}    placeholder = { Text(\"${props.placeholder}\") },")
                sb.appendLine("${indent}    shape = RoundedCornerShape(${props.cornerRadius}.dp),")
                sb.appendLine("${indent}    modifier = $modifierChain")
                sb.appendLine("${indent})")
            }

            UIElementType.GLOWING_ORB -> {
                val pColor = "Color(android.graphics.Color.parseColor(\"${props.orbColorPrimaryHex}\"))"
                val sColor = "Color(android.graphics.Color.parseColor(\"${props.orbColorSecondaryHex}\"))"
                val gColor = "Color(android.graphics.Color.parseColor(\"${props.orbGlowColorHex}\"))"
                sb.appendLine("${indent}GlowingOrbWidget(")
                sb.appendLine("${indent}    sizeDp = ${props.orbSizeDp}.dp,")
                sb.appendLine("${indent}    primaryColor = $pColor,")
                sb.appendLine("${indent}    secondaryColor = $sColor,")
                sb.appendLine("${indent}    glowColor = $gColor,")
                sb.appendLine("${indent}    modifier = $modifierChain")
                sb.appendLine("${indent})")
            }

            UIElementType.WAVEFORM -> {
                val wColor = "Color(android.graphics.Color.parseColor(\"${props.waveformColorHex}\"))"
                sb.appendLine("${indent}LiveWaveformWidget(")
                sb.appendLine("${indent}    barCount = ${props.waveformBars},")
                sb.appendLine("${indent}    waveColor = $wColor,")
                sb.appendLine("${indent}    modifier = $modifierChain")
                sb.appendLine("${indent})")
            }

            UIElementType.SWITCH -> {
                sb.appendLine("${indent}var checked_${node.id.take(4)} by remember { mutableStateOf(${props.isChecked}) }")
                sb.appendLine("${indent}Row(")
                sb.appendLine("${indent}    modifier = $modifierChain,")
                sb.appendLine("${indent}    horizontalArrangement = Arrangement.SpaceBetween,")
                sb.appendLine("${indent}    verticalAlignment = Alignment.CenterVertically")
                sb.appendLine("${indent}) {")
                sb.appendLine("${indent}    Text(\"${props.text}\", fontSize = ${props.fontSize}.sp)")
                sb.appendLine("${indent}    Switch(checked = checked_${node.id.take(4)}, onCheckedChange = { checked_${node.id.take(4)} = it })")
                sb.appendLine("${indent}}")
            }

            UIElementType.CHECKBOX -> {
                sb.appendLine("${indent}var isChecked_${node.id.take(4)} by remember { mutableStateOf(${props.isChecked}) }")
                sb.appendLine("${indent}Row(")
                sb.appendLine("${indent}    modifier = $modifierChain,")
                sb.appendLine("${indent}    verticalAlignment = Alignment.CenterVertically")
                sb.appendLine("${indent}) {")
                sb.appendLine("${indent}    Checkbox(checked = isChecked_${node.id.take(4)}, onCheckedChange = { isChecked_${node.id.take(4)} = it })")
                sb.appendLine("${indent}    Spacer(modifier = Modifier.width(8.dp))")
                sb.appendLine("${indent}    Text(\"${props.text}\", fontSize = ${props.fontSize}.sp)")
                sb.appendLine("${indent}}")
            }

            UIElementType.SLIDER -> {
                sb.appendLine("${indent}var sliderVal_${node.id.take(4)} by remember { mutableStateOf(${props.sliderValue}f) }")
                sb.appendLine("${indent}Slider(")
                sb.appendLine("${indent}    value = sliderVal_${node.id.take(4)},")
                sb.appendLine("${indent}    onValueChange = { sliderVal_${node.id.take(4)} = it },")
                sb.appendLine("${indent}    valueRange = 0f..100f,")
                sb.appendLine("${indent}    modifier = $modifierChain")
                sb.appendLine("${indent})")
            }

            UIElementType.IMAGE -> {
                sb.appendLine("${indent}Box(")
                sb.appendLine("${indent}    modifier = $modifierChain,")
                sb.appendLine("${indent}    contentAlignment = Alignment.Center")
                sb.appendLine("${indent}) {")
                sb.appendLine("${indent}    Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))")
                sb.appendLine("${indent}}")
            }

            UIElementType.ICON -> {
                val iconColor = if (props.textColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.textColorHex}\"))" else "MaterialTheme.colorScheme.primary"
                sb.appendLine("${indent}Icon(")
                sb.appendLine("${indent}    imageVector = Icons.Default.${props.iconName},")
                sb.appendLine("${indent}    contentDescription = null,")
                sb.appendLine("${indent}    tint = $iconColor,")
                sb.appendLine("${indent}    modifier = $modifierChain")
                sb.appendLine("${indent})")
            }

            UIElementType.SPACER -> {
                sb.appendLine("${indent}Spacer(modifier = Modifier.height(${props.fixedHeightDp}.dp))")
            }

            UIElementType.DIVIDER -> {
                val divColor = if (props.borderColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.borderColorHex}\"))" else "Color.LightGray"
                sb.appendLine("${indent}HorizontalDivider(thickness = ${props.borderWidth.coerceAtLeast(1)}.dp, color = $divColor, modifier = $modifierChain)")
            }

            UIElementType.TOP_APP_BAR -> {
                val barBg = if (props.backgroundColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.backgroundColorHex}\"))" else "MaterialTheme.colorScheme.primary"
                val barTxt = if (props.textColorHex.isNotBlank()) "Color(android.graphics.Color.parseColor(\"${props.textColorHex}\"))" else "Color.White"
                sb.appendLine("${indent}Row(")
                sb.appendLine("${indent}    modifier = $modifierChain.background($barBg).padding(horizontal = 16.dp),")
                sb.appendLine("${indent}    verticalAlignment = Alignment.CenterVertically")
                sb.appendLine("${indent}) {")
                sb.appendLine("${indent}    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = $barTxt)")
                sb.appendLine("${indent}    Spacer(modifier = Modifier.width(16.dp))")
                sb.appendLine("${indent}    Text(\"${props.text}\", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = $barTxt)")
                sb.appendLine("${indent}}")
            }

            UIElementType.COLUMN, UIElementType.PULSE_CONTAINER, UIElementType.CARD -> {
                val arrange = if (props.arrangement.equals("spaceBetween", true)) "Arrangement.SpaceBetween" else "Arrangement.spacedBy(${props.spacingDp}.dp)"
                val align = if (props.alignment.equals("start", true)) "Alignment.Start" else "Alignment.CenterHorizontally"
                sb.appendLine("${indent}Column(")
                sb.appendLine("${indent}    modifier = $modifierChain,")
                sb.appendLine("${indent}    verticalArrangement = $arrange,")
                sb.appendLine("${indent}    horizontalAlignment = $align")
                sb.appendLine("${indent}) {")
                for (child in node.children) {
                    sb.append(generateNodeCode(child, indentLevel + 1))
                }
                sb.appendLine("${indent}}")
            }

            UIElementType.ROW -> {
                val arrange = if (props.arrangement.equals("spaceBetween", true)) "Arrangement.SpaceBetween" else "Arrangement.spacedBy(${props.spacingDp}.dp)"
                sb.appendLine("${indent}Row(")
                sb.appendLine("${indent}    modifier = $modifierChain,")
                sb.appendLine("${indent}    horizontalArrangement = $arrange,")
                sb.appendLine("${indent}    verticalAlignment = Alignment.CenterVertically")
                sb.appendLine("${indent}) {")
                for (child in node.children) {
                    sb.append(generateNodeCode(child, indentLevel + 1))
                }
                sb.appendLine("${indent}}")
            }

            UIElementType.BOX -> {
                sb.appendLine("${indent}Box(modifier = $modifierChain) {")
                for (child in node.children) {
                    sb.append(generateNodeCode(child, indentLevel + 1))
                }
                sb.appendLine("${indent}}")
            }

            else -> {
                sb.appendLine("${indent}/* Element: ${node.name} */")
            }
        }
        return sb.toString()
    }

    private fun buildModifierChain(props: ElementProperties): String {
        val parts = mutableListOf<String>()
        parts.add("Modifier")

        when (props.widthMode) {
            "FILL" -> parts.add("fillMaxWidth()")
            "FIXED" -> parts.add("width(${props.fixedWidthDp}.dp)")
            else -> parts.add("wrapContentWidth()")
        }

        when (props.heightMode) {
            "FILL" -> parts.add("fillMaxHeight()")
            "FIXED" -> parts.add("height(${props.fixedHeightDp}.dp)")
            else -> parts.add("wrapContentHeight()")
        }

        if (props.elevation > 0) {
            parts.add("shadow(${props.elevation}.dp, RoundedCornerShape(${props.cornerRadius}.dp))")
        }

        if (props.cornerRadius > 0) {
            parts.add("clip(RoundedCornerShape(${props.cornerRadius}.dp))")
        }

        if (props.backgroundColorHex.isNotBlank()) {
            parts.add("background(Color(android.graphics.Color.parseColor(\"${props.backgroundColorHex}\")))")
        }

        if (props.borderWidth > 0 && props.borderColorHex.isNotBlank()) {
            parts.add("border(${props.borderWidth}.dp, Color(android.graphics.Color.parseColor(\"${props.borderColorHex}\")), RoundedCornerShape(${props.cornerRadius}.dp))")
        }

        if (props.paddingHorizontal > 0 || props.paddingVertical > 0) {
            parts.add("padding(horizontal = ${props.paddingHorizontal}.dp, vertical = ${props.paddingVertical}.dp)")
        }

        return parts.joinToString(".")
    }

    private fun UIElementNode.findNodeByType(targetType: UIElementType): UIElementNode? {
        if (type == targetType) return this
        for (c in children) {
            val f = c.findNodeByType(targetType)
            if (f != null) return f
        }
        return null
    }

    private fun generateGlowingOrbHelperCode(): String {
        return """
// --- 3D Glowing AI Orb Component ---
@Composable
fun GlowingOrbWidget(
    sizeDp: androidx.compose.ui.unit.Dp,
    primaryColor: Color,
    secondaryColor: Color,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbAnim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "Pulse"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "Rotate"
    )

    Box(modifier = modifier.size(sizeDp).padding(8.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension / 2f) * 0.72f * pulseScale

            // Outer Glow
            drawCircle(
                brush = Brush.radialGradient(listOf(glowColor.copy(alpha = 0.6f), Color.Transparent), center = center, radius = radius * 1.5f),
                radius = radius * 1.5f, center = center
            )
            // 3D Sphere Body
            val rad = Math.toRadians(rotation.toDouble())
            val light = Offset(center.x + (radius * 0.35f * kotlin.math.cos(rad)).toFloat(), center.y + (radius * 0.35f * kotlin.math.sin(rad)).toFloat())
            drawCircle(
                brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.95f), primaryColor, secondaryColor, Color(0xFF0F172A)), center = light, radius = radius * 1.1f),
                radius = radius, center = center
            )
            // Specular Reflection
            val specCenter = Offset(center.x - radius * 0.32f, center.y - radius * 0.32f)
            drawCircle(
                brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.85f), Color.Transparent), center = specCenter, radius = radius * 0.45f),
                radius = radius * 0.45f, center = specCenter
            )
        }
    }
}
""".trimIndent()
    }

    private fun generateWaveformHelperCode(): String {
        return """
// --- Live Audio Waveform Component ---
@Composable
fun LiveWaveformWidget(
    barCount: Int = 18,
    waveColor: Color = Color(0xFF00F2FE),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "Phase"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(40.dp).padding(horizontal = 12.dp)) {
        val barWidth = (size.width / (barCount * 2f)).coerceAtLeast(3f)
        val spacing = (size.width - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

        for (i in 0 until barCount) {
            val norm = i.toFloat() / barCount
            val amp = 0.25f + ((sin(phase + norm * 4f) + 1f) / 2f) * 0.7f
            val h = size.height * amp
            val left = i * (barWidth + spacing)
            val top = (size.height - h) / 2f
            drawRoundRect(
                color = waveColor.copy(alpha = 0.5f + amp * 0.5f),
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(barWidth, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
""".trimIndent()
    }

    /**
     * Generates a complete prompt file (PROMPT_FOR_AI.md) that any AI agent
     * (ChatGPT, Claude, Gemini, Copilot, Cursor) can immediately consume and build with zero friction.
     */
    fun generateAIPrompt(project: AppProject): String {
        val sb = StringBuilder()
        sb.appendLine("# 🚀 AI Agent Build Instructions for ${project.name}")
        sb.appendLine()
        sb.appendLine("You are an expert Android Kotlin & Jetpack Compose engineer.")
        sb.appendLine("Here is a complete, validated Android application designed visually using **ComposeCraft**.")
        sb.appendLine()
        sb.appendLine("## 📦 Project Specifications:")
        sb.appendLine("- **Project Name**: `${project.name}`")
        sb.appendLine("- **Package Name**: `${project.packageName}`")
        sb.appendLine("- **UI Framework**: Modern Jetpack Compose (Material3, Kotlin 2.0+)")
        sb.appendLine("- **Number of Screens**: ${project.screens.size}")
        sb.appendLine()
        sb.appendLine("## 🛠️ Instructions for AI Agent:")
        sb.appendLine("1. Create the Jetpack Compose project using standard Gradle dependencies (Compose BOM, Material3, Navigation Compose).")
        sb.appendLine("2. Add each of the generated screen composables below under `ui/screens/`.")
        sb.appendLine("3. Connect the screens in `MainActivity.kt` with a `NavHost`.")
        sb.appendLine("4. All interactive animations (3D Glowing Orb, Waveform) are already included and self-contained.")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()
        for (screen in project.screens) {
            sb.appendLine("### 📱 Screen: ${screen.name}")
            sb.appendLine("```kotlin")
            sb.appendLine(generateScreenCode(screen, project.packageName))
            sb.appendLine("```")
            sb.appendLine()
        }
        return sb.toString()
    }
}
