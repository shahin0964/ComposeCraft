package com.composecraft.app.model

enum class UIElementType(
    val displayName: String,
    val category: WidgetCategory,
    val iconName: String,
    val isContainer: Boolean
) {
    // 3D & Live Animated Elements (Special AI & Interactive)
    GLOWING_ORB("3D Glowing AI Orb", WidgetCategory.ANIMATIONS, "Animation", false),
    WAVEFORM("Live Audio Waveform", WidgetCategory.ANIMATIONS, "GraphicEq", false),
    PULSE_CONTAINER("Pulsing Glow Container", WidgetCategory.ANIMATIONS, "BlurOn", true),

    // Containers
    COLUMN("Column (Vertical)", WidgetCategory.CONTAINER, "ViewColumn", true),
    ROW("Row (Horizontal)", WidgetCategory.CONTAINER, "ViewStream", true),
    BOX("Box (Stack)", WidgetCategory.CONTAINER, "Layers", true),
    CARD("Card (Surfaced)", WidgetCategory.CONTAINER, "CropOriginal", true),

    // Basic UI Elements
    TEXT("Text / Label", WidgetCategory.BASIC, "Title", false),
    BUTTON("Filled Button", WidgetCategory.BASIC, "SmartButton", false),
    OUTLINED_BUTTON("Outlined Button", WidgetCategory.BASIC, "CheckBoxOutlineBlank", false),
    TEXT_FIELD("Text Input Field", WidgetCategory.BASIC, "Edit", false),
    IMAGE("Image / Banner", WidgetCategory.BASIC, "Image", false),
    ICON("Material Icon", WidgetCategory.BASIC, "Star", false),

    // Controls
    SWITCH("Switch Toggle", WidgetCategory.CONTROLS, "ToggleOn", false),
    CHECKBOX("Checkbox", WidgetCategory.CONTROLS, "CheckBox", false),
    SLIDER("Slider", WidgetCategory.CONTROLS, "LinearScale", false),

    // Layout Helpers
    SPACER("Spacer", WidgetCategory.HELPERS, "SpaceBar", false),
    DIVIDER("Divider Line", WidgetCategory.HELPERS, "HorizontalRule", false),

    // Navigation / Shell
    TOP_APP_BAR("Top App Bar", WidgetCategory.SHELL, "WebAsset", false),
    BOTTOM_NAV_BAR("Bottom Navigation", WidgetCategory.SHELL, "CallToAction", false),
    FLOATING_ACTION_BUTTON("FAB (Floating Action)", WidgetCategory.SHELL, "AddCircle", false)
}

enum class WidgetCategory(val title: String) {
    ANIMATIONS("3D & Live Animations ✨"),
    CONTAINER("Containers & Layouts"),
    BASIC("Basic Elements"),
    CONTROLS("Input & Controls"),
    HELPERS("Spacing & Dividers"),
    SHELL("App Shell & Nav")
}
