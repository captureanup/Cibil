package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ActionCardEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ToolExecutionEntity
import com.example.data.local.entity.UserPreferencesEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BuildlabsRepository(private val database: AppDatabase) {

    // User Profile & Preferences
    val userProfile: Flow<UserProfileEntity?> = database.userDao().getUserProfile()

    fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> =
        database.userDao().getUserPreferences(userId)

    suspend fun updateProfile(profile: UserProfileEntity) {
        database.userDao().insertOrUpdateProfile(profile)
    }

    suspend fun updatePreferences(preferences: UserPreferencesEntity) {
        database.userDao().insertOrUpdatePreferences(preferences)
    }

    // Projects
    val allProjects: Flow<List<ProjectEntity>> = database.projectDao().getAllProjects()
    val activeProjects: Flow<List<ProjectEntity>> = database.projectDao().getActiveProjects()

    suspend fun getProjectById(id: String): ProjectEntity? = database.projectDao().getProjectById(id)

    suspend fun createProject(name: String, description: String, status: String = "Active"): ProjectEntity {
        val project = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            status = status
        )
        database.projectDao().insertProject(project)
        return project
    }

    suspend fun updateProject(project: ProjectEntity) {
        database.projectDao().updateProject(project)
    }

    suspend fun deleteProject(id: String) {
        database.projectDao().deleteProjectById(id)
    }

    // Conversations
    val allConversations: Flow<List<ConversationEntity>> = database.conversationDao().getAllConversations()

    fun getConversationsByProject(projectId: String): Flow<List<ConversationEntity>> =
        database.conversationDao().getConversationsByProject(projectId)

    suspend fun createConversation(title: String, projectId: String? = null): ConversationEntity {
        val conv = ConversationEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            projectId = projectId
        )
        database.conversationDao().insertConversation(conv)
        return conv
    }

    suspend fun deleteConversation(id: String) {
        database.conversationDao().deleteConversationById(id)
        database.conversationDao().deleteMessagesForConversation(id)
    }

    // Messages
    fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        database.conversationDao().getMessagesForConversation(conversationId)

    suspend fun getMessagesList(conversationId: String): List<MessageEntity> =
        database.conversationDao().getMessagesList(conversationId)

    suspend fun addMessage(
        conversationId: String,
        role: String,
        content: String,
        status: String = "completed",
        toolName: String? = null,
        toolArgs: String? = null
    ): MessageEntity {
        val msg = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = role,
            content = content,
            status = status,
            toolName = toolName,
            toolArgs = toolArgs
        )
        database.conversationDao().insertMessage(msg)
        return msg
    }

    suspend fun updateMessage(message: MessageEntity) {
        database.conversationDao().updateMessage(message)
    }

    // Action Cards
    val allActionCards: Flow<List<ActionCardEntity>> = database.actionCardDao().getAllActionCards()
    val pendingActionCards: Flow<List<ActionCardEntity>> = database.actionCardDao().getPendingActionCards()

    suspend fun createActionCard(
        title: String,
        description: String,
        source: String = "Buildlabs Agent",
        priority: String = "Medium",
        deadline: String? = null,
        requiresApproval: Boolean = false
    ): ActionCardEntity {
        val card = ActionCardEntity(
            title = title,
            description = description,
            source = source,
            priority = priority,
            deadline = deadline,
            requiresApproval = requiresApproval
        )
        database.actionCardDao().insertActionCard(card)
        return card
    }

    suspend fun updateActionCard(card: ActionCardEntity) {
        database.actionCardDao().updateActionCard(card)
    }

    suspend fun deleteActionCard(id: String) {
        database.actionCardDao().deleteActionCard(id)
    }

    // Memories
    val allMemories: Flow<List<MemoryEntity>> = database.memoryDao().getAllMemories()

    suspend fun addMemory(category: String, content: String, source: String = "Chat"): MemoryEntity {
        val mem = MemoryEntity(
            category = category,
            content = content,
            sourceReference = source
        )
        database.memoryDao().insertMemory(mem)
        return mem
    }

    suspend fun deleteMemory(id: String) {
        database.memoryDao().deleteMemory(id)
    }

    // Tool Executions
    suspend fun logToolExecution(
        toolName: String,
        argsJson: String,
        resultJson: String? = null,
        status: String = "Executed"
    ): ToolExecutionEntity {
        val execution = ToolExecutionEntity(
            toolName = toolName,
            argumentsJson = argsJson,
            resultJson = resultJson,
            status = status
        )
        database.toolExecutionDao().insertExecution(execution)
        return execution
    }
}
