package com.composecraft.app.model

enum class UIElementType(
    val displayName: String,
    val category: WidgetCategory,
    val iconName: String,
    val isContainer: Boolean
) {
    // 3D & Live Animated Elements ✨
    GLOWING_ORB("3D Glowing AI Orb", WidgetCategory.ANIMATIONS, "Animation", false),
    WAVEFORM("Live Audio Waveform", WidgetCategory.ANIMATIONS, "GraphicEq", false),
    SHIMMER_BOX("Live Shimmer Skeleton", WidgetCategory.ANIMATIONS, "AutoAwesome", false),
    ROTATING_BORDER_CARD("Animated Border Card", WidgetCategory.ANIMATIONS, "CropSquare", true),
    PULSE_CONTAINER("Pulsing Glow Container", WidgetCategory.ANIMATIONS, "BlurOn", true),

    // Advanced & Diverse Navigation Styles 🧭
    FLOATING_NAV_BAR("Floating Island Dock", WidgetCategory.NAVIGATION, "Dock", false),
    SEGMENTED_TABS("Segmented Pill Tabs", WidgetCategory.NAVIGATION, "ViewColumn", false),
    SIDE_DRAWER_PREVIEW("Slide-out Drawer Menu", WidgetCategory.NAVIGATION, "MenuOpen", false),
    TOP_APP_BAR("Top App Bar", WidgetCategory.NAVIGATION, "WebAsset", false),
    BOTTOM_NAV_BAR("Bottom Navigation Bar", WidgetCategory.NAVIGATION, "CallToAction", false),
    FLOATING_ACTION_BUTTON("FAB (Floating Action)", WidgetCategory.NAVIGATION, "AddCircle", false),

    // Interactive Sliders & Action Bars ⚡
    SLIDE_TO_ACTION("Slide Bar (Swipe Action)", WidgetCategory.CONTROLS, "Swipe", false),
    SWITCH("Switch Toggle", WidgetCategory.CONTROLS, "ToggleOn", false),
    CHECKBOX("Checkbox", WidgetCategory.CONTROLS, "CheckBox", false),
    SLIDER("Slider Bar", WidgetCategory.CONTROLS, "LinearScale", false),

    // Specialized Buttons & Cards 🎨
    GRADIENT_BUTTON("Gradient Glow Button", WidgetCategory.BASIC, "SmartButton", false),
    BUTTON("Filled Button", WidgetCategory.BASIC, "SmartButton", false),
    OUTLINED_BUTTON("Outlined Button", WidgetCategory.BASIC, "CheckBoxOutlineBlank", false),
    TEXT("Text / Label", WidgetCategory.BASIC, "Title", false),
    TEXT_FIELD("Text Input Field", WidgetCategory.BASIC, "Edit", false),
    IMAGE("Image / Banner", WidgetCategory.BASIC, "Image", false),
    ICON("Material Icon", WidgetCategory.BASIC, "Star", false),

    // Containers & Layouts
    COLUMN("Column (Vertical)", WidgetCategory.CONTAINER, "ViewColumn", true),
    ROW("Row (Horizontal)", WidgetCategory.CONTAINER, "ViewStream", true),
    BOX("Box (Stack)", WidgetCategory.CONTAINER, "Layers", true),
    CARD("Card (Surfaced)", WidgetCategory.CONTAINER, "CropOriginal", true),

    // Layout Helpers
    SPACER("Spacer", WidgetCategory.HELPERS, "SpaceBar", false),
    DIVIDER("Divider Line", WidgetCategory.HELPERS, "HorizontalRule", false)
}

enum class WidgetCategory(val title: String) {
    ANIMATIONS("3D & Live Animations ✨"),
    NAVIGATION("Modern Navigation Styles 🧭"),
    CONTROLS("Sliders & Swipe Bars ⚡"),
    BASIC("Buttons & Basic Elements 🎨"),
    CONTAINER("Containers & Layouts"),
    HELPERS("Spacing & Dividers")
}
