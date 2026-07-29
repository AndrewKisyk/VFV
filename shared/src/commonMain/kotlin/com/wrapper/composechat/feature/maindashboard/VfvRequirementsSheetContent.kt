package com.wrapper.composechat.feature.maindashboard

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.*
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val SheetCardIdleBg = Color(0xFF09001F).copy(alpha = 0.72f)
private val SheetCardDoneBg = Color(0xFF0F0524)
private val SheetCardDoneBorder = Color(0xFF9B35FF)
private val SheetCardIdleBorder = Color.White.copy(alpha = 0.16f)
private val SheetValueText = Color(0xFFB0B0B0)
private val SheetStatusIdleBg = Color(0xFF1C1C28)
private val SheetStatusIdleIcon = Color.White.copy(alpha = 0.28f)
private val SheetBadgeGradient = Brush.horizontalGradient(
    listOf(Color(0xFFDF18FF), Color(0xFF6400EC)),
)
private val RequirementCardCorner = 18.dp

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(Res.string.requirements_close),
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = groupTitle,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = family,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.requirements_min_count, minRequired),
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 14.sp,
            fontFamily = family,
        )
        Spacer(Modifier.height(14.dp))
        VfvRequirementsCounter(label = "$doneCount/$minRequired")
        Spacer(Modifier.height(18.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            requirements.forEach { requirement ->
                RequirementSheetRowCard(
                    requirement = requirement,
                    family = family,
                    onClick = { onRequirementClick(requirement) },
                )
            }
            Spacer(Modifier.height(12.dp))
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
        bottomStart = 14.dp,
        bottomEnd = 0.dp,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .clip(shape)
            .background(if (done) SheetCardDoneBg else SheetCardIdleBg)
            .border(
                width = if (done) 2.dp else 1.dp,
                color = if (done) SheetCardDoneBorder else SheetCardIdleBorder,
                shape = shape,
            )
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = requirement.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = family,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = requirement.threshold,
                    color = SheetValueText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = family,
                )
            }
            if (!done) {
                Spacer(Modifier.width(10.dp))
                RequirementStatusCircle(done = false)
            }
        }

        if (done) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(doneBadgeShape)
                    .background(SheetBadgeGradient)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text(
                    text = stringResource(Res.string.requirements_done_badge),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = family,
                )
            }
        }
    }
}

@Composable
private fun RequirementStatusCircle(done: Boolean) {
    val circleBg = if (done) Color(0xFF1F7A3A) else SheetStatusIdleBg
    val iconTint = if (done) Color.White else SheetStatusIdleIcon
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(circleBg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp),
        )
    }
}
