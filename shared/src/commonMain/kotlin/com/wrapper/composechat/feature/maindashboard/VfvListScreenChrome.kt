package com.wrapper.composechat.feature.maindashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.platform.optionalBackdropBlur
import com.wrapper.composechat.ui.components.VfvChromeBackIconButton
import com.wrapper.composechat.ui.components.VfvChromeTrashIconButton
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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
        VfvChromeBackIconButton(onClick = onBack)
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
        VfvChromeTrashIconButton(onClick = onTrash)
    }
}
