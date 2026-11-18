package com.composecraft.app.model

import java.util.UUID

data class AppScreen(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Screen 1",
    val backgroundColorHex: String = "#F8FAFC",
    val rootNode: UIElementNode = createDefaultRoot()
) {
    companion object {
        fun createDefaultRoot(): UIElementNode {
            return UIElementNode(
                id = "root_container",
                type = UIElementType.COLUMN,
                name = "Screen Root (Column)",
                properties = ElementProperties(
                    widthMode = "FILL",
                    heightMode = "FILL",
                    paddingHorizontal = 16,
                    paddingVertical = 16,
                    spacingDp = 12,
                    backgroundColorHex = "#F8FAFC"
                )
            )
        }
    }
}

data class AppProject(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "My Android App",
    val packageName: String = "com.example.myapp",
    val screens: List<AppScreen> = listOf(AppScreen(name = "Home Screen")),
    val currentScreenId: String = screens.firstOrNull()?.id ?: ""
) {
    val currentScreen: AppScreen
        get() = screens.find { it.id == currentScreenId } ?: screens.first()

    fun updateScreen(updated: AppScreen): AppProject {
        return copy(screens = screens.map { if (it.id == updated.id) updated else it })
    }

    fun addScreen(name: String): AppProject {
        val newScreen = AppScreen(name = name)
        return copy(screens = screens + newScreen, currentScreenId = newScreen.id)
    }

    fun deleteScreen(screenId: String): AppProject {
        if (screens.size <= 1) return this // Keep at least one screen
        val filtered = screens.filter { it.id != screenId }
        val nextCurrent = if (currentScreenId == screenId) filtered.first().id else currentScreenId
        return copy(screens = filtered, currentScreenId = nextCurrent)
    }
}
