package com.composecraft.app.viewmodel

import androidx.lifecycle.ViewModel
import com.composecraft.app.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DesignerViewModel : ViewModel() {

    private val _project = MutableStateFlow(ProjectTemplates.createAIAssistantTemplate())
    val project: StateFlow<AppProject> = _project.asStateFlow()

    private val _selectedNodeId = MutableStateFlow<String?>("login_root")
    val selectedNodeId: StateFlow<String?> = _selectedNodeId.asStateFlow()

    private val _isDesignMode = MutableStateFlow(true)
    val isDesignMode: StateFlow<Boolean> = _isDesignMode.asStateFlow()

    private val _isFreeFormMode = MutableStateFlow(false)
    val isFreeFormMode: StateFlow<Boolean> = _isFreeFormMode.asStateFlow()

    // Undo / Redo stacks
    private val undoStack = mutableListOf<AppProject>()
    private val redoStack = mutableListOf<AppProject>()

    val selectedNode: UIElementNode?
        get() = _selectedNodeId.value?.let { id -> _project.value.currentScreen.rootNode.findNode(id) }

    fun selectNode(id: String?) {
        _selectedNodeId.value = id
    }

    fun toggleDesignMode() {
        _isDesignMode.update { !it }
        if (!_isDesignMode.value) {
            _selectedNodeId.value = null // clear selection in play mode
        }
    }

    fun toggleCanvasMode() {
        _isFreeFormMode.update { !it }
    }

    private fun saveSnapshot() {
        undoStack.add(_project.value)
        if (undoStack.size > 25) undoStack.removeAt(0)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val last = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_project.value)
            _project.value = last
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_project.value)
            _project.value = next
        }
    }

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun updateNodeProperties(updatedProps: ElementProperties) {
        val currId = _selectedNodeId.value ?: return
        val current = selectedNode ?: return
        saveSnapshot()
        val updatedNode = current.copy(properties = updatedProps)
        val updatedRoot = _project.value.currentScreen.rootNode.updateNode(updatedNode)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
    }

    fun updateNodeName(newName: String) {
        val current = selectedNode ?: return
        saveSnapshot()
        val updatedNode = current.copy(name = newName)
        val updatedRoot = _project.value.currentScreen.rootNode.updateNode(updatedNode)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
    }

    fun addElement(type: UIElementType, containerId: String? = null) {
        saveSnapshot()
        val newNode = UIElementNode.createDefault(type)
        val targetContainer = containerId
            ?: (if (selectedNode?.type?.isContainer == true) selectedNode?.id else null)
            ?: _project.value.currentScreen.rootNode.id

        val updatedRoot = _project.value.currentScreen.rootNode.addChild(targetContainer, newNode)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
        _selectedNodeId.value = newNode.id
    }

    fun deleteSelectedNode() {
        val targetId = _selectedNodeId.value ?: return
        if (targetId == _project.value.currentScreen.rootNode.id) return // Don't delete root
        saveSnapshot()
        val updatedRoot = _project.value.currentScreen.rootNode.deleteNode(targetId)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
        _selectedNodeId.value = null
    }

    fun duplicateSelectedNode() {
        val current = selectedNode ?: return
        if (current.id == _project.value.currentScreen.rootNode.id) return
        saveSnapshot()
        val duplicated = current.duplicate()
        val updatedRoot = _project.value.currentScreen.rootNode.insertChildAdjacent(current.id, duplicated, before = false)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
        _selectedNodeId.value = duplicated.id
    }

    fun moveNodeFreeForm(nodeId: String, newX: Float, newY: Float) {
        val node = _project.value.currentScreen.rootNode.findNode(nodeId) ?: return
        val updatedProps = node.properties.copy(posX = newX, posY = newY)
        val updatedNode = node.copy(properties = updatedProps)
        val updatedRoot = _project.value.currentScreen.rootNode.updateNode(updatedNode)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
    }

    fun switchScreen(screenId: String) {
        _project.update { it.copy(currentScreenId = screenId) }
        _selectedNodeId.value = null
    }

    fun addScreen(name: String) {
        saveSnapshot()
        _project.update { it.addScreen(name) }
        _selectedNodeId.value = null
    }

    fun deleteScreen(screenId: String) {
        saveSnapshot()
        _project.update { it.deleteScreen(screenId) }
        _selectedNodeId.value = null
    }

    fun loadTemplate(template: AppProject) {
        saveSnapshot()
        _project.value = template
        _selectedNodeId.value = template.currentScreen.rootNode.id
    }
}
