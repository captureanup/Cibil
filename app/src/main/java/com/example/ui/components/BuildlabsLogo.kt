package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun BuildlabsLogoBadge(
    modifier: Modifier = Modifier,
    size: Int = 36
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        ElectricBlue.copy(alpha = 0.25f),
                        NeonViolet.copy(alpha = 0.35f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(ElectricBlue, NeonViolet)),
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "B",
            fontWeight = FontWeight.Black,
            fontSize = (size * 0.55).sp,
            color = Color.White,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun BuildlabsHeaderLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BuildlabsLogoBadge(size = 38)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "BUILD",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "LABS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = ElectricBlue
                    )
                )
            }
            if (showTagline) {
                Text(
                    text = "Intelligence That Gets Things Done",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
