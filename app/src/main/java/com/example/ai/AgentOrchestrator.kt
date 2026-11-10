package com.example.ai

import com.example.data.local.entity.MessageEntity
import com.example.data.model.AgentExecutionState
import com.example.data.model.AgentStatus
import com.example.data.model.ToolCall
import com.example.data.repository.BuildlabsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject
import java.util.UUID

class AgentOrchestrator(
    private val repository: BuildlabsRepository,
    private val geminiProvider: GeminiProvider,
    private val toolRegistry: ToolRegistry,
    private val contextBuilder: ContextBuilder
) {

    private val _agentState = MutableStateFlow(AgentExecutionState())
    val agentState: StateFlow<AgentExecutionState> = _agentState.asStateFlow()

    suspend fun processUserMessage(
        conversationId: String,
        userMessageText: String,
        projectId: String?,
        userApiKey: String?
    ) {
        _agentState.value = AgentExecutionState(
            status = AgentStatus.THINKING,
            currentStep = "Analyzing request & retrieving context..."
        )

        // 1. Save user message to database
        repository.addMessage(
            conversationId = conversationId,
            role = "user",
            content = userMessageText,
            status = "completed"
        )

        // 2. Fetch context elements
        val userProfile = repository.userProfile.firstOrNull()
        val currentProject = projectId?.let { repository.getProjectById(it) }
        val memories = repository.allMemories.firstOrNull() ?: emptyList()
        val history = repository.getMessagesList(conversationId)

        val systemInstruction = contextBuilder.buildSystemInstruction(
            user = userProfile,
            project = currentProject,
            memories = memories,
            tools = toolRegistry.tools
        )

        val fullPrompt = contextBuilder.buildPromptWithHistory(userMessageText, history)

        // Check if user request is directly an action command for tools
        val intentTool = detectToolIntent(userMessageText)
        if (intentTool != null) {
            handleDirectToolIntent(conversationId, intentTool, userMessageText)
            return
        }

        // 3. Otherwise stream real AI response from GeminiProvider
        _agentState.value = AgentExecutionState(
            status = AgentStatus.GENERATING,
            currentStep = "Streaming response from Gemini Brain..."
        )

        val placeholderAssistantMsg = repository.addMessage(
            conversationId = conversationId,
            role = "assistant",
            content = "",
            status = "generating"
        )

        val accumulatedContent = StringBuilder()

        geminiProvider.streamChatResponse(
            prompt = fullPrompt,
            systemInstruction = systemInstruction,
            userCustomKey = userApiKey,
            tools = toolRegistry.tools
        ).collect { event ->
            when (event) {
                is AiStreamEvent.Started -> {
                    _agentState.value = _agentState.value.copy(
                        status = AgentStatus.GENERATING,
                        currentStep = "Connected to ${event.modelName}"
                    )
                }

                is AiStreamEvent.ContentDelta -> {
                    accumulatedContent.append(event.text)
                    _agentState.value = _agentState.value.copy(
                        streamingContent = accumulatedContent.toString()
                    )
                }

                is AiStreamEvent.Completed -> {
                    val finalMsg = event.fullText
                    repository.updateMessage(
                        placeholderAssistantMsg.copy(
                            content = finalMsg,
                            status = "completed"
                        )
                    )
                    _agentState.value = AgentExecutionState(
                        status = AgentStatus.COMPLETED,
                        currentStep = "Response generated successfully."
                    )
                }

                is AiStreamEvent.Failed -> {
                    repository.updateMessage(
                        placeholderAssistantMsg.copy(
                            content = "⚠️ ${event.error}",
                            status = "failed"
                        )
                    )
                    _agentState.value = AgentExecutionState(
                        status = AgentStatus.FAILED,
                        errorMessage = event.error,
                        currentStep = "Generation halted."
                    )
                }

                else -> {}
            }
        }
    }

    private fun detectToolIntent(text: String): Pair<String, JSONObject>? {
        val lower = text.lowercase().trim()
        return when {
            lower.contains("list my projects") || lower == "list projects" || lower.contains("show projects") -> {
                Pair("list_projects", JSONObject())
            }
            lower.startsWith("create project") || lower.contains("make a project called") -> {
                val name = text.replace("create project", "", ignoreCase = true)
                    .replace("make a project called", "", ignoreCase = true)
                    .trim(':', ' ', '"', '\'')
                val title = if (name.isNotBlank()) name else "New Initiative"
                val args = JSONObject().apply {
                    put("name", title)
                    put("description", "Initiative created by Buildlabs agent.")
                }
                Pair("create_project", args)
            }
            lower.startsWith("create action card") || lower.startsWith("add action card") -> {
                val desc = text.replace("create action card", "", ignoreCase = true)
                    .replace("add action card", "", ignoreCase = true)
                    .trim(':', ' ')
                val args = JSONObject().apply {
                    put("title", "Action: $desc")
                    put("description", desc)
                    put("priority", "High")
                    put("deadline", "This week")
                }
                Pair("create_action_card", args)
            }
            lower.contains("my profile") || lower.contains("who am i") || lower.contains("user profile") -> {
                Pair("get_user_profile", JSONObject())
            }
            else -> null
        }
    }

    private suspend fun handleDirectToolIntent(
        conversationId: String,
        toolIntent: Pair<String, JSONObject>,
        userPrompt: String
    ) {
        val toolName = toolIntent.first
        val toolArgs = toolIntent.second
        val toolDef = toolRegistry.getTool(toolName)

        if (toolDef != null && toolDef.requiresApproval) {
            val call = ToolCall(
                id = UUID.randomUUID().toString(),
                name = toolName,
                argumentsJson = toolArgs.toString()
            )
            _agentState.value = AgentExecutionState(
                status = AgentStatus.APPROVAL_REQUIRED,
                currentStep = "Operation requires authorization",
                activeToolName = toolName,
                activeToolArgs = toolArgs.toString(),
                pendingApprovalCall = call
            )

            repository.addMessage(
                conversationId = conversationId,
                role = "assistant",
                content = "I have prepared the action **$toolName** with parameters:\n```json\n${toolArgs.toString(2)}\n```\n⚠️ **Authorization Required**: Please confirm in the approval dialog to execute this action.",
                status = "approval_required",
                toolName = toolName,
                toolArgs = toolArgs.toString()
            )
            return
        }

        executeToolDirectly(conversationId, toolName, toolArgs.toString())
    }

    suspend fun executeToolDirectly(conversationId: String, toolName: String, argsJson: String) {
        _agentState.value = AgentExecutionState(
            status = AgentStatus.EXECUTING_TOOL,
            currentStep = "Executing $toolName...",
            activeToolName = toolName,
            activeToolArgs = argsJson
        )

        val resultJson = toolRegistry.executeTool(toolName, argsJson)
        repository.logToolExecution(toolName, argsJson, resultJson, status = "Executed")

        val responseContent = """
        ✅ **Tool Executed: `$toolName`**
        
        **Parameters**:
        ```json
        $argsJson
        ```
        
        **Result**:
        ```json
        ${JSONObject(resultJson).toString(2)}
        ```
        """.trimIndent()

        repository.addMessage(
            conversationId = conversationId,
            role = "assistant",
            content = responseContent,
            status = "completed",
            toolName = toolName,
            toolArgs = argsJson
        )

        _agentState.value = AgentExecutionState(
            status = AgentStatus.COMPLETED,
            currentStep = "Tool execution completed."
        )
    }

    fun dismissApproval() {
        _agentState.value = AgentExecutionState(
            status = AgentStatus.CANCELLED,
            currentStep = "Tool execution declined by user."
        )
    }

    fun cancelOperation() {
        _agentState.value = AgentExecutionState(
            status = AgentStatus.CANCELLED,
            currentStep = "Operation stopped by user."
        )
    }
}
