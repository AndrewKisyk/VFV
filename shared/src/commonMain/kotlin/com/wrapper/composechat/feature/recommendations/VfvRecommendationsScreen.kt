package com.wrapper.composechat.feature.recommendations

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrapper.composechat.feature.dashboard.VfvListCardCompleteBlurRadiusDp
import com.wrapper.composechat.feature.dashboard.VfvListChromeHorizontalPadding
import com.wrapper.composechat.feature.dashboard.VfvListChromeTopBar
import com.wrapper.composechat.feature.dashboard.VfvListScreenBackdrop
import com.wrapper.composechat.feature.dashboard.recommendationsHeroSharedElement
import com.wrapper.composechat.feature.home.LocalVfvTransitionInteractor
import com.wrapper.composechat.feature.home.navigateSettled
import com.wrapper.composechat.ui.components.VfvConfirmDeleteProgressDialog
import com.wrapper.composechat.ui.components.VfvListItemEntrance
import com.wrapper.composechat.ui.liquidglass.LocalVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.rememberVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.vfvLiquidGlass
import com.wrapper.composechat.ui.liquidglass.vfvScreenLayerBackdrop
import com.wrapper.composechat.resources.*
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Immutable
private data class RecommendationTopicRow(
    val id: String,
    val titleRes: StringResource,
    val subtitleRes: StringResource,
    val iconDrawable: DrawableResource,
)

private val recommendationTopics = vfvRecommendationTopics.map { topic ->
    RecommendationTopicRow(
        id = topic.id,
        titleRes = topic.titleRes,
        subtitleRes = topic.subtitleRes,
        iconDrawable = topic.iconDrawable,
    )
}

/** Same secondary label style as Groups «виконано:» row. */
private val RecommendationsReadSecondaryText = Color.White.copy(alpha = 0.60f)

/**
 * VFV «Рекомендації» — список тем як у [com.plstudio.a123.vfv.fragments.RecomendationListFragment].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VfvRecommendationsScreen(
    onBack: () -> Unit,
    onTopicClick: (topicId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: VfvRecommendationsViewModel = koinInject(),
) {
    val vmState by viewModel.state.collectAsState()
    val family = LocalVfvDisplayFontFamily.current
    val shapeCard = RoundedCornerShape(20.dp)
    val screenBackdrop = rememberVfvScreenBackdrop()
    var backConsumed by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    val transitionInteractor = LocalVfvTransitionInteractor.current
    // Survives the trip to the detail screen: on pop the rows must stay put, otherwise the
    // title morph would chase a row that is still sliding in.
    var entrancePlayed by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    CompositionLocalProvider(LocalVfvScreenBackdrop provides screenBackdrop) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .vfvScreenLayerBackdrop(screenBackdrop),
        ) {
            VfvListScreenBackdrop(heroDrawable = Res.drawable.main_dashboard_recommendations)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = VfvListChromeHorizontalPadding),
        ) {
            VfvListChromeTopBar(
                title = stringResource(Res.string.main_dashboard_recommendations_title).uppercase(),
                onBack = {
                    if (backConsumed) return@VfvListChromeTopBar
                    backConsumed = true
                    onBack()
                },
                onTrash = { showConfirmDelete = true },
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
                        modifier = Modifier.size(9.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.recommendations_read_label),
                        color = RecommendationsReadSecondaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = family,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${vmState.totalRead}/${vmState.totalCount}",
                        color = RecommendationsReadSecondaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = family,
                    )
                }
                Spacer(Modifier.height(18.dp))
                recommendationTopics.forEachIndexed { index, topic ->
                    val read = topic.id in vmState.readTopicIds
                    VfvListItemEntrance(index = index, shouldAnimate = !entrancePlayed) {
                        RecommendationTopicRowCard(
                            topicId = topic.id,
                            title = stringResource(topic.titleRes),
                            subtitle = stringResource(topic.subtitleRes),
                            read = read,
                            iconDrawable = topic.iconDrawable,
                            shape = shapeCard,
                            family = family,
                            onClick = {
                                entrancePlayed = true
                                transitionInteractor.navigateSettled { onTopicClick(topic.id) }
                            },
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                }
            }
        }
        VfvConfirmDeleteProgressDialog(
            visible = showConfirmDelete,
            onConfirm = {
                showConfirmDelete = false
                viewModel.resetAllReads()
            },
            onDismiss = { showConfirmDelete = false },
        )
    }
    }
}

@Composable
private fun RecommendationTopicRowCard(
    topicId: String,
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
                        .vfvLiquidGlass(
                            blurRadiusDp = VfvListCardCompleteBlurRadiusDp,
                            shape = shape,
                            surfaceColor = Color.White.copy(alpha = 0.19f),
                        ),
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
                        modifier = Modifier.recommendationTitleSharedBounds(topicId),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = family,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        modifier = Modifier.recommendationSubtitleSharedBounds(
                            topicId = topicId,
                            alignment = Alignment.CenterStart,
                        ),
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
