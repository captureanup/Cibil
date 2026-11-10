package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MessageEntity
import com.example.ui.theme.DangerRose
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMessageBubble(
    message: MessageEntity,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == "user"
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.createdAt))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("chat_message_${message.id}"),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            BuildlabsLogoBadge(size = 32)
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            modifier = Modifier.weight(1f, fill = false),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Header: role + timestamp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 3.dp)
            ) {
                Text(
                    text = if (isUser) "You" else "BUILDLABS Agent",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) ElectricBlue else NeonViolet
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
            }

            // Message Body Box
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(if (isUser) ElectricBlue.copy(alpha = 0.2f) else NavyCard)
                    .border(
                        1.dp,
                        if (isUser) ElectricBlue.copy(alpha = 0.4f) else NavyBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Column {
                    // Tool tag if applicable
                    if (message.toolName != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NavySurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Tool",
                                tint = ElectricBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tool: ${message.toolName}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElectricBlue
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Content
                    if (message.content.contains("```")) {
                        FormattedCodeMessage(text = message.content)
                    } else {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                lineHeight = 22.sp
                            )
                        )
                    }

                    // Status warning if failed or approval required
                    if (message.status == "approval_required") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WarningAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Approval required",
                                tint = WarningAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Action awaiting authorization",
                                color = WarningAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else if (message.status == "failed") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Generation error",
                            color = DangerRose,
                            fontSize = 11.sp
                        )
                    }

                    // Bottom action row for assistant
                    if (!isUser && message.content.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(message.content))
                                    Toast.makeText(context, "Copied response to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy message",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun FormattedCodeMessage(text: String) {
    val parts = text.split("```")
    Column {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Code block
                val lines = part.lines()
                val lang = lines.firstOrNull()?.trim().orEmpty()
                val codeContent = if (lines.size > 1) lines.drop(1).joinToString("\n").trim() else part.trim()
                val clipboard = LocalClipboardManager.current
                val ctx = LocalContext.current

                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.ifEmpty { "CODE" }.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            IconButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(codeContent))
                                    Toast.makeText(ctx, "Copied code", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = codeContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8),
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                if (part.trim().isNotEmpty()) {
                    Text(
                        text = part.trim(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            lineHeight = 22.sp
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
