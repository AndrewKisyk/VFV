package com.wrapper.composechat.feature.maindashboard

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.data.requirements.minRequiredForGroup
import com.wrapper.composechat.platform.rememberComposeViewBitmapCapture
import com.wrapper.composechat.ui.components.VfvGlassFullScreenBottomSheet
import com.wrapper.composechat.ui.components.VfvListItemEntrance
import com.wrapper.composechat.resources.*
import com.wrapper.composechat.ui.liquidglass.LocalVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.rememberVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.vfvLiquidGlass
import com.wrapper.composechat.ui.liquidglass.vfvScreenLayerBackdrop
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private const val RequirementsSheetBackdropBlurRadiusDp = 20f
private const val RequirementGroupCardBlurRadiusDp = 8f

private val RequirementGroupSecondaryText = Color.White.copy(alpha = 0.60f)
private val RequirementGroupCardIdleBg = Color(0xFF09001F).copy(alpha = 0.40f)
private val RequirementGroupCardCompleteBg = Color.White.copy(alpha = 0.10f)
private val RequirementGroupCardBorder = Color.White.copy(alpha = 0.20f)

private val GroupMaxPerGroup = listOf(2, 2, 2, 3, 3)

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

/** Figma `Progress Bar` (`323:5138`): 6dp fill inside a 1dp inset (`p-px`). */
private val RequirementGroupProgressTrackHeight = 8.dp
private val RequirementGroupProgressInset = 1.dp
private val RequirementGroupProgressTrackColor = Color(0xFF28046B)

private fun groupBadgeDrawable(groupIndex: Int, activeAsset: Boolean): DrawableResource {
    return when (groupIndex) {
        0 -> if (activeAsset) Res.drawable.groups_g1_active else Res.drawable.groups_g1_inactive
        1 -> if (activeAsset) Res.drawable.groups_g2_active else Res.drawable.groups_g2_inactive
        2 -> if (activeAsset) Res.drawable.groups_g3_active else Res.drawable.groups_g3_inactive
        3 -> if (activeAsset) Res.drawable.groups_g4_active else Res.drawable.groups_g4_inactive
        else -> if (activeAsset) Res.drawable.groups_g5_active else Res.drawable.groups_g5_inactive
    }
}

private fun groupTitle(groupId: Int): org.jetbrains.compose.resources.StringResource = when (groupId) {
    1 -> Res.string.groups_group1_title
    2 -> Res.string.groups_group2_title
    3 -> Res.string.groups_group3_title
    4 -> Res.string.groups_group4_title
    else -> Res.string.groups_group5_title
}

