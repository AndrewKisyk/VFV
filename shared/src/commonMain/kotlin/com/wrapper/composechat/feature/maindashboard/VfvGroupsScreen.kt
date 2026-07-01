package com.wrapper.composechat.feature.maindashboard

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.platform.optionalBackdropBlur
import com.wrapper.composechat.resources.*
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Immutable
data class VfvGroupsUiState(
    /** Completed count per group (order matches [requirementGroups]). */
    val donePerGroup: List<Int> = listOf(0, 0, 0, 0, 0),
) {
    init {
        require(donePerGroup.size == 5)
    }

    val maxPerGroup: List<Int> = listOf(2, 2, 2, 3, 3)
    val totalDone: Int get() = donePerGroup.zip(maxPerGroup).sumOf { (d, m) -> d.coerceIn(0, m) }
    val totalMax: Int get() = maxPerGroup.sum()
}

@Immutable
private data class RequirementGroupRow(
    val titleRes: org.jetbrains.compose.resources.StringResource,
    val subtitleRes: org.jetbrains.compose.resources.StringResource,
    val index1: Int,
)

private val requirementGroups = listOf(
    RequirementGroupRow(Res.string.groups_group1_title, Res.string.groups_group1_subtitle, 1),
    RequirementGroupRow(Res.string.groups_group2_title, Res.string.groups_group2_subtitle, 2),
    RequirementGroupRow(Res.string.groups_group3_title, Res.string.groups_group3_subtitle, 3),
    RequirementGroupRow(Res.string.groups_group4_title, Res.string.groups_group4_subtitle, 4),
    RequirementGroupRow(Res.string.groups_group5_title, Res.string.groups_group5_subtitle, 5),
)

private const val RequirementGroupCompleteBlurRadiusDp = 20f

/** Same physical height as the load bar in [com.wrapper.composechat.feature.splash.SplashScreen] (`height(8.dp)`). */
private val RequirementGroupProgressTrackHeight = 8.dp

private fun groupBadgeDrawable(groupIndex: Int, activeAsset: Boolean): DrawableResource {
    return when (groupIndex) {
        0 -> if (activeAsset) Res.drawable.groups_g1_active else Res.drawable.groups_g1_inactive
        1 -> if (activeAsset) Res.drawable.groups_g2_active else Res.drawable.groups_g2_inactive
        2 -> if (activeAsset) Res.drawable.groups_g3_active else Res.drawable.groups_g3_inactive
        3 -> if (activeAsset) Res.drawable.groups_g4_active else Res.drawable.groups_g4_inactive
        else -> if (activeAsset) Res.drawable.groups_g5_active else Res.drawable.groups_g5_inactive
    }
}

