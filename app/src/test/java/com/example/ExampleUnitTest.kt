package com.example

import com.example.ai.ContextBuilder
import com.example.ai.ToolDefinition
import com.example.ai.ToolParameter
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.AgentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testContextBuilder_includesUserAndProjectContext() {
        val builder = ContextBuilder()
        val user = UserProfileEntity(email = "test@buildlabs.ai", displayName = "Alex Carter")
        val project = ProjectEntity(name = "AI Brain", description = "Model Gateway")
        val memories = listOf(
            MemoryEntity(category = "Preference", content = "Prefers concise responses")
        )
        val tools = listOf(
            ToolDefinition(
                name = "create_project",
                description = "Creates a workspace project",
                parameters = listOf(ToolParameter("name", "string", "Project Name")),
                requiresApproval = true
            )
        )

        val instruction = builder.buildSystemInstruction(user, project, memories, tools)

        assertTrue(instruction.contains("Alex Carter"))
        assertTrue(instruction.contains("AI Brain"))
        assertTrue(instruction.contains("Prefers concise responses"))
        assertTrue(instruction.contains("create_project"))
    }

    @Test
    fun testAgentStatus_labelsMatchExpected() {
        assertEquals("Thinking...", AgentStatus.THINKING.label)
        assertEquals("Generating response...", AgentStatus.GENERATING.label)
        assertEquals("Approval required", AgentStatus.APPROVAL_REQUIRED.label)
        assertEquals("Completed", AgentStatus.COMPLETED.label)
    }
}
