package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.ActionCardEntity
import com.example.data.local.entity.ProjectEntity
import com.example.ui.components.ActionCardItem
import com.example.ui.components.BuildlabsHeaderLogo
import com.example.ui.theme.CyanAccent
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
import com.example.ui.viewmodel.BuildlabsViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: BuildlabsViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()
    val actionCards by viewModel.activeActionCards.collectAsStateWithLifecycle()
    val conversations by viewModel.allConversations.collectAsStateWithLifecycle()
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()

    val isAiConfigured = viewModel.geminiProvider.isConfigured(userPrefs?.geminiApiKey)

    // Compute dynamic greeting based on hour of day
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingPrefix = when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
    val displayName = userProfile?.displayName?.ifBlank { "Alex" } ?: "Alex"
    val dateStr = SimpleDateFormat("EEEE, MMMM d • h:mm a", Locale.getDefault()).format(Date())

    var showCreateProjectDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // 1. Personalized Greeting & Local Date/Time
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "$greetingPrefix, $displayName",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 2. Main AI Assistant Entry Point Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(ElectricBlue.copy(alpha = 0.5f), NeonViolet.copy(alpha = 0.3f))),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { viewModel.navigateTo(Screen.Chat) }
                    .testTag("home_ai_entry_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Brain",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "BUILDLABS ORCHESTRATOR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = ElectricBlue
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAiConfigured) SuccessGreen.copy(alpha = 0.15f) else CyanAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isAiConfigured) "Gemini Live" else "Phase 2 Brain",
                                color = if (isAiConfigured) SuccessGreen else CyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "What would you like to build or orchestrate today?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Autonomous tool execution, multi-turn reasoning, and context-aware workspace workflows.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tap to open chat workspace",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Chat) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("home_launch_chat_button")
                        ) {
                            Text("Launch Chat", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. Quick Action Chips
        item {
            Text(
                text = "SUGGESTED ACTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                item {
                    QuickPromptChip(
                        text = "List my projects",
                        onClick = {
                            viewModel.navigateTo(Screen.Chat)
                            viewModel.sendMessage("List my projects")
                        }
                    )
                }
                item {
                    QuickPromptChip(
                        text = "Create new action card",
                        onClick = {
                            viewModel.navigateTo(Screen.Chat)
                            viewModel.sendMessage("Create action card: Verify database migration and security rules")
                        }
                    )
                }
                item {
                    QuickPromptChip(
                        text = "Show my profile",
                        onClick = {
                            viewModel.navigateTo(Screen.Chat)
                            viewModel.sendMessage("Show my profile")
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Active Projects Header & List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE PROJECTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )

                Text(
                    text = "View All (${projects.size})",
                    fontSize = 12.sp,
                    color = ElectricBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Projects) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (projects.isEmpty()) {
                EmptySectionPlaceholder(
                    icon = Icons.Default.Folder,
                    message = "No projects yet. Create your first project to organize chats and tasks."
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(projects.take(4)) { project ->
                        HomeProjectMiniCard(
                            project = project,
                            onClick = {
                                viewModel.selectProject(project)
                                viewModel.createNewConversation("Chat: ${project.name}", project.id)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 5. Action Cards Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTION CARDS & APPROVALS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )

                Text(
                    text = "Manage",
                    fontSize = 12.sp,
                    color = ElectricBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Actions) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        val pendingCards = actionCards.filter { it.status == "Pending" }
        if (pendingCards.isEmpty()) {
            item {
                EmptySectionPlaceholder(
                    icon = Icons.Default.CheckCircle,
                    message = "All action cards completed. The agent will suggest new items as needed."
                )
            }
        } else {
            items(pendingCards.take(3)) { card ->
                ActionCardItem(
                    card = card,
                    onComplete = { viewModel.completeActionCard(card) },
                    onDismiss = { viewModel.dismissActionCard(card) },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }

        // 6. System Architecture & Foundation Status Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "System Health",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BUILDLABS FOUNDATION & ARCHITECTURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Local SQLite Room Database: Operational (Active)\n• Gemini 2.5/3.8 Flash Gateway: ${if (isAiConfigured) "Connected" else "Key Required in Settings"}\n• Autonomous Agent Orchestration: Multi-turn + Tool Gating\n• Cloud Computer (Playwright Handoff): Phase 5 Scheduled",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun QuickPromptChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HomeProjectMiniCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .width(220.dp)
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Project",
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = project.status,
                        color = SuccessGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = project.description.ifEmpty { "No description provided." },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
fun EmptySectionPlaceholder(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            )
        }
    }
}
