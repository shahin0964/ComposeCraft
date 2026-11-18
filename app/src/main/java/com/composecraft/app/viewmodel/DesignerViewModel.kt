package com.composecraft.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.composecraft.app.engine.ProjectStorage
import com.composecraft.app.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DesignerViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // Screen Route: "home" or "editor"
    private val _currentRoute = MutableStateFlow("home")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // Saved projects list
    private val _savedProjects = MutableStateFlow<List<AppProject>>(emptyList())
    val savedProjects: StateFlow<List<AppProject>> = _savedProjects.asStateFlow()

    // Currently opened project
    private val _project = MutableStateFlow<AppProject>(AppProject.createBlank("My App", "com.example.myapp"))
    val project: StateFlow<AppProject> = _project.asStateFlow()

    private val _selectedNodeId = MutableStateFlow<String?>(null)
    val selectedNodeId: StateFlow<String?> = _selectedNodeId.asStateFlow()

    private val _isDesignMode = MutableStateFlow(true)
    val isDesignMode: StateFlow<Boolean> = _isDesignMode.asStateFlow()

    private val _isFreeFormMode = MutableStateFlow(false)
    val isFreeFormMode: StateFlow<Boolean> = _isFreeFormMode.asStateFlow()

    // Undo / Redo stacks
    private val undoStack = mutableListOf<AppProject>()
    private val redoStack = mutableListOf<AppProject>()

    init {
        refreshProjects()
    }

    fun refreshProjects() {
        val loaded = ProjectStorage.loadAllProjects(context)
        _savedProjects.value = loaded
    }

    val selectedNode: UIElementNode?
        get() = _selectedNodeId.value?.let { id -> _project.value.currentScreen.rootNode.findNode(id) }

    fun createNewBlankProject(name: String, packageName: String) {
        val newProj = AppProject.createBlank(name, packageName)
        ProjectStorage.saveProject(context, newProj)
        _project.value = newProj
        _selectedNodeId.value = newProj.currentScreen.rootNode.id
        undoStack.clear()
        redoStack.clear()
        refreshProjects()
        _currentRoute.value = "editor"
    }

    fun openProject(proj: AppProject) {
        _project.value = proj
        _selectedNodeId.value = proj.currentScreen.rootNode.id
        undoStack.clear()
        redoStack.clear()
        _currentRoute.value = "editor"
    }

    fun cloneSampleProject(sample: AppProject) {
        val cloned = sample.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${sample.name} (Copy)",
            updatedAtTimestamp = System.currentTimeMillis()
        )
        ProjectStorage.saveProject(context, cloned)
        _project.value = cloned
        _selectedNodeId.value = cloned.currentScreen.rootNode.id
        undoStack.clear()
        redoStack.clear()
        refreshProjects()
        _currentRoute.value = "editor"
    }

    fun importProjectFromJson(jsonString: String): Boolean {
        val parsed = ProjectStorage.parseProjectFromJson(jsonString) ?: return false
        val newProj = parsed.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${parsed.name} (Imported)",
            updatedAtTimestamp = System.currentTimeMillis()
        )
        ProjectStorage.saveProject(context, newProj)
        refreshProjects()
        openProject(newProj)
        return true
    }

    fun deleteSavedProject(projectId: String) {
        ProjectStorage.deleteProject(context, projectId)
        refreshProjects()
    }

    fun navigateToHome() {
        // Auto-save on exit
        ProjectStorage.saveProject(context, _project.value)
        refreshProjects()
        _currentRoute.value = "home"
    }

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

    private fun persistProject() {
        ProjectStorage.saveProject(context, _project.value)
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val last = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_project.value)
            _project.value = last
            persistProject()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_project.value)
            _project.value = next
            persistProject()
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
        persistProject()
    }

    fun updateNodeName(newName: String) {
        val current = selectedNode ?: return
        saveSnapshot()
        val updatedNode = current.copy(name = newName)
        val updatedRoot = _project.value.currentScreen.rootNode.updateNode(updatedNode)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
        persistProject()
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
        persistProject()
    }

    fun deleteSelectedNode() {
        val targetId = _selectedNodeId.value ?: return
        if (targetId == _project.value.currentScreen.rootNode.id) return // Don't delete root
        saveSnapshot()
        val updatedRoot = _project.value.currentScreen.rootNode.deleteNode(targetId)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
        _selectedNodeId.value = null
        persistProject()
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
        persistProject()
    }

    fun moveNodeFreeForm(nodeId: String, newX: Float, newY: Float) {
        val node = _project.value.currentScreen.rootNode.findNode(nodeId) ?: return
        val updatedProps = node.properties.copy(posX = newX, posY = newY)
        val updatedNode = node.copy(properties = updatedProps)
        val updatedRoot = _project.value.currentScreen.rootNode.updateNode(updatedNode)
        val updatedScreen = _project.value.currentScreen.copy(rootNode = updatedRoot)
        _project.update { it.updateScreen(updatedScreen) }
        persistProject()
    }

    fun switchScreen(screenId: String) {
        _project.update { it.copy(currentScreenId = screenId) }
        _selectedNodeId.value = null
    }

    fun addScreen(name: String) {
        saveSnapshot()
        _project.update { it.addScreen(name) }
        _selectedNodeId.value = null
        persistProject()
    }

    fun deleteScreen(screenId: String) {
        saveSnapshot()
        _project.update { it.deleteScreen(screenId) }
        _selectedNodeId.value = null
        persistProject()
    }
}