/**
 * VFV «Вимоги» — п’ять груп норм як у [com.plstudio.a123.vfv.fragments.GroupsFragment].
 * Прогрес: передай [state] після інтеграції з даними (аналог [RequirementsLab]); за замовчуванням нулі.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VfvGroupsScreen(
    onBack: () -> Unit,
    onTrashClick: () -> Unit = {},
    onGroupClick: (groupId: Int) -> Unit = {},
    modifier: Modifier = Modifier,
    state: VfvGroupsUiState = remember { VfvGroupsUiState() },
) {
    val family = LocalVfvDisplayFontFamily.current
    val shapeCard = RoundedCornerShape(20.dp)

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.main_dashboard_requirements),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .optionalBackdropBlur(38f),
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            GroupsChromeTopBar(
                title = stringResource(Res.string.main_dashboard_requirements_title).uppercase(),
                onBack = onBack,
                onTrash = onTrashClick,
                family = family,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 28.dp),
            ) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.CenterHorizontally)
                        .requirementsHeroSharedElement()
                        .clip(shapeCard)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.14f),
                            shape = shapeCard,
                        ),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.main_dashboard_requirements),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillWidth,
                    )
                }
                Spacer(Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.groups_completed_label),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.groups_completed_label),
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 16.sp,
                        fontFamily = family,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${state.totalDone}/${state.totalMax}",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = family,
                    )
                }
                Spacer(Modifier.height(18.dp))
                requirementGroups.forEachIndexed { idx, group ->
                    val done = state.donePerGroup[idx].coerceIn(0, state.maxPerGroup[idx])
                    val max = state.maxPerGroup[idx]
                    val complete = done >= max
                    RequirementGroupRowCard(
                        title = stringResource(group.titleRes),
                        subtitle = stringResource(group.subtitleRes),
                        progress = done.toFloat() / max.coerceAtLeast(1),
                        progressLabel = "$done/$max",
                        complete = complete,
                        badgeDrawable = groupBadgeDrawable(idx, activeAsset = complete),
                        shape = shapeCard,
                        family = family,
                        onClick = { onGroupClick(group.index1) },
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun GroupsChromeTopBar(
    title: String,
    onBack: () -> Unit,
    onTrash: () -> Unit,
    family: androidx.compose.ui.text.font.FontFamily?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GroupsChromeIconButton(
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
        GroupsChromeIconButton(
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
private fun GroupsChromeIconButton(
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

@Composable
private fun RequirementGroupRowCard(
    title: String,
    subtitle: String,
    progress: Float,
    progressLabel: String,
    complete: Boolean,
    badgeDrawable: DrawableResource,
    shape: RoundedCornerShape,
    family: androidx.compose.ui.text.font.FontFamily?,
    onClick: () -> Unit,
) {
    val cardBg = Color(0xFF09001F).copy(alpha = 0.4f)
    val strokeDefault = Color.White.copy(alpha = 0.2f)
    val secondaryText = Color.White.copy(alpha = 0.6f)
    val strokeModifier =
        if (!complete) {
            Modifier.border(width = 1.dp, color = strokeDefault, shape = shape)
        } else {
            Modifier
        }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (complete) Color.Transparent else cardBg,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            if (complete) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .optionalBackdropBlur(RequirementGroupCompleteBlurRadiusDp)
                        .background(Color.White.copy(alpha = 0.19f)),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(strokeModifier)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(badgeDrawable),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.width(10.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = family,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        color = secondaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = family,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!complete) {
                        Spacer(Modifier.height(8.dp))
                        SplashStyleGradientProgress(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth(),
                            trackHeight = RequirementGroupProgressTrackHeight,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                if (complete) {
                    Image(
                        painter = painterResource(Res.drawable.groups_row_complete_check),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(
                        text = progressLabel,
                        color = secondaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = family,
                    )
                }
            }
        }
    }
}

/** Pixel-aligned copy of the load bar in [com.wrapper.composechat.feature.splash.SplashScreen]. */
@Composable
private fun SplashStyleGradientProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    trackHeight: Dp = RequirementGroupProgressTrackHeight,
) {
    val p = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight),
    ) {
        val trackBackgroundH = size.height
        val trackH = trackBackgroundH * 0.8f
        val trackW = size.width
        val r = trackBackgroundH * 0.5f
        drawRoundRect(
            color = Color(0xFF28046B).copy(alpha = 0.6f),
            size = Size(trackW, trackBackgroundH),
            cornerRadius = CornerRadius(r, r),
        )
        drawRoundRect(
            color = Color(0xFF28046B).copy(alpha = 0.4f),
            size = Size(trackW, trackBackgroundH),
            cornerRadius = CornerRadius(r, r),
            style = Stroke(width = 1.5f),
        )
        val fillW = trackW * p
        if (fillW > 2.5f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    0f to Color(0xFFDF18FF),
                    0.38f to Color(0xFF8800DC),
                    1f to Color(0xFF6400EC),
                ),
                size = Size(fillW, trackH),
                cornerRadius = CornerRadius(r, r),
            )
        }
    }
}
