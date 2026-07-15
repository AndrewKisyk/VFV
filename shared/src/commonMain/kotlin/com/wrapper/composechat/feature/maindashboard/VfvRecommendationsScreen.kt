package com.wrapper.composechat.feature.maindashboard

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.platform.optionalBackdropBlur
import com.wrapper.composechat.resources.*
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Immutable
data class VfvRecommendationsUiState(
    /** Topic ids from legacy [com.plstudio.a123.vfv.fragments.RecomendationListFragment]. */
    val readTopicIds: Set<String> = emptySet(),
) {
    val totalCount: Int get() = recommendationTopics.size
    val totalRead: Int get() = readTopicIds.count { id -> recommendationTopics.any { it.id == id } }
}

@Immutable
private data class RecommendationTopicRow(
    val id: String,
    val titleRes: StringResource,
    val subtitleRes: StringResource,
    val iconDrawable: DrawableResource,
)

private val recommendationTopics = listOf(
    RecommendationTopicRow("cardio", Res.string.recommendations_topic1_title, Res.string.recommendations_topic1_subtitle, Res.drawable.heart),
    RecommendationTopicRow("jump", Res.string.recommendations_topic2_title, Res.string.recommendations_topic2_subtitle, Res.drawable.long_jump),
    RecommendationTopicRow("running", Res.string.recommendations_topic3_title, Res.string.recommendations_topic3_subtitle, Res.drawable.run),
    RecommendationTopicRow("strength", Res.string.recommendations_topic4_title, Res.string.recommendations_topic4_subtitle, Res.drawable.weight),
)

/**
 * VFV «Рекомендації» — список тем як у [com.plstudio.a123.vfv.fragments.RecomendationListFragment].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VfvRecommendationsScreen(
    onBack: () -> Unit,
    onTrashClick: () -> Unit = {},
    onTopicClick: (topicId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    state: VfvRecommendationsUiState = remember { VfvRecommendationsUiState() },
) {
    val family = LocalVfvDisplayFontFamily.current
    val shapeCard = RoundedCornerShape(20.dp)

    Box(modifier = modifier.fillMaxSize()) {
        VfvListScreenBackdrop(heroDrawable = Res.drawable.main_dashboard_recommendations)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            VfvListChromeTopBar(
                title = stringResource(Res.string.main_dashboard_recommendations_title).uppercase(),
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
                        .recommendationsHeroSharedElement()
                        .clip(shapeCard)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.14f),
                            shape = shapeCard,
                        ),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.main_dashboard_recommendations),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
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
                        text = stringResource(Res.string.recommendations_read_label),
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 16.sp,
                        fontFamily = family,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${state.totalRead}/${state.totalCount}",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = family,
                    )
                }
                Spacer(Modifier.height(18.dp))
                recommendationTopics.forEach { topic ->
                    val read = topic.id in state.readTopicIds
                    RecommendationTopicRowCard(
                        title = stringResource(topic.titleRes),
                        subtitle = stringResource(topic.subtitleRes),
                        read = read,
                        iconDrawable = topic.iconDrawable,
                        shape = shapeCard,
                        family = family,
                        onClick = { onTopicClick(topic.id) },
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun RecommendationTopicRowCard(
    title: String,
    subtitle: String,
    read: Boolean,
    iconDrawable: DrawableResource,
    shape: RoundedCornerShape,
    family: androidx.compose.ui.text.font.FontFamily?,
    onClick: () -> Unit,
) {
    val cardBg = Color(0xFF09001F).copy(alpha = 0.4f)
    val strokeDefault = Color.White.copy(alpha = 0.2f)
    val secondaryText = Color.White.copy(alpha = 0.6f)
    val strokeModifier =
        if (!read) {
            Modifier.border(width = 1.dp, color = strokeDefault, shape = shape)
        } else {
            Modifier
        }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (read) Color.Transparent else cardBg,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            if (read) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .optionalBackdropBlur(VfvListCardCompleteBlurRadiusDp)
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0A0F1C).copy(alpha = 0.65f))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(14.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(iconDrawable),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
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
                }
                Spacer(Modifier.width(8.dp))
                if (read) {
                    Image(
                        painter = painterResource(Res.drawable.groups_row_complete_check),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.45f),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}
