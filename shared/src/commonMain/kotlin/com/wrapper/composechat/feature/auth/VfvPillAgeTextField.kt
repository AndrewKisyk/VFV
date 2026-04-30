package com.wrapper.composechat.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.wrapper.composechat.ui.theme.ChatColors
import com.wrapper.composechat.ui.theme.ChatDimens

// Leading check: light top → dark bottom
private val vfvAgeCheckGradient = Brush.verticalGradient(
    0f to Color(0xFF9B6AEC),
    1f to Color(0xFF4A1B8A),
)

/**
 * Material 3 [OutlinedTextField] (floating label), pill shape, and a leading check **only** when
 * [showValidCheck] (valid age 12–17).
 */
@Composable
fun VfvPillAgeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    isError: Boolean,
    errorText: String?,
    showValidCheck: Boolean,
    modifier: Modifier = Modifier,
) {
    val errorColor = MaterialTheme.colorScheme.error
    val shape = RoundedCornerShape(ChatDimens.vfvAuthPillCorner)
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedTextColor = ChatColors.onContent,
        unfocusedTextColor = ChatColors.onContent,
        disabledTextColor = ChatColors.onContentMuted,
        errorTextColor = ChatColors.onContent,
        focusedBorderColor = ChatColors.primary,
        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
        errorBorderColor = errorColor,
        cursorColor = ChatColors.primary,
        focusedLabelColor = ChatColors.onContentMuted,
        unfocusedLabelColor = ChatColors.onContentMuted,
        errorLabelColor = errorColor,
        errorSupportingTextColor = errorColor,
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = ChatDimens.vfvAuthPillFieldHeight),
        label = { Text(text = labelText) },
        isError = isError,
        singleLine = true,
        shape = shape,
        colors = colors,
        leadingIcon = if (showValidCheck) {
            {
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(vfvAgeCheckGradient, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White,
                    )
                }
            }
        } else {
            null
        },
        supportingText = {
            if (isError && errorText != null) {
                Text(
                    text = errorText,
                    color = errorColor,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
