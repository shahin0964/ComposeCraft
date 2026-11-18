package com.composecraft.app.model

data class ElementProperties(
    // Typography & Content
    val text: String = "Sample Text",
    val placeholder: String = "Type here...",
    val fontSize: Int = 16,
    val fontWeight: String = "Normal", // "Light", "Normal", "Medium", "Bold"
    val textAlign: String = "Start",    // "Start", "Center", "End"
    val textColorHex: String = "#1E293B",

    // Appearance & Styling
    val backgroundColorHex: String = "", // empty means transparent/default
    val cornerRadius: Int = 12,
    val elevation: Int = 0,
    val borderWidth: Int = 0,
    val borderColorHex: String = "#E2E8F0",

    // Sizing & Spacing
    val widthMode: String = "FILL",  // "WRAP", "FILL", "FIXED"
    val heightMode: String = "WRAP", // "WRAP", "FILL", "FIXED"
    val fixedWidthDp: Int = 150,
    val fixedHeightDp: Int = 50,
    val paddingHorizontal: Int = 16,
    val paddingVertical: Int = 8,

    // Container specific
    val arrangement: String = "Top",   // "Top", "Center", "Bottom", "SpaceBetween", "SpaceAround"
    val alignment: String = "CenterHorizontally", // "Start", "CenterHorizontally", "End"
    val spacingDp: Int = 8,

    // Interactive & Controls
    val isChecked: Boolean = true,
    val sliderValue: Float = 50f,
    val iconName: String = "Star",

    // Actions
    val actionType: String = "NONE", // "NONE", "NAVIGATE", "TOAST", "OPEN_URL"
    val actionTarget: String = "",

    // 3D & Live Animation Properties ✨
    val orbColorPrimaryHex: String = "#00F2FE",   // Neon Cyan
    val orbColorSecondaryHex: String = "#4FACFE", // Vibrant Electric Blue
    val orbGlowColorHex: String = "#38EF7D",      // Emerald Aura
    val orbSizeDp: Int = 140,
    val animationSpeedMs: Int = 2200,
    val waveformBars: Int = 16,
    val waveformColorHex: String = "#00F2FE",

    // Free-form Drag & Drop Canvas Coordinates (dp)
    val posX: Float = 0f,
    val posY: Float = 0f
)
