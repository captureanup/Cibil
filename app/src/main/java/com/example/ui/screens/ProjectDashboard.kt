package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.ProjectEntity
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
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectDashboard(
    viewModel: BuildlabsViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()
    val conversations by viewModel.allConversations.collectAsStateWithLifecycle()
    val actionCards by viewModel.activeActionCards.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<ProjectEntity?>(null) }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Active", "Archived"

    val activeCount = projects.count { it.status == "Active" }
    val archivedCount = projects.count { it.status == "Archived" }
    val pendingActionCount = actionCards.count { it.status == "Pending" }

    val filteredProjects = projects.filter {
        when (selectedFilter) {
            "Active" -> it.status == "Active"
            "Archived" -> it.status == "Archived"
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ElectricBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("dashboard_create_project_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Initiative"
                )
            }
        },
        containerColor = NavyDark,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NavyDark)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            // 1. Executive Summary & Status Metric Cards (2x2 Grid Layout)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PROJECT WORKSPACE DASHBOARD",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Active initiatives, AI context mapping & milestone health",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SuccessGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Live Sync",
                            color = SuccessGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Metric KPI Cards Grid (Row 1: Total & Active Projects)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardMetricCard(
                        title = "Total Initiatives",
                        count = projects.size.toString(),
                        subtitle = "${projects.size} in workspace",
                        icon = Icons.Default.Layers,
                        accentColor = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardMetricCard(
                        title = "Active Projects",
                        count = activeCount.toString(),
                        subtitle = "$archivedCount archived",
                        icon = Icons.Default.TrendingUp,
                        accentColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Metric KPI Cards Grid (Row 2: Conversations & Actions)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardMetricCard(
                        title = "Dedicated Chats",
                        count = conversations.size.toString(),
                        subtitle = "Context isolated",
                        icon = Icons.Default.Forum,
                        accentColor = NeonViolet,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardMetricCard(
                        title = "Pending Actions",
                        count = pendingActionCount.toString(),
                        subtitle = "Awaiting review",
                        icon = Icons.Default.PlaylistAddCheck,
                        accentColor = if (pendingActionCount > 0) WarningAmber else CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. Development Roadmap / Milestone Health Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(ElectricBlue.copy(alpha = 0.4f), NeonViolet.copy(alpha = 0.2f))),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                    .background(CyanAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timeline,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "PLATFORM MILESTONE STATUS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = CyanAccent
                                )
                            }
                            Text(
                                text = "Phases 0-2 Active",
                                color = SuccessGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { 0.70f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ElectricBlue,
                            trackColor = NavySurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MilestonePill("P0: Foundation", true)
                            MilestonePill("P1: Core App", true)
                            MilestonePill("P2: AI Brain", true)
                            MilestonePill("P3: Memory", false)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 3. Quick AI Summarization & Action Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MANAGE INITIATIVES (${filteredProjects.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )

                    // Quick AI Status Summarize button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricBlue.copy(alpha = 0.15f))
                            .clickable {
                                viewModel.navigateTo(Screen.Chat)
                                viewModel.sendMessage("Summarize the current development status and key milestones of my active projects.")
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("ai_summarize_projects_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Status Summary",
                            color = ElectricBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Active", "Archived").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ElectricBlue.copy(alpha = 0.2f) else NavySurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricBlue else NavyBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("dashboard_filter_$filter")
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) ElectricBlue else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 4. Project Items List
            if (filteredProjects.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (selectedFilter == "All") "No projects in workspace" else "No $selectedFilter projects found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create a project initiative to structure AI conversations, tasks, and memory context.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showCreateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                            ) {
                                Text("New Project Initiative", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(filteredProjects) { project ->
                    DashboardProjectCard(
                        project = project,
                        onOpenChat = {
                            viewModel.selectProject(project)
                            viewModel.createNewConversation("Chat: ${project.name}", project.id)
                        },
                        onToggleArchive = {
                            val newStatus = if (project.status == "Archived") "Active" else "Archived"
                            viewModel.createProject(project.name, project.description)
                            Toast.makeText(context, "Initiative marked as $newStatus", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = { projectToDelete = project }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    // Create Project Dialog
    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, desc ->
                viewModel.createProject(name, desc)
                showCreateDialog = false
                Toast.makeText(context, "Initiative '$name' created successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Confirmation Dialog
    if (projectToDelete != null) {
        val proj = projectToDelete!!
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = {
                Text(
                    text = "Delete Initiative?",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Permanently delete '${proj.name}'? Associated threads and action items will be detached.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProject(proj.id)
                        projectToDelete = null
                        Toast.makeText(context, "Initiative deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRose),
                    modifier = Modifier.testTag("confirm_delete_initiative_button")
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { projectToDelete = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = NavyCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DashboardMetricCard(
    title: String,
    count: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun MilestonePill(label: String, isComplete: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (isComplete) SuccessGreen else TextMuted)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isComplete) TextPrimary else TextMuted,
            fontWeight = if (isComplete) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun DashboardProjectCard(
    project: ProjectEntity,
    onOpenChat: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(project.updatedAt))

    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
            .testTag("dashboard_project_card_${project.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Status, Health & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (project.status == "Active") SuccessGreen.copy(alpha = 0.15f)
                                else TextMuted.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = project.status,
                            color = if (project.status == "Active") SuccessGreen else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricBlue.copy(alpha = 0.12f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Agent Linked",
                                color = ElectricBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onToggleArchive,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archive",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = DangerRose.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Project Title
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = project.description.ifEmpty { "Autonomous agent project workspace." },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Footer: Updated Date & Dedicated Chat Launch Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Updated $dateStr",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Button(
                    onClick = onOpenChat,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("dashboard_open_chat_${project.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Project Chat",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
