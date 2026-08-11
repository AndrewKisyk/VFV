package com.wrapper.composechat.feature.maindashboard

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.wrapper.composechat.ui.components.VfvChromeBackIconButton
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private val VfvRecommendationDetailBackground = Color(0xFF020725)
private val VfvRecommendationDetailTopBarScrolled = Color(0xFF09001F)

/**
 * VFV recommendation article — port of [com.plstudio.a123.vfv.fragments.RecomendationFragment].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VfvRecommendationDetailScreen(
    topicId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VfvRecommendationsViewModel = koinInject(),
) {
    val topic = vfvRecommendationTopic(topicId) ?: run {
        onBack()
        return
    }
    val vmState by viewModel.state.collectAsState()
    val alreadyRead = topicId in vmState.readTopicIds
    val family = LocalVfvDisplayFontFamily.current
    val articleHtml = stringResource(topic.articleRes)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }

    // Legacy RecomendationFragment: start 7s timer once, cancel on onPause, no restart on resume.
    DisposableEffect(lifecycleOwner, topicId, alreadyRead) {
        if (alreadyRead) return@DisposableEffect onDispose {}
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        var timerJob = scope.launch {
            delay(7_000)
            viewModel.markTopicRead(topicId)
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                timerJob.cancel()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            scope.cancel()
        }
    }

    val scrollState = rememberScrollState()
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val topBarScrollInset = with(density) { topBarHeightPx.toDp() }
    val title = stringResource(topic.titleRes).uppercase()
    val subtitle = stringResource(topic.subtitleRes)
    val isTopBarScrolled by remember {
        derivedStateOf { scrollState.value > 0 }
    }
    val topBarColor by animateColorAsState(
        targetValue = if (isTopBarScrolled) {
            VfvRecommendationDetailTopBarScrolled
        } else {
            VfvRecommendationDetailBackground
        },
        animationSpec = tween(durationMillis = 200),
        label = "detailTopBarColor",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VfvRecommendationDetailBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Spacer(Modifier.height(topBarScrollInset))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
            ) {
                VfvRecommendationArticleHtml(
                    html = articleHtml,
                    family = family,
                )
            }
        }
        VfvRecommendationDetailTopBar(
            title = title,
            subtitle = subtitle,
            onBack = onBack,
            family = family,
            backgroundColor = topBarColor,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .onSizeChanged { topBarHeightPx = it.height },
        )
    }
}

@Composable
private fun VfvRecommendationDetailTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    family: androidx.compose.ui.text.font.FontFamily?,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(bottom = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = VfvListChromeHorizontalPadding),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VfvChromeBackIconButton(onClick = onBack)
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    color = Color.White.copy(alpha = 0.60f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = family,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.size(32.dp))
            }
            Text(
                text = subtitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 44.dp, end = 44.dp),
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 14.sp,
                fontFamily = family,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
