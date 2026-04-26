package com.wrapper.composechat.feature.maindashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.resources.*
import com.wrapper.composechat.ui.theme.Glassmorphism
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ChatsAccessSheetContent(
    onContinue: () -> Unit,
) {
    val family = LocalVfvDisplayFontFamily.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.frosted_chats_title),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = family,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.frosted_chats_body),
            color = Color.White.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontFamily = family,
        )
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Glassmorphism.primaryActionBrush)
                .clickable(onClick = onContinue),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.frosted_chats_continue),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontFamily = family,
            )
        }
    }
}
