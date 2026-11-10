package com.example.ai

import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.UserProfileEntity

class ContextBuilder {

    fun buildSystemInstruction(
        user: UserProfileEntity?,
        project: ProjectEntity?,
        memories: List<MemoryEntity>,
        tools: List<ToolDefinition>
    ): String {
        val userName = user?.displayName ?: "User"
        val projectContext = if (project != null) {
            """
            CURRENT PROJECT CONTEXT:
            - Project Name: ${project.name}
            - Description: ${project.description}
            - Status: ${project.status}
            """.trimIndent()
        } else {
            "CURRENT PROJECT CONTEXT: General Workspace (No specific project assigned)."
        }

        val memoryContext = if (memories.isNotEmpty()) {
            val items = memories.take(5).joinToString("\n") { "- [${it.category}]: ${it.content}" }
            """
            STORED LONG-TERM USER MEMORIES:
            $items
            """.trimIndent()
        } else {
            "STORED LONG-TERM USER MEMORIES: None recorded yet."
        }

        val toolList = tools.joinToString("\n") { tool ->
            "- ${tool.name}: ${tool.description} (Requires User Approval: ${tool.requiresApproval})"
        }

        return """
        You are BUILDLABS — an advanced personal AI operating system and autonomous agent platform.
        Tagline: "Intelligence That Gets Things Done."
        User: $userName
        
        $projectContext
        
        $memoryContext
        
        AVAILABLE AGENT TOOLS:
        $toolList
        
        BEHAVIOR RULES:
        1. Act as a senior product architect, autonomous agent, and executive technical assistant.
        2. Deliver clear, actionable, production-grade solutions. Never generate fake success claims.
        3. If a task requires one of the available tools, propose the tool call clearly.
        4. If a tool requires approval (such as create_project), specify the parameters clearly.
        5. Format code blocks cleanly with proper language specifiers.
        6. When executing tools, communicate clearly what steps are being taken.
        """.trimIndent()
    }

    fun buildPromptWithHistory(
        userPrompt: String,
        history: List<MessageEntity>
    ): String {
        val sb = StringBuilder()
        val relevantHistory = history.takeLast(8)
        if (relevantHistory.isNotEmpty()) {
            sb.append("CONVERSATION HISTORY:\n")
            relevantHistory.forEach { msg ->
                val speaker = if (msg.role == "user") "User" else "Buildlabs Agent"
                sb.append("$speaker: ${msg.content}\n")
            }
            sb.append("\n")
        }
        sb.append("CURRENT REQUEST:\n$userPrompt")
        return sb.toString()
    }
}
