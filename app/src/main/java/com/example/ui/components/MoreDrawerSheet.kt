package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.Screen

data class ModuleItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val targetScreen: Screen,
    val statusTag: String,
    val isOperational: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreDrawerSheet(
    onDismiss: () -> Unit,
    onSelectScreen: (Screen) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val modules = listOf(
        ModuleItem("Home", "Personalized dashboard", Icons.Default.Home, Screen.Home, "Phase 1 • Live", true),
        ModuleItem("Chat", "AI Brain & Agent", Icons.Default.Forum, Screen.Chat, "Phase 2 • Live", true),
        ModuleItem("Projects", "Dedicated workspaces", Icons.Default.Folder, Screen.Projects, "Phase 1 • Live", true),
        ModuleItem("Actions", "Action Cards & approvals", Icons.Default.Checklist, Screen.Actions, "Phase 1 • Live", true),
        ModuleItem("Panels", "Mini-application builder", Icons.Default.Dashboard, Screen.Panels, "Phase 6 Planned", false),
        ModuleItem("Specialties", "Reusable workflows", Icons.Default.Extension, Screen.Specialties, "Phase 6 Planned", false),
        ModuleItem("Scheduled Tasks", "Automations & queue", Icons.Default.Schedule, Screen.Automations, "Phase 7 Planned", false),
        ModuleItem("Integrations", "Google & Microsoft OAuth", Icons.Default.Hub, Screen.Integrations, "Phase 4 Planned", false),
        ModuleItem("Cloud Handoff", "Isolated browser agent", Icons.Default.Computer, Screen.Handoff, "Phase 5 Planned", false),
        ModuleItem("Security Vault", "Encrypted credentials", Icons.Default.Lock, Screen.Vault, "Phase 8 Planned", false),
        ModuleItem("Notifications", "System & task alerts", Icons.Default.Notifications, Screen.Notifications, "Live", true),
        ModuleItem("Settings", "Gemini API key & Profile", Icons.Default.Settings, Screen.Settings, "Config", true)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NavyDark,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BUILDLABS PLATFORM MODULES",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "12 Architecture Subsystems per Master Blueprint",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(modules) { mod ->
                    ModuleGridCard(
                        module = mod,
                        onClick = {
                            onDismiss()
                            onSelectScreen(mod.targetScreen)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ModuleGridCard(
    module: ModuleItem,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("module_item_${module.title.lowercase().replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (module.isOperational) ElectricBlue.copy(alpha = 0.15f) else NavySurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = module.title,
                        tint = if (module.isOperational) ElectricBlue else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (module.isOperational) SuccessGreen.copy(alpha = 0.15f) else NavySurfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = module.statusTag,
                        color = if (module.isOperational) SuccessGreen else TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = module.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = module.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}
