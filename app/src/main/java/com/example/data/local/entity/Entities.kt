package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String,
    val language: String = "en",
    val timezone: String = "UTC",
    val theme: String = "dark",
    @ColumnInfo(name = "notification_preferences") val notificationPreferences: Boolean = true,
    @ColumnInfo(name = "gemini_api_key") val geminiApiKey: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String = "default_user",
    val name: String,
    val description: String = "",
    val status: String = "Active", // Active, In Progress, Review, Archived
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String = "default_user",
    @ColumnInfo(name = "project_id") val projectId: String? = null,
    val title: String = "New Conversation",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "conversation_id") val conversationId: String,
    @ColumnInfo(name = "user_id") val userId: String = "default_user",
    val role: String, // "user", "assistant", "system", "tool"
    val content: String,
    val status: String = "completed", // "thinking", "generating", "executing_tool", "approval_required", "completed", "failed", "cancelled"
    @ColumnInfo(name = "tool_name") val toolName: String? = null,
    @ColumnInfo(name = "tool_args") val toolArgs: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "action_cards")
data class ActionCardEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val source: String = "Buildlabs AI Agent",
    val priority: String = "Medium", // High, Medium, Low
    val deadline: String? = null,
    val status: String = "Pending", // Pending, Approved, Dismissed, Completed
    @ColumnInfo(name = "requires_approval") val requiresApproval: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String = "default_user",
    val category: String = "Preference", // Profile, Preference, Decision, Fact, Project
    val content: String,
    @ColumnInfo(name = "source_reference") val sourceReference: String = "Chat",
    val confidence: Float = 0.95f,
    @ColumnInfo(name = "is_approved") val isApproved: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tool_executions")
data class ToolExecutionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "tool_name") val toolName: String,
    @ColumnInfo(name = "arguments_json") val argumentsJson: String,
    @ColumnInfo(name = "result_json") val resultJson: String? = null,
    val status: String = "Pending", // Pending, Approved, Rejected, Executed, Failed
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
