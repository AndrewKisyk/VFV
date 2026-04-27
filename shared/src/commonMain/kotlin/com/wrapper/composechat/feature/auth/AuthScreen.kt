package com.wrapper.composechat.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import com.wrapper.composechat.resources.auth_swipe_to_continue
import com.wrapper.composechat.ui.components.SwipeToActionButton
import com.wrapper.composechat.ui.components.VfvGlassFullScreenBottomSheet
import com.wrapper.composechat.ui.theme.ChatColors
import com.wrapper.composechat.ui.theme.ChatDimens
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun AuthScreen(
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = koinInject<AuthViewModel>(),
    /** Captured with [com.wrapper.composechat.platform.rememberComposeViewBitmapCapture] before the sheet opens (e.g. Root). */
    backgroundSnapshot: ImageBitmap? = null,
    titleImageModifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                AuthEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }


    // Blur + strip: same defaults as [com.wrapper.composechat.feature.maindashboard.MainDashboardScreen] chats sheet.
    VfvGlassFullScreenBottomSheet(
        visible = true,
        onDismissRequest = {},
        backgroundSnapshot = backgroundSnapshot,
        swipeToDismissEnabled = false,
    ) {
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
                )
                FilterChip(
                    selected = state.selectedSex == Sex.Female,
                    onClick = { onSexSelect(Sex.Female) },
                    label = { Text(stringResource(Res.string.auth_sex_female)) },
                    colors = chipColors(),
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
        SwipeToActionButton(
            text = stringResource(Res.string.auth_swipe_to_continue),
            onComplete = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ChatDimens.formCardInnerPadding),
            contentDescription = stringResource(Res.string.auth_swipe_to_continue),
            frostedGlass = true,
            trackCorner = ChatDimens.vfvAuthPillCorner,
            height = ChatDimens.vfvAuthPillFieldHeight,
            thumbSize = 44.dp,
        )
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ChatColors.primary.copy(alpha = 0.45f),
    selectedLabelColor = ChatColors.onContent,
    selectedLeadingIconColor = ChatColors.onContent,
    containerColor = Color.White.copy(alpha = 0.12f),
    labelColor = ChatColors.onContent,
    iconColor = ChatColors.onContentMuted,
)
