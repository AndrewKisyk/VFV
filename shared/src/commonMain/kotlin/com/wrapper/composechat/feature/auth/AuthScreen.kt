package com.wrapper.composechat.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.auth.AuthEvent
import com.wrapper.composechat.auth.AuthState
import com.wrapper.composechat.auth.AuthValidation
import com.wrapper.composechat.auth.Sex
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.auth_age_error
import com.wrapper.composechat.resources.auth_age_label
import com.wrapper.composechat.resources.auth_sex_error
import com.wrapper.composechat.resources.auth_sex_female
import com.wrapper.composechat.resources.auth_sex_label
import com.wrapper.composechat.resources.auth_sex_male
import com.wrapper.composechat.resources.frosted_chats_continue
import com.wrapper.composechat.platform.isBackdropBlurAvailable
import com.wrapper.composechat.platform.optionalBackdropBlur
import com.wrapper.composechat.ui.components.VfvGlassFullScreenBottomSheet
import com.wrapper.composechat.ui.theme.ChatColors
import com.wrapper.composechat.ui.theme.ChatDimens
import com.wrapper.composechat.ui.theme.Glassmorphism
import com.wrapper.composechat.ui.theme.LocalVfvDisplayFontFamily
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private const val AuthSheetBackdropBlurRadiusDp = 20f

@Composable
fun AuthScreen(
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = koinInject<AuthViewModel>(),
    /** Captured with [com.wrapper.composechat.platform.rememberComposeViewBitmapCapture] before the sheet opens (e.g. Root). */
    backgroundSnapshot: ImageBitmap? = null,
    titleImageModifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val liveBackdropBlur = isBackdropBlurAvailable()

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                AuthEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }
    
    VfvGlassFullScreenBottomSheet(
        visible = true,
        onDismissRequest = {},
        backgroundSnapshot = if (liveBackdropBlur) null else backgroundSnapshot,
        fullScreenBlurredSnapshot = true,
        revealLiveBackdrop = liveBackdropBlur,
        blurBackgroundSnapshot = !liveBackdropBlur,
        backdropBlurRadiusDp = AuthSheetBackdropBlurRadiusDp,
        swipeToDismissEnabled = false,
    ) { _ ->
        AuthFormContent(
            state = state,
            onAgeChange = { viewModel.onEvent(AuthEvent.AgeChanged(it)) },
            onSexSelect = { viewModel.onEvent(AuthEvent.SexSelected(it)) },
            onContinue = { viewModel.onEvent(AuthEvent.ContinueClicked) },
            titleImageModifier = titleImageModifier,
        )
    }
}

@Composable
private fun AuthFormContent(
    state: AuthState,
    onAgeChange: (String) -> Unit,
    onSexSelect: (Sex) -> Unit,
    onContinue: () -> Unit,
    titleImageModifier: Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = ChatDimens.screenEdgeHorizontal)
            .padding(bottom = ChatDimens.screenEdgeVertical),
    ) {
        VfvAuthTitleImage(modifier = titleImageModifier)
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ChatDimens.formCardInnerPadding),
            verticalArrangement = Arrangement.spacedBy(ChatDimens.formFieldSpacing),
        ) {
            VfvPillAgeTextField(
                value = state.ageInput,
                onValueChange = { onAgeChange(it.filter { ch -> ch.isDigit() }) },
                labelText = stringResource(Res.string.auth_age_label),
                isError = state.ageError,
                errorText = if (state.ageError) stringResource(Res.string.auth_age_error) else null,
                showValidCheck = AuthValidation.isAgeInputValid(state.ageInput),
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(Res.string.auth_sex_label),
                color = ChatColors.onContent,
                style = MaterialTheme.typography.titleSmall,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = state.selectedSex == Sex.Male,
                    onClick = { onSexSelect(Sex.Male) },
                    label = { Text(stringResource(Res.string.auth_sex_male)) },
                    colors = chipColors(),
                    border = sexChipBorder(selected = state.selectedSex == Sex.Male),
                )
                FilterChip(
                    selected = state.selectedSex == Sex.Female,
                    onClick = { onSexSelect(Sex.Female) },
                    label = { Text(stringResource(Res.string.auth_sex_female)) },
                    colors = chipColors(),
                    border = sexChipBorder(selected = state.selectedSex == Sex.Female),
                )
            }
            if (state.sexError) {
                Text(
                    text = stringResource(Res.string.auth_sex_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        AuthContinueControl(
            canContinue = AuthValidation.validate(state.ageInput, state.selectedSex).isValid,
            onContinue = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ChatDimens.formCardInnerPadding),
        )
    }
}

private val AuthFrostTrackBorder = Color.White.copy(alpha = 0.2f)
private val AuthFrostTrackFillFallback = Color.White.copy(alpha = 0.12f)
private val AuthFrostTrackFillBlur = Color.White.copy(alpha = 0.07f)
/** Same as [com.wrapper.composechat.ui.components.SwipeToActionButton] label on frosted track. */
private val AuthContinueDisabledText = Color(0xFFE8D4FF)

@Composable
private fun AuthContinueControl(
    canContinue: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val family = LocalVfvDisplayFontFamily.current
    val continueLabel = stringResource(Res.string.frosted_chats_continue)
    val shape = RoundedCornerShape(25.dp)
    val frostBlurRadiusDp = 18f

    if (canContinue) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = continueLabel
                }
                .clip(shape)
                .background(Glassmorphism.primaryActionBrush)
                .clickable(onClick = onContinue),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = continueLabel,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontFamily = family,
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .alpha(0.45f)
                .semantics(mergeDescendants = true) {
                    contentDescription = continueLabel
                }
                .clip(shape),
        ) {
            if (isBackdropBlurAvailable()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .background(AuthFrostTrackFillBlur, shape)
                        .optionalBackdropBlur(frostBlurRadiusDp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .background(AuthFrostTrackFillFallback, shape),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, AuthFrostTrackBorder, shape),
            )
            Text(
                text = continueLabel,
                color = AuthContinueDisabledText,
                fontWeight = FontWeight.SemiBold,
                fontFamily = family,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp),
            )
        }
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ChatColors.primary.copy(alpha = 0.45f),
    selectedLabelColor = ChatColors.onContent,
    selectedLeadingIconColor = ChatColors.onContent,
    containerColor = Color.White.copy(alpha = 0.2f),
    disabledContainerColor = Color.White.copy(alpha = 0.2f),
    labelColor = ChatColors.onContent,
    iconColor = ChatColors.onContentMuted,
)

@Composable
private fun sexChipBorder(selected: Boolean) = FilterChipDefaults.filterChipBorder(
    enabled = true,
    selected = selected,
    borderColor = Color.White.copy(alpha = 0.2f),
    selectedBorderColor = Color.White.copy(alpha = 0.2f),
)
