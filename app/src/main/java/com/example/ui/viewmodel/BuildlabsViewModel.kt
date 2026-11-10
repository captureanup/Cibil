package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AgentOrchestrator
import com.example.ai.ContextBuilder
import com.example.ai.GeminiProvider
import com.example.ai.ToolRegistry
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ActionCardEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.UserPreferencesEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.AgentExecutionState
import com.example.data.repository.BuildlabsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val title: String) {
    data object Home : Screen("Home")
    data object Chat : Screen("Chat")
    data object Projects : Screen("Projects")
    data object Actions : Screen("Actions")
    data object Panels : Screen("Panels")
    data object Specialties : Screen("Specialties")
    data object Automations : Screen("Scheduled Tasks")
    data object Integrations : Screen("Integrations")
    data object Handoff : Screen("Cloud Handoff")
    data object Vault : Screen("Security Vault")
    data object Notifications : Screen("Notifications")
    data object Settings : Screen("Settings")
    data object Auth : Screen("Profile & Account")
}

@OptIn(ExperimentalCoroutinesApi::class)
class BuildlabsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = BuildlabsRepository(database)
    val geminiProvider = GeminiProvider()
    val toolRegistry = ToolRegistry(repository)
    val contextBuilder = ContextBuilder()
    val agentOrchestrator = AgentOrchestrator(
        repository = repository,
        geminiProvider = geminiProvider,
        toolRegistry = toolRegistry,
        contextBuilder = contextBuilder
    )

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedProject = MutableStateFlow<ProjectEntity?>(null)
    val selectedProject: StateFlow<ProjectEntity?> = _selectedProject.asStateFlow()

    private val _selectedConversationId = MutableStateFlow<String?>("conv_welcome")
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userPreferences: StateFlow<UserPreferencesEntity?> = repository.getUserPreferences("usr_lead_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allConversations: StateFlow<List<ConversationEntity>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeActionCards: StateFlow<List<ActionCardEntity>> = repository.allActionCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agentState: StateFlow<AgentExecutionState> = agentOrchestrator.agentState

    val currentMessages: StateFlow<List<MessageEntity>> = _selectedConversationId
        .flatMapLatest { convId ->
            if (convId != null) repository.getMessages(convId) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectProject(project: ProjectEntity?) {
        _selectedProject.value = project
    }

    fun selectConversation(conversationId: String) {
        _selectedConversationId.value = conversationId
    }

    fun sendMessage(text: String) {
        val convId = _selectedConversationId.value ?: "conv_welcome"
        val userKey = userPreferences.value?.geminiApiKey
        val projectId = _selectedProject.value?.id

        viewModelScope.launch {
            agentOrchestrator.processUserMessage(
                conversationId = convId,
                userMessageText = text,
                projectId = projectId,
                userApiKey = userKey
            )
        }
    }

    fun approvePendingTool() {
        val pending = agentState.value.pendingApprovalCall ?: return
        val convId = _selectedConversationId.value ?: "conv_welcome"
        viewModelScope.launch {
            agentOrchestrator.executeToolDirectly(
                conversationId = convId,
                toolName = pending.name,
                argsJson = pending.argumentsJson
            )
        }
    }

    fun rejectPendingTool() {
        agentOrchestrator.dismissApproval()
    }

    fun stopGeneration() {
        agentOrchestrator.cancelOperation()
    }

    fun createProject(name: String, description: String) {
        viewModelScope.launch {
            val proj = repository.createProject(name, description)
            _selectedProject.value = proj
            val conv = repository.createConversation("Project: ${proj.name}", proj.id)
            _selectedConversationId.value = conv.id
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_selectedProject.value?.id == id) {
                _selectedProject.value = null
            }
        }
    }

    fun createNewConversation(title: String, projectId: String? = null) {
        viewModelScope.launch {
            val conv = repository.createConversation(title, projectId)
            _selectedConversationId.value = conv.id
            _currentScreen.value = Screen.Chat
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_selectedConversationId.value == id) {
                _selectedConversationId.value = null
            }
        }
    }

    fun updateApiKey(newKey: String) {
        viewModelScope.launch {
            val current = userPreferences.value ?: UserPreferencesEntity(userId = "usr_lead_01")
            repository.updatePreferences(current.copy(geminiApiKey = newKey))
        }
    }

    fun updateProfile(displayName: String, email: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity(email = email, displayName = displayName)
            repository.updateProfile(current.copy(displayName = displayName, email = email))
        }
    }

    fun completeActionCard(card: ActionCardEntity) {
        viewModelScope.launch {
            repository.updateActionCard(card.copy(status = "Completed"))
        }
    }

    fun dismissActionCard(card: ActionCardEntity) {
        viewModelScope.launch {
            repository.updateActionCard(card.copy(status = "Dismissed"))
        }
    }
}
