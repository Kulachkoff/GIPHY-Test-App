package com.chililabs.giphytest.ui.screens.settings

import androidx.compose.runtime.Immutable
import com.chililabs.giphytest.domain.model.AppTheme

@Immutable
data class SettingsState(
    val theme: AppTheme = AppTheme.System,
    val isLoading: Boolean = false
)

