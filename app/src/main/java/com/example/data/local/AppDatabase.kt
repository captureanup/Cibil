package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ActionCardDao
import com.example.data.local.dao.ConversationDao
import com.example.data.local.dao.MemoryDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.ToolExecutionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.ActionCardEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ToolExecutionEntity
import com.example.data.local.entity.UserPreferencesEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        UserPreferencesEntity::class,
        ProjectEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        ActionCardEntity::class,
        MemoryEntity::class,
        ToolExecutionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun conversationDao(): ConversationDao
    abstract fun actionCardDao(): ActionCardDao
    abstract fun memoryDao(): MemoryDao
    abstract fun toolExecutionDao(): ToolExecutionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "buildlabs_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    seedInitialData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            // Seed default profile
            val defaultUser = UserProfileEntity(
                id = "usr_lead_01",
                email = "user@buildlabs.ai",
                displayName = "Alex Carter",
                avatarUrl = ""
            )
            database.userDao().insertOrUpdateProfile(defaultUser)

            val defaultPrefs = UserPreferencesEntity(
                userId = defaultUser.id,
                theme = "dark",
                language = "en",
                timezone = "America/Los_Angeles"
            )
            database.userDao().insertOrUpdatePreferences(defaultPrefs)

            // Seed initial active project
            val initialProject = ProjectEntity(
                id = "proj_01",
                userId = defaultUser.id,
                name = "Autonomous Agent Infrastructure",
                description = "Architecting cloud handoff, tool approval gating, and Gemini 3.8 Flash model gateway.",
                status = "Active"
            )
            database.projectDao().insertProject(initialProject)

            // Seed initial conversation
            val welcomeConv = ConversationEntity(
                id = "conv_welcome",
                userId = defaultUser.id,
                projectId = initialProject.id,
                title = "Buildlabs Agent Genesis"
            )
            database.conversationDao().insertConversation(welcomeConv)

            val welcomeMsg1 = MessageEntity(
                conversationId = welcomeConv.id,
                userId = defaultUser.id,
                role = "user",
                content = "Initialize BUILDLABS personal AI operating system.",
                status = "completed"
            )
            val welcomeMsg2 = MessageEntity(
                conversationId = welcomeConv.id,
                userId = "buildlabs_agent",
                role = "assistant",
                content = """Welcome to **BUILDLABS** — *Intelligence That Gets Things Done.*

I am your personal AI operating system. I am equipped with:
- **Autonomous Agent Orchestration**: Multi-turn planning, tool calling, and strict authorization gating.
- **Context-Aware Projects**: Dedicated workspace threads with project-level memory.
- **Action Cards**: Real-time actionable tasks, approvals, and reminders.
- **Long-Term Memory Engine**: User-approved preferences and operational context.

How can I assist you with your projects today?""".trimIndent(),
                status = "completed"
            )
            database.conversationDao().insertMessage(welcomeMsg1)
            database.conversationDao().insertMessage(welcomeMsg2)

            // Seed initial Action Cards
            database.actionCardDao().insertActionCard(
                ActionCardEntity(
                    id = "act_01",
                    title = "Review Model Gateway Configuration",
                    description = "Verify Gemini API credentials and configure fallback provider in Settings.",
                    source = "Buildlabs Core Orchestrator",
                    priority = "High",
                    deadline = "Today",
                    requiresApproval = true
                )
            )
            database.actionCardDao().insertActionCard(
                ActionCardEntity(
                    id = "act_02",
                    title = "Verify Cloud Browser Handoff Isolation",
                    description = "Ensure Playwright sandbox and egress rules are enforced before launching sessions.",
                    source = "Security Vault",
                    priority = "Medium",
                    deadline = "Tomorrow",
                    requiresApproval = false
                )
            )

            // Seed initial memory
            database.memoryDao().insertMemory(
                MemoryEntity(
                    userId = defaultUser.id,
                    category = "Preference",
                    content = "User prefers concise, production-oriented solutions and high-security tool verification.",
                    sourceReference = "Genesis Onboarding"
                )
            )
        }
    }
}
