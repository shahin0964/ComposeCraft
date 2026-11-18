package com.composecraft.app.model

object ProjectTemplates {

    fun createAIAssistantTemplate(): AppProject {
        val root = UIElementNode(
            id = "ai_root",
            type = UIElementType.COLUMN,
            name = "AI Voice Screen",
            properties = ElementProperties(
                widthMode = "FILL",
                heightMode = "FILL",
                paddingHorizontal = 20,
                paddingVertical = 24,
                spacingDp = 18,
                backgroundColorHex = "#090A0F",
                alignment = "CenterHorizontally"
            ),
            children = listOf(
                UIElementNode(
                    type = UIElementType.TOP_APP_BAR,
                    name = "Header Bar",
                    properties = ElementProperties(
                        text = "Aura 3D AI Assistant",
                        backgroundColorHex = "#12141F",
                        textColorHex = "#00F2FE",
                        iconName = "Star"
                    )
                ),
                UIElementNode(
                    type = UIElementType.SPACER,
                    name = "Spacer Top",
                    properties = ElementProperties(fixedHeightDp = 20, heightMode = "FIXED")
                ),
                // 3D Glowing Orb at center!
                UIElementNode(
                    type = UIElementType.GLOWING_ORB,
                    name = "Central 3D AI Orb",
                    properties = ElementProperties(
                        orbSizeDp = 160,
                        orbColorPrimaryHex = "#00F2FE",
                        orbColorSecondaryHex = "#7F00FF",
                        orbGlowColorHex = "#4FACFE",
                        animationSpeedMs = 2000
                    )
                ),
                // Realtime audio waveform
                UIElementNode(
                    type = UIElementType.WAVEFORM,
                    name = "Live Waveform",
                    properties = ElementProperties(
                        waveformBars = 22,
                        waveformColorHex = "#00F2FE",
                        fixedHeightDp = 40,
                        heightMode = "FIXED",
                        widthMode = "FILL"
                    )
                ),
                UIElementNode(
                    type = UIElementType.CARD,
                    name = "AI Response Card",
                    properties = ElementProperties(
                        backgroundColorHex = "#12141F",
                        cornerRadius = 20,
                        elevation = 6,
                        borderWidth = 1,
                        borderColorHex = "#1E2238",
                        paddingHorizontal = 20,
                        paddingVertical = 18,
                        widthMode = "FILL"
                    ),
                    children = listOf(
                        UIElementNode(
                            type = UIElementType.TEXT,
                            name = "Agent Status",
                            properties = ElementProperties(
                                text = "AI Assistant • Listening",
                                fontSize = 12,
                                fontWeight = "Bold",
                                textColorHex = "#00F2FE"
                            )
                        ),
                        UIElementNode(
                            type = UIElementType.TEXT,
                            name = "Transcript Text",
                            properties = ElementProperties(
                                text = "\"Hello! I am ready to help you design, code, and build your Android dream app.\"",
                                fontSize = 16,
                                fontWeight = "Normal",
                                textColorHex = "#F1F5F9"
                            )
                        )
                    )
                ),
                UIElementNode(
                    type = UIElementType.BUTTON,
                    name = "Tap to Speak Button",
                    properties = ElementProperties(
                        text = "🎙️ Tap to Speak",
                        backgroundColorHex = "#4F46E5",
                        textColorHex = "#FFFFFF",
                        cornerRadius = 28,
                        paddingVertical = 16,
                        fontSize = 16,
                        fontWeight = "Bold",
                        widthMode = "FILL",
                        actionType = "TOAST",
                        actionTarget = "Voice Listening Activated!"
                    )
                )
            )
        )

        return AppProject(
            name = "Futuristic AI Assistant",
            screens = listOf(AppScreen(name = "AI Voice Screen", backgroundColorHex = "#090A0F", rootNode = root))
        )
    }

