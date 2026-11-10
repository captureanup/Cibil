package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.Screen

@Composable
fun BuildlabsBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onOpenMoreDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = NavyBorder)
            .background(NavyDark)
            .navigationBarsPadding(),
        containerColor = NavyDark,
        tonalElevation = 8.dp
    ) {
        // 1. Home
        val isHome = currentScreen == Screen.Home
        NavigationBarItem(
            selected = isHome,
            onClick = { onNavigate(Screen.Home) },
            icon = {
                Icon(
                    imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = if (isHome) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ElectricBlue,
                selectedTextColor = ElectricBlue,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NavySurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        // 2. Chat
        val isChat = currentScreen == Screen.Chat
        NavigationBarItem(
            selected = isChat,
            onClick = { onNavigate(Screen.Chat) },
            icon = {
                Icon(
                    imageVector = if (isChat) Icons.Filled.Forum else Icons.Outlined.Forum,
                    contentDescription = "Chat",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "Chat",
                    fontSize = 11.sp,
                    fontWeight = if (isChat) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ElectricBlue,
                selectedTextColor = ElectricBlue,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NavySurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_chat")
        )

        // 3. Projects
        val isProjects = currentScreen == Screen.Projects
        NavigationBarItem(
            selected = isProjects,
            onClick = { onNavigate(Screen.Projects) },
            icon = {
                Icon(
                    imageVector = if (isProjects) Icons.Filled.Folder else Icons.Outlined.Folder,
                    contentDescription = "Projects",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "Projects",
                    fontSize = 11.sp,
                    fontWeight = if (isProjects) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ElectricBlue,
                selectedTextColor = ElectricBlue,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NavySurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_projects")
        )

        // 4. Actions
        val isActions = currentScreen == Screen.Actions
        NavigationBarItem(
            selected = isActions,
            onClick = { onNavigate(Screen.Actions) },
            icon = {
                Icon(
                    imageVector = if (isActions) Icons.Filled.Checklist else Icons.Outlined.Checklist,
                    contentDescription = "Actions",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "Actions",
                    fontSize = 11.sp,
                    fontWeight = if (isActions) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ElectricBlue,
                selectedTextColor = ElectricBlue,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NavySurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_actions")
        )

        // 5. More Modules
        NavigationBarItem(
            selected = false,
            onClick = onOpenMoreDrawer,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.MoreHoriz,
                    contentDescription = "More Modules",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "More",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = TextPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = NavySurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_more")
        )
    }
}
