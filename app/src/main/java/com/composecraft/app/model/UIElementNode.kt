package com.composecraft.app.model

import java.util.UUID

data class UIElementNode(
    val id: String = UUID.randomUUID().toString(),
    val type: UIElementType,
    val name: String = type.displayName,
    val properties: ElementProperties = ElementProperties(),
    val children: List<UIElementNode> = emptyList()
) {
    /**
     * Recursively search for a node by its ID.
     */
    fun findNode(targetId: String): UIElementNode? {
        if (id == targetId) return this
        for (child in children) {
            val found = child.findNode(targetId)
            if (found != null) return found
        }
        return null
    }

    /**
     * Recursively update a node in the tree.
     */
    fun updateNode(updated: UIElementNode): UIElementNode {
        if (id == updated.id) return updated
        return copy(children = children.map { it.updateNode(updated) })
    }

    /**
     * Recursively delete a node from the tree.
     */
    fun deleteNode(targetId: String): UIElementNode {
        return copy(
            children = children
                .filter { it.id != targetId }
                .map { it.deleteNode(targetId) }
        )
    }

    /**
     * Add a child to a specific container node.
     */
    fun addChild(containerId: String, newChild: UIElementNode): UIElementNode {
        if (id == containerId && type.isContainer) {
            return copy(children = children + newChild)
        }
        return copy(children = children.map { it.addChild(containerId, newChild) })
    }

    /**
     * Insert a child before or after a target node inside a container.
     */
    fun insertChildAdjacent(targetId: String, newChild: UIElementNode, before: Boolean): UIElementNode {
        val index = children.indexOfFirst { it.id == targetId }
        if (index != -1) {
            val mutable = children.toMutableList()
            val insertAt = if (before) index else index + 1
            mutable.add(insertAt, newChild)
            return copy(children = mutable)
        }
        return copy(children = children.map { it.insertChildAdjacent(targetId, newChild, before) })
    }

    /**
     * Duplicate a node with new unique IDs for it and all descendants.
     */
    fun duplicate(): UIElementNode {
        return copy(
            id = UUID.randomUUID().toString(),
            name = "$name (Copy)",
            children = children.map { it.duplicate() }
        )
    }

    companion object {
        fun createDefault(type: UIElementType, name: String? = null): UIElementNode {
            val defaultProps = when (type) {
                UIElementType.GLOWING_ORB -> ElementProperties(
                    orbSizeDp = 150,
                    orbColorPrimaryHex = "#00F2FE",
                    orbColorSecondaryHex = "#4FACFE",
                    orbGlowColorHex = "#7F00FF",
                    animationSpeedMs = 2400,
                    widthMode = "WRAP",
                    heightMode = "WRAP"
                )
                UIElementType.WAVEFORM -> ElementProperties(
                    widthMode = "FILL",
                    heightMode = "FIXED",
                    fixedHeightDp = 48,
                    waveformBars = 20,
                    waveformColorHex = "#00F2FE",
                    animationSpeedMs = 800
                )
                UIElementType.SHIMMER_BOX -> ElementProperties(
                    widthMode = "FILL",
                    cornerRadius = 16,
                    heightMode = "WRAP"
                )
                UIElementType.ROTATING_BORDER_CARD -> ElementProperties(
                    backgroundColorHex = "#0F172A",
                    orbColorPrimaryHex = "#00F2FE",
                    orbColorSecondaryHex = "#7F00FF",
                    cornerRadius = 20,
                    widthMode = "FILL"
                )
                UIElementType.PULSE_CONTAINER -> ElementProperties(
                    backgroundColorHex = "#1E1B4B",
                    cornerRadius = 24,
                    elevation = 8,
                    borderWidth = 2,
                    borderColorHex = "#6366F1",
                    paddingHorizontal = 20,
                    paddingVertical = 20,
                    widthMode = "FILL"
                )
                UIElementType.SLIDE_TO_ACTION -> ElementProperties(
                    text = "Slide to confirm >>",
                    backgroundColorHex = "#1E293B",
                    orbColorPrimaryHex = "#00F2FE",
                    widthMode = "WRAP",
                    heightMode = "WRAP",
                    actionType = "TOAST",
                    actionTarget = "Action confirmed via slide bar! 🚀"
                )
                UIElementType.FLOATING_NAV_BAR -> ElementProperties(
                    backgroundColorHex = "#0F172A",
                    orbColorPrimaryHex = "#00F2FE",
                    widthMode = "FILL"
                )
                UIElementType.SEGMENTED_TABS -> ElementProperties(
                    backgroundColorHex = "#1E293B",
                    orbColorSecondaryHex = "#4F46E5",
                    widthMode = "FILL"
                )
                UIElementType.SIDE_DRAWER_PREVIEW -> ElementProperties(
                    backgroundColorHex = "#0F172A",
                    orbColorPrimaryHex = "#00F2FE",
                    widthMode = "FILL"
                )
                UIElementType.GRADIENT_BUTTON -> ElementProperties(
                    text = "✨ Upgrade to Pro",
                    backgroundColorHex = "#4F46E5",
                    textColorHex = "#FFFFFF",
                    cornerRadius = 16,
                    paddingVertical = 14,
                    paddingHorizontal = 24,
                    fontSize = 16,
                    fontWeight = "Bold",
                    widthMode = "FILL"
                )
                UIElementType.TEXT -> ElementProperties(
                    text = "Welcome to ComposeCraft",
                    fontSize = 18,
                    fontWeight = "Bold",
                    textColorHex = "#0F172A"
                )
                UIElementType.BUTTON -> ElementProperties(
                    text = "Get Started",
                    backgroundColorHex = "#4F46E5",
                    textColorHex = "#FFFFFF",
                    cornerRadius = 14,
                    paddingVertical = 12,
                    paddingHorizontal = 24,
                    widthMode = "FILL"
                )
                UIElementType.OUTLINED_BUTTON -> ElementProperties(
                    text = "Cancel",
                    backgroundColorHex = "",
                    borderWidth = 2,
                    borderColorHex = "#4F46E5",
                    textColorHex = "#4F46E5",
                    cornerRadius = 14,
                    widthMode = "FILL"
                )
                UIElementType.TEXT_FIELD -> ElementProperties(
                    text = "",
                    placeholder = "Enter your email...",
                    cornerRadius = 12,
                    borderWidth = 1,
                    borderColorHex = "#CBD5E1",
                    backgroundColorHex = "#F8FAFC",
                    widthMode = "FILL"
                )
                UIElementType.CARD -> ElementProperties(
                    backgroundColorHex = "#FFFFFF",
                    cornerRadius = 16,
                    elevation = 4,
                    paddingHorizontal = 16,
                    paddingVertical = 16,
                    widthMode = "FILL",
                    borderColorHex = "#E2E8F0",
                    borderWidth = 1
                )
                UIElementType.COLUMN -> ElementProperties(
                    widthMode = "FILL",
                    heightMode = "WRAP",
                    spacingDp = 12,
                    arrangement = "Top",
                    alignment = "CenterHorizontally"
                )
                UIElementType.ROW -> ElementProperties(
                    widthMode = "FILL",
                    heightMode = "WRAP",
                    spacingDp = 12,
                    arrangement = "SpaceBetween",
                    alignment = "CenterHorizontally"
                )
                UIElementType.BOX -> ElementProperties(
                    widthMode = "FILL",
                    heightMode = "WRAP"
                )
                UIElementType.TOP_APP_BAR -> ElementProperties(
                    text = "My App Screen",
                    backgroundColorHex = "#1E293B",
                    textColorHex = "#FFFFFF",
                    iconName = "ArrowBack",
                    widthMode = "FILL",
                    heightMode = "FIXED",
                    fixedHeightDp = 56
                )
                UIElementType.BOTTOM_NAV_BAR -> ElementProperties(
                    backgroundColorHex = "#0F172A",
                    textColorHex = "#94A3B8",
                    widthMode = "FILL",
                    heightMode = "FIXED",
                    fixedHeightDp = 64
                )
                UIElementType.FLOATING_ACTION_BUTTON -> ElementProperties(
                    backgroundColorHex = "#4F46E5",
                    textColorHex = "#FFFFFF",
                    iconName = "Add",
                    widthMode = "WRAP",
                    cornerRadius = 28
                )
                UIElementType.IMAGE -> ElementProperties(
                    widthMode = "FILL",
                    heightMode = "FIXED",
                    fixedHeightDp = 180,
                    backgroundColorHex = "#E2E8F0",
                    cornerRadius = 16
                )
                UIElementType.ICON -> ElementProperties(
                    iconName = "Star",
                    textColorHex = "#F59E0B",
                    widthMode = "WRAP",
                    fixedWidthDp = 32,
                    fixedHeightDp = 32
                )
                UIElementType.SWITCH -> ElementProperties(
                    text = "Enable Notifications",
                    isChecked = true,
                    textColorHex = "#1E293B",
                    widthMode = "FILL"
                )
                UIElementType.CHECKBOX -> ElementProperties(
                    text = "I agree to terms",
                    isChecked = false,
                    textColorHex = "#1E293B",
                    widthMode = "FILL"
                )
                UIElementType.SLIDER -> ElementProperties(
                    sliderValue = 65f,
                    widthMode = "FILL"
                )
                UIElementType.SPACER -> ElementProperties(
                    heightMode = "FIXED",
                    fixedHeightDp = 16,
                    widthMode = "FILL"
                )
                UIElementType.DIVIDER -> ElementProperties(
                    heightMode = "FIXED",
                    fixedHeightDp = 1,
                    borderColorHex = "#E2E8F0",
                    borderWidth = 1,
                    widthMode = "FILL"
                )
            }
            return UIElementNode(
                type = type,
                name = name ?: type.displayName,
                properties = defaultProps
            )
        }
    }
}
