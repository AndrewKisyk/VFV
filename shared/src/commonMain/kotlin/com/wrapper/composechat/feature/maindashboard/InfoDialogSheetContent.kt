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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.info_dialog_change_age
import com.wrapper.composechat.resources.info_dialog_feature_age_norms
import com.wrapper.composechat.resources.info_dialog_feature_five_groups
import com.wrapper.composechat.resources.info_dialog_feature_no_ads
import com.wrapper.composechat.resources.info_dialog_feature_offline
import com.wrapper.composechat.resources.info_dialog_feature_tips
import com.wrapper.composechat.resources.info_dialog_feature_tracker
import com.wrapper.composechat.resources.info_dialog_plast_logo
import com.wrapper.composechat.resources.info_dialog_terms
import com.wrapper.composechat.resources.requirements_tick_circle
import com.wrapper.composechat.ui.components.VfvChromeCloseIconButton
import com.wrapper.composechat.ui.components.VfvListItemEntrance
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Figma `InfoDialog` (`551:1457`) — replaces chats access sheet content. */
private val InfoDialogFeatureIconBorder = Color(0xFF96FD9F)
private val InfoDialogFeatureIconGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6BEE76), Color(0xFF2DA838)),
)
private val InfoDialogButtonFill = Color(0xFF531DAB)
private val InfoDialogButtonBorder = Color(0xFFB37FEB)
private val InfoDialogButtonHighlight = Brush.linearGradient(
    colorStops = arrayOf(
        0.019f to Color(0xFFB37FEB).copy(alpha = 0.35f),
        0.173f to Color(0x00D2AEF5),
        0.874f to Color(0xFFEFDFFF).copy(alpha = 0.28f),
    ),
)
private val InfoDialogButtonShadow = Color(0x529254DE)
private val InfoDialogButtonShape = RoundedCornerShape(12.dp)

@Immutable
private data class InfoDialogFeature(
    val textRes: StringResource,
)

private val InfoDialogFeatures = listOf(
    InfoDialogFeature(Res.string.info_dialog_feature_age_norms),
    InfoDialogFeature(Res.string.info_dialog_feature_five_groups),
    InfoDialogFeature(Res.string.info_dialog_feature_tips),
    InfoDialogFeature(Res.string.info_dialog_feature_tracker),
    InfoDialogFeature(Res.string.info_dialog_feature_offline),
    InfoDialogFeature(Res.string.info_dialog_feature_no_ads),
)

@Composable
fun InfoDialogSheetContent(
    onClose: () -> Unit,
    onChangeAge: () -> Unit,
    onTermsClick: () -> Unit = {},
) {
    val family = LocalVfvDisplayFontFamily.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // Figma Navigation / Navbar — close aligned end.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.weight(1f))
            VfvChromeCloseIconButton(onClick = onClose)
        }

        // Figma Content — logo, breathing space, feature list.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.info_dialog_plast_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .height(71.dp),
                contentScale = ContentScale.Fit,
            )

            // Empty `Carousel / Filter Chip` frame in Figma (180dp) — keeps vertical rhythm.
            Spacer(Modifier.height(180.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoDialogFeatures.forEachIndexed { index, feature ->
                    VfvListItemEntrance(index = index) {
                        InfoDialogFeatureRow(text = stringResource(feature.textRes))
                    }
                }
            }
        }

        // Figma Action — primary CTA + terms.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = InfoDialogButtonShape,
                        ambientColor = InfoDialogButtonShadow,
                        spotColor = InfoDialogButtonShadow,
                    )
                    .clip(InfoDialogButtonShape)
                    .background(InfoDialogButtonFill)
                    .border(0.5.dp, InfoDialogButtonBorder, InfoDialogButtonShape)
                    .clickable(onClick = onChangeAge),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(InfoDialogButtonHighlight),
                )
                Text(
                    text = stringResource(Res.string.info_dialog_change_age),
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = family,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onTermsClick),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.info_dialog_terms),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = family,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}

/** Figma `Elements / Features` — green completed icon + Bold 13/18 label. */
@Composable
private fun InfoDialogFeatureRow(
    text: String,
) {
    val family = LocalVfvDisplayFontFamily.current
    Row(
        modifier = Modifier.padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(InfoDialogFeatureIconGradient, CircleShape)
                .border(0.5.dp, InfoDialogFeatureIconBorder, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.requirements_tick_circle),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = family,
            maxLines = 1,
        )
    }
}
