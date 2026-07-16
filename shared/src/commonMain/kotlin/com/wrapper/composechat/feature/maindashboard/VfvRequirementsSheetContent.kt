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

private val SheetPanelColor = Color(0xFF14082E)
private val SheetCardIdleBg = Color(0xFF09001F).copy(alpha = 0.55f)
private val SheetCardDoneBorder = Color(0xFF9B35FF)
private val SheetBadgeGradient = Brush.horizontalGradient(
    listOf(Color(0xFFDF18FF), Color(0xFF6400EC)),
)

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
    val panelShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(panelShape)
            .background(SheetPanelColor)
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
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.groups_completed_label),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "$doneCount/$minRequired",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = family,
            )
        }
        Spacer(Modifier.height(18.dp))
        Column(
            modifier = Modifier
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
    val shape = RoundedCornerShape(20.dp)
    val done = requirement.isDone
    val borderModifier = if (done) {
        Modifier.border(1.5.dp, SheetCardDoneBorder, shape)
    } else {
        Modifier.border(1.dp, Color.White.copy(alpha = 0.14f), shape)
    }
    val icon = requirementIconDrawable(requirement.imageKey)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(borderModifier)
            .background(if (done) Color(0xFF1E0A3D).copy(alpha = 0.85f) else SheetCardIdleBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        if (done) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(50))
                    .background(SheetBadgeGradient)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2A0F52).copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = requirement.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = family,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = requirement.threshold,
                    color = Color.White.copy(alpha = 0.62f),
                    fontSize = 13.sp,
                    fontFamily = family,
                )
            }
        }
    }
}
