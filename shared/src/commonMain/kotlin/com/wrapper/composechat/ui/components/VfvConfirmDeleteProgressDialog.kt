package com.wrapper.composechat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.delete_progress_cancel
import com.wrapper.composechat.resources.delete_progress_confirm
import com.wrapper.composechat.resources.delete_progress_message
import com.wrapper.composechat.resources.delete_progress_title
import com.wrapper.composechat.ui.liquidglass.vfvLiquidGlass
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.stringResource

private val AlertShape = RoundedCornerShape(28.dp)
private val AlertSurface = Color.White.copy(alpha = 0.16f)
private val AlertBorder = Color.White.copy(alpha = 0.20f)
private val AlertScrim = Color(0xFF09001F).copy(alpha = 0.55f)
private val AlertDivider = Color.White.copy(alpha = 0.16f)
private val AlertDestructive = Color(0xFFFF6B9A)
private const val AlertGlassBlurDp = 20f

/**
 * Liquid-glass confirm alert for deleting progress. Samples [com.wrapper.composechat.ui.liquidglass.LocalVfvScreenBackdrop].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VfvConfirmDeleteProgressDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.94f),
        exit = fadeOut(tween(140)) + scaleOut(tween(140), targetScale = 0.96f),
        modifier = modifier
            .fillMaxSize()
            .zIndex(20_000f),
    ) {
        BackHandler(onBack = onDismiss)
        val family = LocalVfvDisplayFontFamily.current
        val dismissTap = remember { MutableInteractionSource() }
        val consumeTap = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AlertScrim)
                .clickable(
                    interactionSource = dismissTap,
                    indication = null,
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .clip(AlertShape)
                    .clickable(
                        interactionSource = consumeTap,
                        indication = null,
                        onClick = {},
                    )
                    .vfvLiquidGlass(
                        blurRadiusDp = AlertGlassBlurDp,
                        shape = AlertShape,
                        surfaceColor = AlertSurface,
                    )
                    .border(0.5.dp, AlertBorder, AlertShape),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.delete_progress_title),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp),
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = family,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(Res.string.delete_progress_message),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 18.dp),
                    color = Color.White.copy(alpha = 0.60f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = family,
                    textAlign = TextAlign.Center,
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(AlertDivider),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.delete_progress_cancel),
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = family,
                        )
                    }
                    Box(
                        Modifier
                            .width(0.5.dp)
                            .height(48.dp)
                            .background(AlertDivider),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable(onClick = onConfirm),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.delete_progress_confirm),
                            color = AlertDestructive,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = family,
                        )
                    }
                }
            }
        }
    }
}