    fun createLoginTemplate(): AppProject {
        val root = UIElementNode(
            id = "login_root",
            type = UIElementType.COLUMN,
            name = "Login Container",
            properties = ElementProperties(
                widthMode = "FILL",
                heightMode = "FILL",
                paddingHorizontal = 24,
                paddingVertical = 36,
                spacingDp = 16,
                backgroundColorHex = "#FFFFFF",
                alignment = "CenterHorizontally"
            ),
            children = listOf(
                UIElementNode(
                    type = UIElementType.ICON,
                    name = "Logo Icon",
                    properties = ElementProperties(
                        iconName = "Lock",
                        textColorHex = "#4F46E5",
                        fixedWidthDp = 48,
                        fixedHeightDp = 48,
                        widthMode = "WRAP"
                    )
                ),
                UIElementNode(
                    type = UIElementType.TEXT,
                    name = "Header Title",
                    properties = ElementProperties(
                        text = "Welcome Back 👋",
                        fontSize = 26,
                        fontWeight = "Bold",
                        textColorHex = "#0F172A",
                        textAlign = "Center",
                        widthMode = "FILL"
                    )
                ),
                UIElementNode(
                    type = UIElementType.TEXT,
                    name = "Subtitle",
                    properties = ElementProperties(
                        text = "Sign in to your account to continue",
                        fontSize = 14,
                        fontWeight = "Normal",
                        textColorHex = "#64748B",
                        textAlign = "Center",
                        widthMode = "FILL"
                    )
                ),
                UIElementNode(
                    type = UIElementType.SPACER,
                    name = "Spacer Top",
                    properties = ElementProperties(fixedHeightDp = 12, heightMode = "FIXED")
                ),
                UIElementNode(
                    type = UIElementType.TEXT_FIELD,
                    name = "Email Input",
                    properties = ElementProperties(
                        placeholder = "Email Address",
                        widthMode = "FILL",
                        cornerRadius = 14,
                        borderWidth = 1,
                        borderColorHex = "#E2E8F0",
                        backgroundColorHex = "#F8FAFC"
                    )
                ),
                UIElementNode(
                    type = UIElementType.TEXT_FIELD,
                    name = "Password Input",
                    properties = ElementProperties(
                        placeholder = "Password (min 8 chars)",
                        widthMode = "FILL",
                        cornerRadius = 14,
                        borderWidth = 1,
                        borderColorHex = "#E2E8F0",
                        backgroundColorHex = "#F8FAFC"
                    )
                ),
                UIElementNode(
                    type = UIElementType.BUTTON,
                    name = "Sign In Button",
                    properties = ElementProperties(
                        text = "Sign In",
                        backgroundColorHex = "#4F46E5",
                        textColorHex = "#FFFFFF",
                        cornerRadius = 14,
                        paddingVertical = 14,
                        fontSize = 16,
                        fontWeight = "Bold",
                        widthMode = "FILL",
                        actionType = "TOAST",
                        actionTarget = "Signing in successfully!"
                    )
                ),
                UIElementNode(
                    type = UIElementType.DIVIDER,
                    name = "Divider Line",
                    properties = ElementProperties(borderWidth = 1, borderColorHex = "#F1F5F9", widthMode = "FILL")
                ),
                UIElementNode(
                    type = UIElementType.OUTLINED_BUTTON,
                    name = "Create Account Button",
                    properties = ElementProperties(
                        text = "Don't have an account? Sign Up",
                        borderWidth = 1,
                        borderColorHex = "#CBD5E1",
                        textColorHex = "#475569",
                        cornerRadius = 14,
                        paddingVertical = 12,
                        widthMode = "FILL"
                    )
                )
            )
        )

        val screen = AppScreen(
            name = "Login Screen",
            backgroundColorHex = "#FFFFFF",
            rootNode = root
        )

        return AppProject(
            name = "Login Flow Project",
            screens = listOf(screen)
        )
    }

    fun createProfileTemplate(): AppProject {
        val root = UIElementNode(
            id = "profile_root",
            type = UIElementType.COLUMN,
            name = "Profile Screen",
            properties = ElementProperties(
                widthMode = "FILL",
                heightMode = "FILL",
                paddingHorizontal = 20,
                paddingVertical = 20,
                spacingDp = 14,
                backgroundColorHex = "#F8FAFC",
                alignment = "CenterHorizontally"
            ),
            children = listOf(
                UIElementNode(
                    type = UIElementType.TOP_APP_BAR,
                    name = "Top Bar",
                    properties = ElementProperties(
                        text = "My Profile",
                        backgroundColorHex = "#0F172A",
                        textColorHex = "#FFFFFF",
                        iconName = "ArrowBack"
                    )
                ),
                UIElementNode(
                    type = UIElementType.CARD,
                    name = "Profile Header Card",
                    properties = ElementProperties(
                        backgroundColorHex = "#FFFFFF",
                        cornerRadius = 20,
                        elevation = 2,
                        paddingHorizontal = 20,
                        paddingVertical = 20,
                        widthMode = "FILL",
                        borderWidth = 1,
                        borderColorHex = "#E2E8F0"
                    ),
                    children = listOf(
                        UIElementNode(
                            type = UIElementType.ICON,
                            name = "Avatar Icon",
                            properties = ElementProperties(
                                iconName = "Person",
                                textColorHex = "#4F46E5",
                                fixedWidthDp = 56,
                                fixedHeightDp = 56,
                                widthMode = "WRAP"
                            )
                        ),
                        UIElementNode(
                            type = UIElementType.TEXT,
                            name = "User Name",
                            properties = ElementProperties(
                                text = "Alex Morgan",
                                fontSize = 20,
                                fontWeight = "Bold",
                                textColorHex = "#0F172A",
                                widthMode = "FILL",
                                textAlign = "Center"
                            )
                        ),
                        UIElementNode(
                            type = UIElementType.TEXT,
                            name = "User Email",
                            properties = ElementProperties(
                                text = "alex.morgan@composecraft.dev",
                                fontSize = 13,
                                textColorHex = "#64748B",
                                widthMode = "FILL",
                                textAlign = "Center"
                            )
                        )
                    )
                ),
                UIElementNode(
                    type = UIElementType.CARD,
                    name = "Settings Card",
                    properties = ElementProperties(
                        backgroundColorHex = "#FFFFFF",
                        cornerRadius = 16,
                        elevation = 1,
                        paddingHorizontal = 16,
                        paddingVertical = 12,
                        widthMode = "FILL",
                        borderWidth = 1,
                        borderColorHex = "#E2E8F0"
                    ),
                    children = listOf(
                        UIElementNode(
                            type = UIElementType.SWITCH,
                            name = "Dark Mode Toggle",
                            properties = ElementProperties(
                                text = "Dark Mode",
                                isChecked = false,
                                widthMode = "FILL"
                            )
                        ),
                        UIElementNode(
                            type = UIElementType.DIVIDER,
                            name = "Divider",
                            properties = ElementProperties(fixedHeightDp = 1, borderColorHex = "#F1F5F9")
                        ),
                        UIElementNode(
                            type = UIElementType.SWITCH,
                            name = "Push Notifications",
                            properties = ElementProperties(
                                text = "Push Notifications",
                                isChecked = true,
                                widthMode = "FILL"
                            )
                        )
                    )
                ),
                UIElementNode(
                    type = UIElementType.BUTTON,
                    name = "Edit Profile Button",
                    properties = ElementProperties(
                        text = "Edit Profile Settings",
                        backgroundColorHex = "#0F172A",
                        textColorHex = "#FFFFFF",
                        cornerRadius = 14,
                        paddingVertical = 12,
                        widthMode = "FILL"
                    )
                )
            )
        )

        return AppProject(
            name = "Profile Template",
            screens = listOf(AppScreen(name = "Profile Screen", rootNode = root))
        )
    }

