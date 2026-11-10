package com.example.data.model

data class ToolCall(
    val id: String,
    val name: String,
    val argumentsJson: String
)

data class ToolResult(
    val toolCallId: String,
    val toolName: String,
    val outputJson: String,
    val isError: Boolean = false
)

enum class AgentStatus(val label: String) {
    IDLE("Idle"),
    THINKING("Thinking..."),
    GENERATING("Generating response..."),
    EXECUTING_TOOL("Executing tool..."),
    APPROVAL_REQUIRED("Approval required"),
    COMPLETED("Completed"),
    FAILED("Execution failed"),
    CANCELLED("Cancelled")
}

data class AgentExecutionState(
    val status: AgentStatus = AgentStatus.IDLE,
    val currentStep: String = "",
    val activeToolName: String? = null,
    val activeToolArgs: String? = null,
    val pendingApprovalCall: ToolCall? = null,
    val streamingContent: String = "",
    val errorMessage: String? = null
)

data class NavItem(
    val id: String,
    val label: String,
    val iconName: String,
    val isImplemented: Boolean = true,
    val phaseTag: String = "Phase 1"
)
