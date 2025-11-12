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
import androidx.compose.ui.res.stringResource
import com.chililabs.giphytest.R
import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.ui.theme.Dimens

@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.settingsScreenPadding)
    ) {
        Text(
            modifier = Modifier.padding(vertical = Dimens.settingsSectionVertical),
            text = stringResource(R.string.settings_appearance),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            modifier = Modifier.padding(
                top = Dimens.settingsItemTop,
                bottom = Dimens.settingsItemBottom
            ),
            text = stringResource(R.string.settings_theme),
            style = MaterialTheme.typography.titleMedium
        )

        AppTheme.entries.forEach { theme ->
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = when (theme) {
                            AppTheme.Light -> stringResource(R.string.theme_light)
                            AppTheme.Dark -> stringResource(R.string.theme_dark)
                            AppTheme.System -> stringResource(R.string.theme_system)
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

