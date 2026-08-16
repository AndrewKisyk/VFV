package com.wrapper.composechat.feature.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.data.requirements.VfvRequirement
import com.wrapper.composechat.ui.components.VfvChromeCloseIconButton
import com.wrapper.composechat.ui.components.VfvListItemEntrance
import com.wrapper.composechat.ui.liquidglass.LocalVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.rememberVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.vfvLiquidGlass
import com.wrapper.composechat.ui.liquidglass.vfvScreenLayerBackdrop
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.*

private val SheetScrimGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0f to Color(0xFF400D91).copy(alpha = 0.70f),
        0.50343f to Color(0xFF09001F).copy(alpha = 0.92f),
        1f to Color(0xFF09001F).copy(alpha = 0.92f),
    ),
)
private val SheetCardIdleBg = Color(0xFF09001F).copy(alpha = 0.40f)
private val SheetCardIdleBorder = Color.White.copy(alpha = 0.20f)
private val SheetCardDoneBorder = Color(0xFF42098F)
private val SheetValueText = Color.White.copy(alpha = 0.60f)
private val SheetBadgeGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF42098F), Color(0xFFB53FFE)),
)
private val RequirementCardCorner = 12.dp
private const val RequirementCardBlurRadiusDp = 8f

@Composable
fun VfvRequirementsSheetContent(
    groupTitle: String,
    minRequired: Int,
    doneCount: Int,
    requirements: List<VfvRequirement>,
    onClose: () -> Unit,
    onRequirementClick: (VfvRequirement) -> Unit,
    modifier: Modifier = Modifier,
) {
    val family = LocalVfvDisplayFontFamily.current
    val sheetBackdrop = rememberVfvScreenBackdrop()

    CompositionLocalProvider(LocalVfvScreenBackdrop provides sheetBackdrop) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .vfvScreenLayerBackdrop(sheetBackdrop),
        ) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(SheetScrimGradient),
            )
        }
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))
                VfvChromeCloseIconButton(onClick = onClose)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(40.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = groupTitle,
                        color = Color.White,
                        fontSize = 32.sp,
                        lineHeight = 35.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = family,
                    )
                    Text(
                        text = stringResource(Res.string.requirements_min_count, minRequired),
                        color = Color.White.copy(alpha = 0.80f),
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        fontFamily = family,
                    )
                }
                VfvRequirementsCounter(
                    label = "$doneCount/$minRequired",
                    completed = doneCount >= minRequired,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    requirements.forEachIndexed { index, requirement ->
                        VfvListItemEntrance(index = index) {
                            RequirementSheetRowCard(
                                requirement = requirement,
                                family = family,
                                onClick = { onRequirementClick(requirement) },
                            )
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun RequirementSheetRowCard(
    requirement: VfvRequirement,
    family: androidx.compose.ui.text.font.FontFamily?,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(RequirementCardCorner)
    val done = requirement.isDone
    val icon = requirementIconDrawable(requirement.imageKey)
    val doneBadgeShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = RequirementCardCorner,
        bottomStart = RequirementCardCorner,
        bottomEnd = 0.dp,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 62.dp)
            .clip(shape)
            .border(
                width = if (done) 1.dp else 0.5.dp,
                color = if (done) SheetCardDoneBorder else SheetCardIdleBorder,
                shape = shape,
            )
            .clickable(onClick = onClick),
    ) {
        Box(
            Modifier
                .matchParentSize()
                .clip(shape)
                .vfvLiquidGlass(
                    blurRadiusDp = RequirementCardBlurRadiusDp,
                    shape = shape,
                    surfaceColor = SheetCardIdleBg,
                ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = requirement.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = family,
                )
                Text(
                    text = requirement.threshold,
                    color = SheetValueText,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = family,
                )
            }
        }

        if (done) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(doneBadgeShape)
                    .background(SheetBadgeGradient)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(Res.string.requirements_done_badge),
                    color = Color.White,
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = family,
                )
            }
        }
    }
}
