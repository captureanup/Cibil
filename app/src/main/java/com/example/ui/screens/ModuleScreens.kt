package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun PlannedModuleScreen(
    title: String,
    phaseTag: String,
    description: String,
    icon: ImageVector,
    architectureDetails: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = ElectricBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarningAmber.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = phaseTag,
                            color = WarningAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavySurfaceVariant)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phase boundary status: In accordance with the BUILDLABS Blueprint, this module is specified and architected for its target phase. Buttons and interactive workflows will become active upon deployment of the corresponding background workers.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ARCHITECTURE & TECHNICAL SPECIFICATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                architectureDetails.forEach { detail ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// 5. Panels Screen
@Composable
fun PanelsScreen(modifier: Modifier = Modifier) {
    PlannedModuleScreen(
        title = "Custom Panels & Mini-Apps",
        phaseTag = "PHASE 6 PLANNED",
        description = "Natural language component renderer allowing users to request custom mini-applications such as Daily Planners, Expense Trackers, and Habit Dashboards.",
        icon = Icons.Default.Dashboard,
        architectureDetails = listOf(
            "Natural Language Request -> JSON Component Schema Validation",
            "Secure approved component renderer (Cards, Tables, Charts, Buttons)",
            "Strict policy: No execution of arbitrary model-generated JavaScript",
            "Persistence in Supabase panel_data and user_panels tables"
        ),
        modifier = modifier
    )
}

// 6. Specialties Screen
@Composable
fun SpecialtiesScreen(modifier: Modifier = Modifier) {
    PlannedModuleScreen(
        title = "Specialties / Reusable Workflows",
        phaseTag = "PHASE 6 PLANNED",
        description = "Repeatable multi-step autonomous workflows with input variables, permission gates, and execution logging.",
        icon = Icons.Default.Extension,
        architectureDetails = listOf(
            "Workflow definition schema with input parameters and step sequences",
            "Tool and permission gating evaluated dynamically at execution time",
            "Pre-built templates: Weekly Research, Email Prep, Project Status Summary",
            "Versioned specialty registry in specialties and specialty_versions tables"
        ),
        modifier = modifier
    )
}

// 7. Automations / Scheduled Tasks Screen
@Composable
fun AutomationsScreen(modifier: Modifier = Modifier) {
    PlannedModuleScreen(
        title = "Scheduled Tasks & Automations",
        phaseTag = "PHASE 7 PLANNED",
        description = "Timezone-aware background task scheduler powered by Redis/Valkey queue and BullMQ workers.",
        icon = Icons.Default.Schedule,
        architectureDetails = listOf(
            "Independent worker process running outside the public HTTP request loop",
            "Cron expressions and one-time scheduled intervals with timezone precision",
            "Idempotency keys, dead-letter queue, and duplicate task prevention",
            "Firebase Cloud Messaging (FCM) push notification triggers on completion"
        ),
        modifier = modifier
    )
}

// 8. Integrations Screen
@Composable
fun IntegrationsScreen(modifier: Modifier = Modifier) {
    PlannedModuleScreen(
        title = "Integration Center",
        phaseTag = "PHASE 4 PLANNED",
        description = "Secure integrations marketplace connecting Google Workspace (Gmail, Calendar, Drive) and Microsoft 365.",
        icon = Icons.Default.Hub,
        architectureDetails = listOf(
            "OAuth 2.0 authorization-code flow with PKCE",
            "Least-privilege permission scopes with revocable tokens",
            "Refresh tokens stored encrypted on server; no raw credentials in chat",
            "Provider status verification: Real connections only, no simulated success"
        ),
        modifier = modifier
    )
}

// 9. Handoff Screen
@Composable
fun HandoffScreen(modifier: Modifier = Modifier) {
    PlannedModuleScreen(
        title = "Cloud Computer & Handoff Agent",
        phaseTag = "PHASE 5 PLANNED",
        description = "Isolated cloud browser environment (Chromium & Playwright) executing user-authorized web tasks and research.",
        icon = Icons.Default.Computer,
        architectureDetails = listOf(
            "Dedicated containerized browser sandbox per task with egress filtering",
            "Mandatory user approval required for high-risk actions (purchases, emails)",
            "User takeover mode when encountering CAPTCHAs, MFA, or private login",
            "Step-by-step screenshot capture, task verification, and activity logging"
        ),
        modifier = modifier
    )
}

// 10. Security Vault Screen
@Composable
fun VaultScreen(modifier: Modifier = Modifier) {
    PlannedModuleScreen(
        title = "Security Vault",
        phaseTag = "PHASE 8 PLANNED",
        description = "Encrypted storage for credentials, sensitive API keys, and private operational secrets.",
        icon = Icons.Default.Lock,
        architectureDetails = listOf(
            "KMS-managed master encryption keys with envelope encryption (DEK/KEK)",
            "Strict authorization checks: Decrypted credentials never enter AI prompts",
            "Per-record audit trails logging every access, rotation, and deletion",
            "Biometric / MFA authorization required before manual inspection"
        ),
        modifier = modifier
    )
}

// 11. Notifications Screen
@Composable
fun NotificationsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDark)
            .padding(16.dp)
    ) {
        Text(
            text = "SYSTEM & AGENT NOTIFICATIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BUILDLABS Agent Online",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Genesis workspace initialized. Room SQLite persistence active. Autonomous tool gating enabled.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}
