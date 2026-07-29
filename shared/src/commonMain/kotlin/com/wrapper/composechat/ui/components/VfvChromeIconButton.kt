package com.wrapper.composechat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.platform.isBackdropBlurAvailable
import com.wrapper.composechat.platform.optionalBackdropBlur
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.main_dashboard_settings
import com.wrapper.composechat.resources.nav_back
import com.wrapper.composechat.resources.requirements_close
import com.wrapper.composechat.resources.trash
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Figma: Background blur = 16. */
const val VfvChromeIconButtonBlurRadiusDp = 16f

private val VfvChromeIconButtonShape = RoundedCornerShape(12.dp)
/** Figma: padding 8 + icon 16 + padding 8. */
private val VfvChromeIconButtonPadding = 8.dp
private val VfvChromeIconButtonSize = 32.dp
private val VfvChromeIconSize = 16.dp
/** Figma selection: #FFFFFF 100%. */
private val VfvChromeIconTint = Color.White
/** Figma fill: #FFFFFF 20%. */
private val VfvChromeIconButtonFill = Color.White.copy(alpha = 0.20f)
/** Figma stroke: #FFFFFF 20%, inside, 0.5. */
private val VfvChromeIconButtonBorder = Color.White.copy(alpha = 0.20f)
private val VfvChromeIconButtonBorderWidth = 0.5.dp

/**
 * Frosted chrome icon control — shared by dashboard, list screens, and article detail.
 *
 * Figma: radius 12, padding 8, fill #FFFFFF 20%, stroke 0.5 inside #FFFFFF 20%, background blur 16.
 */
@Composable
fun VfvChromeIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .size(VfvChromeIconButtonSize)
            .clip(VfvChromeIconButtonShape)
            .clickable(enabled = enabled, onClick = onClick),
    ) {
        if (isBackdropBlurAvailable()) {
            Box(
                Modifier
                    .matchParentSize()
                    .optionalBackdropBlur(VfvChromeIconButtonBlurRadiusDp)
                    .background(VfvChromeIconButtonFill, VfvChromeIconButtonShape),
            )
        } else {
            Box(
                Modifier
                    .matchParentSize()
                    .background(VfvChromeIconButtonFill, VfvChromeIconButtonShape),
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .border(VfvChromeIconButtonBorderWidth, VfvChromeIconButtonBorder, VfvChromeIconButtonShape),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(VfvChromeIconButtonPadding),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
fun VfvChromeBackIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(Res.string.nav_back),
) {
    VfvChromeIconButton(
        onClick = onClick,
        contentDescription = contentDescription,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowDown,
            contentDescription = null,
            tint = VfvChromeIconTint,
            modifier = Modifier.size(VfvChromeIconSize),
        )
    }
}

@Composable
fun VfvChromeCloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(Res.string.requirements_close),
) {
    VfvChromeIconButton(
        onClick = onClick,
        contentDescription = contentDescription,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = VfvChromeIconTint,
            modifier = Modifier.size(VfvChromeIconSize),
        )
    }
}

@Composable
fun VfvChromeTrashIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(Res.string.main_dashboard_settings),
) {
    VfvChromeIconButton(
        onClick = onClick,
        contentDescription = contentDescription,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(Res.drawable.trash),
            contentDescription = null,
            tint = VfvChromeIconTint,
            modifier = Modifier.size(VfvChromeIconSize),
        )
    }
}
