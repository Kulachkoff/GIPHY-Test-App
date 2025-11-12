package com.chililabs.giphytest.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chililabs.giphytest.domain.model.AppTheme

@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = "Appearance",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            text = "Theme",
            style = MaterialTheme.typography.titleMedium
        )

        AppTheme.entries.forEach { theme ->
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = when (theme) {
                            AppTheme.Light -> "Light"
                            AppTheme.Dark -> "Dark"
                            AppTheme.System -> "System Default"
                        }
                    )
                },
                leadingContent = {
                    RadioButton(
                        selected = state.theme == theme,
                        onClick = { onEvent(SettingsEvent.ThemeChanged(theme)) }
                    )
                }
            )
        }
    }
}

