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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ActionCardItem
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BuildlabsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ActionsScreen(
    viewModel: BuildlabsViewModel,
    modifier: Modifier = Modifier
) {
    val actionCards by viewModel.activeActionCards.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf("Pending") } // Pending, Completed, All
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredCards = actionCards.filter {
        when (selectedFilter) {
            "Pending" -> it.status == "Pending"
            "Completed" -> it.status == "Completed"
            "Dismissed" -> it.status == "Dismissed"
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ElectricBlue,
                contentColor = TextPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("create_action_card_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Action Card")
            }
        },
        containerColor = NavyDark,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NavyDark)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle & Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${actionCards.size} ACTION ITEMS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Pending", "Completed", "All").forEach { filter ->
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
                                .padding(horizontal = 8.dp, vertical = 4.dp)
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
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedFilter == "Pending") "No pending action cards" else "No $selectedFilter action cards",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Actions proposed by the agent or manually created appear here.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredCards) { card ->
                        ActionCardItem(
                            card = card,
                            onComplete = {
                                viewModel.completeActionCard(card)
                                Toast.makeText(context, "Action completed", Toast.LENGTH_SHORT).show()
                            },
                            onDismiss = {
                                viewModel.dismissActionCard(card)
                                Toast.makeText(context, "Action dismissed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateActionCardDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, desc, prio, deadline ->
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.repository.createActionCard(
                        title = title,
                        description = desc,
                        priority = prio,
                        deadline = deadline,
                        source = "User Action"
                    )
                }
                showCreateDialog = false
                Toast.makeText(context, "Action Card created!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun CreateActionCardDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, priority: String, deadline: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("High") }
    var deadline by remember { mutableStateOf("Today") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Action Card",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Action Details") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Deadline / Timeframe") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.trim().isNotEmpty()) {
                        onCreate(title.trim(), desc.trim(), priority, deadline.trim())
                    }
                },
                enabled = title.trim().isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Create Action", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        },
        containerColor = NavyCard,
        shape = RoundedCornerShape(16.dp)
    )
}
