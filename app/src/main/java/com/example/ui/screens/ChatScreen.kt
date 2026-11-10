package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AgentStatus
import com.example.ui.components.ChatMessageBubble
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRose
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.BuildlabsViewModel

@Composable
fun ChatScreen(
    viewModel: BuildlabsViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val agentState by viewModel.agentState.collectAsStateWithLifecycle()
    val conversations by viewModel.allConversations.collectAsStateWithLifecycle()
    val selectedConvId by viewModel.selectedConversationId.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val isAiConfigured = viewModel.geminiProvider.isConfigured(userPrefs?.geminiApiKey)

    var inputPrompt by remember { mutableStateOf("") }
    var showConvMenu by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll when new messages arrive or state updates
    LaunchedEffect(messages.size, agentState.streamingContent) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val activeConv = conversations.find { it.id == selectedConvId }
    val convTitle = activeConv?.title ?: "Main Workspace Chat"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDark)
            .imePadding()
    ) {
        // Top Subheader: Conversation Switcher & Project Context Tag
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyCard)
                .border(0.5.dp, NavyBorder)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showConvMenu = true }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("chat_conversation_dropdown")
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = convTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                }

                DropdownMenu(
                    expanded = showConvMenu,
                    onDismissRequest = { showConvMenu = false },
                    modifier = Modifier.background(NavyCard)
                ) {
                    conversations.forEach { conv ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = conv.title,
                                    color = if (conv.id == selectedConvId) ElectricBlue else TextPrimary
                                )
                            },
                            onClick = {
                                viewModel.selectConversation(conv.id)
                                showConvMenu = false
                            }
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // New Chat Button
                IconButton(
                    onClick = {
                        viewModel.createNewConversation("Chat ${conversations.size + 1}", selectedProject?.id)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("chat_new_conv_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Conversation",
                        tint = TextPrimary
                    )
                }

                // Delete current conversation
                if (conversations.size > 1 && selectedConvId != null) {
                    IconButton(
                        onClick = {
                            selectedConvId?.let { id ->
                                viewModel.deleteConversation(id)
                                Toast.makeText(context, "Conversation deleted", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Conversation",
                            tint = TextMuted
                        )
                    }
                }
            }
        }

        // Agent Status Banner
        AnimatedVisibility(visible = agentState.status != AgentStatus.IDLE && agentState.status != AgentStatus.COMPLETED) {
            val statusBg = when (agentState.status) {
                AgentStatus.APPROVAL_REQUIRED -> WarningAmber.copy(alpha = 0.15f)
                AgentStatus.FAILED -> DangerRose.copy(alpha = 0.15f)
                else -> ElectricBlue.copy(alpha = 0.15f)
            }
            val statusColor = when (agentState.status) {
                AgentStatus.APPROVAL_REQUIRED -> WarningAmber
                AgentStatus.FAILED -> DangerRose
                else -> ElectricBlue
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusBg)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (agentState.status == AgentStatus.THINKING || agentState.status == AgentStatus.GENERATING || agentState.status == AgentStatus.EXECUTING_TOOL) {
                        CircularProgressIndicator(
                            color = statusColor,
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp
                        )
                    } else if (agentState.status == AgentStatus.APPROVAL_REQUIRED) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = agentState.currentStep.ifEmpty { agentState.status.label },
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (agentState.status == AgentStatus.GENERATING) {
                    IconButton(
                        onClick = { viewModel.stopGeneration() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = DangerRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Missing API Key Guidance Banner
        if (!isAiConfigured) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WarningAmber.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "API key not set",
                        tint = WarningAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI response service: Gemini key not configured. Add your key in Settings or AI Studio Secrets.",
                        color = WarningAmber,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
        ) {
            items(messages) { message ->
                ChatMessageBubble(message = message)
            }
        }

        // Quick Suggestions Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                ChatSuggestionChip(
                    text = "List projects",
                    onClick = { inputPrompt = "List my projects" }
                )
            }
            item {
                ChatSuggestionChip(
                    text = "Create project: NextGen Agent",
                    onClick = { inputPrompt = "Create project: NextGen Autonomous Agent" }
                )
            }
            item {
                ChatSuggestionChip(
                    text = "Create action card: Security review",
                    onClick = { inputPrompt = "Create action card: Review security vault and KMS tokens" }
                )
            }
            item {
                ChatSuggestionChip(
                    text = "What is BUILDLABS?",
                    onClick = { inputPrompt = "Explain the BUILDLABS platform and autonomous capabilities." }
                )
            }
        }

        // Composer Row: Input + Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyCard)
                .border(0.5.dp, NavyBorder)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment Placeholder
            IconButton(
                onClick = {
                    Toast.makeText(context, "Attachment support active: Images, PDF, Docs supported in Phase 3", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach file",
                    tint = TextMuted
                )
            }

            // Input Text Field
            OutlinedTextField(
                value = inputPrompt,
                onValueChange = { inputPrompt = it },
                placeholder = {
                    Text(
                        text = "Ask BUILDLABS or run an action...",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = ElectricBlue,
                    focusedContainerColor = NavyDark,
                    unfocusedContainerColor = NavyDark
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 4,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .testTag("chat_input_textfield")
            )

            // Send Button or Stop Button
            if (agentState.status == AgentStatus.GENERATING) {
                IconButton(
                    onClick = { viewModel.stopGeneration() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DangerRose.copy(alpha = 0.2f))
                        .testTag("chat_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = DangerRose
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        val text = inputPrompt.trim()
                        if (text.isNotEmpty()) {
                            viewModel.sendMessage(text)
                            inputPrompt = ""
                        }
                    },
                    enabled = inputPrompt.trim().isNotEmpty(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (inputPrompt.trim().isNotEmpty()) ElectricBlue else NavySurfaceVariant)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputPrompt.trim().isNotEmpty()) Color.White else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun ChatSuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(NavySurfaceVariant)
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
