package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ActionCardEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ToolExecutionEntity
import com.example.data.local.entity.UserPreferencesEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users_profiles LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM users_profiles WHERE id = :id LIMIT 1")
    suspend fun getUserProfileById(id: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_preferences WHERE user_id = :userId LIMIT 1")
    fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: UserPreferencesEntity)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updated_at DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE status != 'Archived' ORDER BY updated_at DESC")
    fun getActiveProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY updated_at DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE project_id = :projectId ORDER BY updated_at DESC")
    fun getConversationsByProject(projectId: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: String)

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at ASC")
    suspend fun getMessagesList(conversationId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE conversation_id = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: String)
}

@Dao
interface ActionCardDao {
    @Query("SELECT * FROM action_cards ORDER BY created_at DESC")
    fun getAllActionCards(): Flow<List<ActionCardEntity>>

    @Query("SELECT * FROM action_cards WHERE status = 'Pending' ORDER BY created_at DESC")
    fun getPendingActionCards(): Flow<List<ActionCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionCard(card: ActionCardEntity)

    @Update
    suspend fun updateActionCard(card: ActionCardEntity)

    @Query("DELETE FROM action_cards WHERE id = :id")
    suspend fun deleteActionCard(id: String)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY created_at DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: String)
}

@Dao
interface ToolExecutionDao {
    @Query("SELECT * FROM tool_executions ORDER BY created_at DESC")
    fun getAllExecutions(): Flow<List<ToolExecutionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExecution(execution: ToolExecutionEntity)

    @Update
    suspend fun updateExecution(execution: ToolExecutionEntity)
}
