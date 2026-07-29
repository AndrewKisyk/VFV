package com.wrapper.composechat.feature.maindashboard

import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.wrapper.composechat.ui.liquidglass.LocalVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.rememberVfvScreenBackdrop
import com.wrapper.composechat.ui.liquidglass.vfvScreenLayerBackdrop
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
    val screenBackdrop = rememberVfvScreenBackdrop()
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

    CompositionLocalProvider(LocalVfvScreenBackdrop provides screenBackdrop) {
        Box(modifier = modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .vfvScreenLayerBackdrop(screenBackdrop),
            ) {
                VfvListScreenBackdrop(heroDrawable = topic.iconDrawable)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                VfvRecommendationDetailTopBar(
                    title = stringResource(topic.titleRes).uppercase(),
                    onBack = onBack,
                    family = family,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 28.dp),
                ) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(topic.subtitleRes),
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 14.sp,
                        fontFamily = family,
                    )
                    Spacer(Modifier.height(16.dp))
                    VfvRecommendationArticleHtml(
                        html = articleHtml,
                        family = family,
                    )
                }
            }
        }
    }
}

@Composable
private fun VfvRecommendationDetailTopBar(
    title: String,
    onBack: () -> Unit,
    family: androidx.compose.ui.text.font.FontFamily?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VfvListChromeIconButton(
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
        Spacer(modifier = Modifier.size(32.dp))
    }
}
