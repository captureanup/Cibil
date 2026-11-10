package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BuildlabsBottomNav
import com.example.ui.components.BuildlabsTopBar
import com.example.ui.components.MoreDrawerSheet
import com.example.ui.components.ToolApprovalDialog
import com.example.ui.screens.ActionsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.AutomationsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HandoffScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IntegrationsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PanelsScreen
import com.example.ui.screens.ProjectDashboard
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SpecialtiesScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.BuildlabsTheme
import com.example.ui.theme.NavyDark
import com.example.ui.viewmodel.BuildlabsViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildlabsTheme {
                BuildlabsApp()
            }
        }
    }
}

@Composable
fun BuildlabsApp(
    viewModel: BuildlabsViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val agentState by viewModel.agentState.collectAsStateWithLifecycle()
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()

    val isAiConfigured = viewModel.geminiProvider.isConfigured(userPrefs?.geminiApiKey)

    var showMoreSheet by remember { mutableStateOf(false) }

    // System Back button handling
    BackHandler(enabled = currentScreen != Screen.Home) {
        viewModel.navigateTo(Screen.Home)
    }

    Scaffold(
        topBar = {
            BuildlabsTopBar(
                currentScreen = currentScreen,
                selectedProject = selectedProject,
                isAiConfigured = isAiConfigured,
                onNavigateBack = { viewModel.navigateTo(Screen.Home) },
                onOpenProfile = { viewModel.navigateTo(Screen.Auth) },
                onSelectProject = { viewModel.navigateTo(Screen.Projects) }
            )
        },
        bottomBar = {
            BuildlabsBottomNav(
                currentScreen = currentScreen,
                onNavigate = { screen -> viewModel.navigateTo(screen) },
                onOpenMoreDrawer = { showMoreSheet = true }
            )
        },
        containerColor = NavyDark,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDark)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(viewModel = viewModel)
                Screen.Chat -> ChatScreen(viewModel = viewModel)
                Screen.Projects -> ProjectDashboard(viewModel = viewModel)
                Screen.Actions -> ActionsScreen(viewModel = viewModel)
                Screen.Panels -> PanelsScreen()
                Screen.Specialties -> SpecialtiesScreen()
                Screen.Automations -> AutomationsScreen()
                Screen.Integrations -> IntegrationsScreen()
                Screen.Handoff -> HandoffScreen()
                Screen.Vault -> VaultScreen()
                Screen.Notifications -> NotificationsScreen()
                Screen.Settings -> SettingsScreen(viewModel = viewModel)
                Screen.Auth -> AuthScreen(viewModel = viewModel)
            }

            // Gated High-Risk Tool Approval Dialog
            if (agentState.pendingApprovalCall != null) {
                val call = agentState.pendingApprovalCall!!
                ToolApprovalDialog(
                    toolCall = call,
                    onApprove = { viewModel.approvePendingTool() },
                    onReject = { viewModel.rejectPendingTool() }
                )
            }

            // More Modules Modal Bottom Sheet
            if (showMoreSheet) {
                MoreDrawerSheet(
                    onDismiss = { showMoreSheet = false },
                    onSelectScreen = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        }
    }
}