/**
 * VFV «Вимоги» — п’ять груп норм як у [com.plstudio.a123.vfv.fragments.GroupsFragment].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VfvGroupsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VfvGroupsViewModel = koinInject(),
) {
    val vmState by viewModel.state.collectAsState()
    val family = LocalVfvDisplayFontFamily.current
    val shapeCard = RoundedCornerShape(20.dp)

    var sheetBackground: ImageBitmap? by remember { mutableStateOf(null) }
    var sheetOpenProgress by remember { mutableFloatStateOf(0f) }
    val captureForSheet = rememberComposeViewBitmapCapture()
    val screenBackdrop = rememberVfvScreenBackdrop()
    val sheetVisible = vmState.selectedGroupId != null
    var sheetAnimatedDismiss by remember { mutableStateOf<(() -> Unit)?>(null) }

    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }

    val totalDone = vmState.donePerGroup.zip(GroupMaxPerGroup).sumOf { (d, m) -> d.coerceIn(0, m) }
    val totalMax = GroupMaxPerGroup.sum()

    CompositionLocalProvider(LocalVfvScreenBackdrop provides screenBackdrop) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .vfvScreenLayerBackdrop(screenBackdrop),
        ) {
            VfvGroupsScreenBackdrop(
                heroDrawable = Res.drawable.main_dashboard_requirements,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .vfvGroupsContentGradient()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
        ) {
            VfvListChromeTopBar(
                title = stringResource(Res.string.main_dashboard_requirements_title).uppercase(),
                onBack = {
                    if (vmState.selectedGroupId != null) {
                        sheetAnimatedDismiss?.invoke() ?: run {
                            viewModel.closeSheet()
                            sheetBackground = null
                            sheetOpenProgress = 0f
                        }
                    } else {
                        onBack()
                    }
                },
                onTrash = { viewModel.resetAllRequirements() },
                family = family,
                titleFontSize = 11.sp,
                titleFontWeight = FontWeight.Light,
                titleColor = Color.White.copy(alpha = 0.60f),
                titleLetterSpacing = 0.sp,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 28.dp),
            ) {
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.CenterHorizontally)
                        .requirementsHeroSharedElement()
                        .clip(shapeCard)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.10f),
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
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.groups_completed_label),
                        contentDescription = null,
                        modifier = Modifier.size(9.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.groups_completed_label),
                        color = RequirementGroupSecondaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = family,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "$totalDone/$totalMax",
                        color = RequirementGroupSecondaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = family,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                requirementGroups.forEachIndexed { idx, group ->
                    val done = vmState.donePerGroup.getOrElse(idx) { 0 }.coerceIn(0, GroupMaxPerGroup[idx])
                    val max = GroupMaxPerGroup[idx]
                    val complete = done >= max
                    VfvListItemEntrance(index = idx) {
                        RequirementGroupRowCard(
                            title = stringResource(group.titleRes),
                            subtitle = stringResource(group.subtitleRes),
                            progress = done.toFloat() / max.coerceAtLeast(1),
                            progressLabel = "$done/$max",
                            complete = complete,
                            badgeDrawable = groupBadgeDrawable(idx, activeAsset = complete),
                            shape = shapeCard,
                            family = family,
                            onClick = {
                                sheetBackground = captureForSheet()
                                viewModel.openGroup(group.index1)
                            },
                        )
                    }
                }
            }
        }
        }
        VfvGlassFullScreenBottomSheet(
            visible = sheetVisible,
            onDismissRequest = {
                viewModel.closeSheet()
                sheetBackground = null
                sheetOpenProgress = 0f
            },
            backgroundSnapshot = sheetBackground,
            revealLiveBackdrop = false,
            blurBackgroundSnapshot = true,
            backdropBlurRadiusDp = RequirementsSheetBackdropBlurRadiusDp,
            onOpenProgressChange = { sheetOpenProgress = it },
            contentFullScreen = true,
        ) { onAnimatedDismiss ->
            DisposableEffect(onAnimatedDismiss) {
                sheetAnimatedDismiss = onAnimatedDismiss
                onDispose { sheetAnimatedDismiss = null }
            }
            val selectedGroupId = vmState.selectedGroupId
            if (selectedGroupId != null) {
                VfvRequirementsSheetContent(
                    groupTitle = stringResource(groupTitle(selectedGroupId)),
                    minRequired = minRequiredForGroup(selectedGroupId),
                    doneCount = vmState.sheetDoneCount,
                    requirements = vmState.sheetRequirements,
                    onClose = onAnimatedDismiss,
                    onRequirementClick = viewModel::toggleRequirement,
                )
            }
        }
    }
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
    val cardBg = if (complete) RequirementGroupCardCompleteBg else RequirementGroupCardIdleBg

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(shape)
                .vfvLiquidGlass(
                    blurRadiusDp = RequirementGroupCardBlurRadiusDp,
                    shape = shape,
                    surfaceColor = cardBg,
                )
                .then(
                    if (!complete) {
                        Modifier.border(0.5.dp, RequirementGroupCardBorder, shape)
                    } else {
                        Modifier
                    },
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(badgeDrawable),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = family,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        color = RequirementGroupSecondaryText,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = family,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!complete) {
                        RequirementGroupProgressBar(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                if (complete) {
                    Image(
                        painter = painterResource(Res.drawable.groups_row_complete_check),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(
                        text = progressLabel,
                        color = RequirementGroupSecondaryText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = family,
                    )
                }
            }
        }
    }
}

/** Figma `Groups/…/Progress Bar` (`323:5138`): `#28046b` pill with a gradient `Active` fill. */
@Composable
private fun RequirementGroupProgressBar(
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
        val trackW = size.width
        val trackH = size.height
        drawRoundRect(
            color = RequirementGroupProgressTrackColor,
            size = Size(trackW, trackH),
            cornerRadius = CornerRadius(trackH * 0.5f, trackH * 0.5f),
        )
        val inset = RequirementGroupProgressInset.toPx()
        val fillH = (trackH - inset * 2f).coerceAtLeast(0f)
        val fillW = (trackW - inset * 2f).coerceAtLeast(0f) * p
        if (fillW <= 0f || fillH <= 0f) return@Canvas
        drawRoundRect(
            // Figma paints the gradient on `Active` itself, so it spans the filled width only.
            brush = Brush.horizontalGradient(
                0.12979f to Color(0xFFDF18FF),
                0.41497f to Color(0xFF8800DC),
                0.87208f to Color(0xFF6400EC),
                startX = inset,
                endX = inset + fillW,
            ),
            topLeft = Offset(inset, inset),
            size = Size(fillW, fillH),
            cornerRadius = CornerRadius(fillH * 0.5f, fillH * 0.5f),
        )
    }
}
