package com.wrapper.composechat.feature.maindashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wrapper.composechat.resources.Res
import com.wrapper.composechat.resources.nav_back
import org.jetbrains.compose.resources.stringResource

/** Placeholder for VFV [com.plstudio.a123.vfv.fragments.GroupsFragment]. */
@Composable
fun VfvGroupsPlaceholderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) {
            Text(stringResource(Res.string.nav_back))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Вимоги (Groups) — перенесіть логіку з VFV GroupsFragment сюди.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
