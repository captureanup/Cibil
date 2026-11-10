package com.example.ai

data class ToolParameter(
    val name: String,
    val type: String,
    val description: String,
    val required: Boolean = true
)

data class ToolDefinition(
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>,
    val requiresApproval: Boolean = false,
    val riskLevel: String = "Low" // Low, Medium, High
)

sealed interface AiStreamEvent {
    data class Started(val modelName: String) : AiStreamEvent
    data class ContentDelta(val text: String) : AiStreamEvent
    data class ToolCallProposed(val toolName: String, val argsJson: String, val requiresApproval: Boolean) : AiStreamEvent
    data class ToolCallStatus(val toolName: String, val status: String) : AiStreamEvent
    data class Completed(val fullText: String) : AiStreamEvent
    data class Failed(val error: String) : AiStreamEvent
}
