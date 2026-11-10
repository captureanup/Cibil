package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ProjectEntity
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.Screen

@Composable
fun BuildlabsTopBar(
    currentScreen: Screen,
    selectedProject: ProjectEntity?,
    isAiConfigured: Boolean,
    onNavigateBack: () -> Unit,
    onOpenProfile: () -> Unit,
    onSelectProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showBack = currentScreen != Screen.Home && currentScreen != Screen.Chat && currentScreen != Screen.Projects

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NavyDark)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("topbar_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            BuildlabsLogoBadge(size = 32)
            Spacer(modifier = Modifier.width(10.dp))
        }

        // Title and screen subtitle
        Text(
            text = currentScreen.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            modifier = Modifier.weight(1f)
        )

        // Project chip if selected
        if (selectedProject != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
                    .clickable { onSelectProject() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Active Project",
                    tint = ElectricBlue,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedProject.name.take(12) + if (selectedProject.name.length > 12) "..." else "",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // AI Status Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isAiConfigured) SuccessGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = if (isAiConfigured) "AI Live" else "AI Setup Needed",
                tint = if (isAiConfigured) SuccessGreen else WarningAmber,
                modifier = Modifier.size(8.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isAiConfigured) "Brain Live" else "Setup Key",
                color = if (isAiConfigured) SuccessGreen else WarningAmber,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Profile Avatar Button
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(NavySurfaceVariant)
                .border(1.dp, NavyBorder, CircleShape)
                .clickable { onOpenProfile() }
                .testTag("topbar_profile_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Profile",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