    fun createEcommerceTemplate(): AppProject {
        val root = UIElementNode(
            id = "store_root",
            type = UIElementType.COLUMN,
            name = "Store Feed",
            properties = ElementProperties(
                widthMode = "FILL",
                heightMode = "FILL",
                paddingHorizontal = 16,
                paddingVertical = 16,
                spacingDp = 14,
                backgroundColorHex = "#F1F5F9"
            ),
            children = listOf(
                UIElementNode(
                    type = UIElementType.TOP_APP_BAR,
                    name = "Store Bar",
                    properties = ElementProperties(
                        text = "Fashion Store",
                        backgroundColorHex = "#4F46E5",
                        textColorHex = "#FFFFFF",
                        iconName = "ShoppingBag"
                    )
                ),
                UIElementNode(
                    type = UIElementType.TEXT_FIELD,
                    name = "Search Input",
                    properties = ElementProperties(
                        placeholder = "Search products, brands...",
                        widthMode = "FILL",
                        cornerRadius = 24,
                        backgroundColorHex = "#FFFFFF",
                        borderWidth = 1,
                        borderColorHex = "#E2E8F0"
                    )
                ),
                UIElementNode(
                    type = UIElementType.CARD,
                    name = "Featured Item Card",
                    properties = ElementProperties(
                        backgroundColorHex = "#FFFFFF",
                        cornerRadius = 18,
                        elevation = 3,
                        paddingHorizontal = 16,
                        paddingVertical = 16,
                        widthMode = "FILL"
                    ),
                    children = listOf(
                        UIElementNode(
                            type = UIElementType.IMAGE,
                            name = "Product Image Banner",
                            properties = ElementProperties(
                                heightMode = "FIXED",
                                fixedHeightDp = 140,
                                backgroundColorHex = "#E0E7FF",
                                cornerRadius = 12
                            )
                        ),
                        UIElementNode(
                            type = UIElementType.TEXT,
                            name = "Product Title",
                            properties = ElementProperties(
                                text = "Wireless Noise Cancelling Headphones",
                                fontSize = 16,
                                fontWeight = "Bold",
                                textColorHex = "#0F172A",
                                widthMode = "FILL"
                            )
                        ),
                        UIElementNode(
                            type = UIElementType.ROW,
                            name = "Price Row",
                            properties = ElementProperties(
                                widthMode = "FILL",
                                arrangement = "SpaceBetween"
                            ),
                            children = listOf(
                                UIElementNode(
                                    type = UIElementType.TEXT,
                                    name = "Price Text",
                                    properties = ElementProperties(
                                        text = "$149.99",
                                        fontSize = 18,
                                        fontWeight = "Bold",
                                        textColorHex = "#4F46E5",
                                        widthMode = "WRAP"
                                    )
                                ),
                                UIElementNode(
                                    type = UIElementType.BUTTON,
                                    name = "Add to Cart Button",
                                    properties = ElementProperties(
                                        text = "+ Add",
                                        backgroundColorHex = "#4F46E5",
                                        textColorHex = "#FFFFFF",
                                        cornerRadius = 10,
                                        paddingHorizontal = 16,
                                        paddingVertical = 8,
                                        widthMode = "WRAP",
                                        actionType = "TOAST",
                                        actionTarget = "Item added to cart!"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        return AppProject(
            name = "E-Commerce App",
            screens = listOf(AppScreen(name = "Product Screen", rootNode = root))
        )
    }
}
