package com.wrapper.composechat.feature.maindashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.platform.optionalBackdropBlur
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.main_dashboard_settings
import com.wrapper.composechat.resources.nav_back
import com.wrapper.composechat.resources.trash
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal const val VfvListBackdropBlurRadiusDp = 38f
internal const val VfvListCardCompleteBlurRadiusDp = 20f

@Composable
internal fun VfvListScreenBackdrop(
    heroDrawable: DrawableResource,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(heroDrawable),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .optionalBackdropBlur(VfvListBackdropBlurRadiusDp),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF3A0084).copy(alpha = 0.60f),
                            0.4f to Color(0xFF09001F).copy(alpha = 0.80f),
                            1f to Color(0xFF09001F).copy(alpha = 0.90f),
                        ),
                    ),
                ),
        )
    }
}

@Composable
internal fun VfvListChromeTopBar(
    title: String,
    onBack: () -> Unit,
    onTrash: () -> Unit,
    family: FontFamily?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VfvListChromeIconButton(
            onClick = onBack,
            desc = stringResource(Res.string.nav_back),
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = family,
            letterSpacing = 1.2.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        VfvListChromeIconButton(
            onClick = onTrash,
            desc = stringResource(Res.string.main_dashboard_settings),
        ) {
            Icon(
                painter = painterResource(Res.drawable.trash),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
internal fun VfvListChromeIconButton(
    onClick: () -> Unit,
    desc: String,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .semantics { contentDescription = desc }
            .size(32.dp)
            .clip(RoundedCornerShape(corner = CornerSize(12.dp)))
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(corner = CornerSize(12.dp)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
