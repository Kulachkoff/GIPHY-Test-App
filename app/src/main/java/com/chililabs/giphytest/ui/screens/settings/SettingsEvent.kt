package com.chililabs.giphytest.ui.screens.settings

import com.chililabs.giphytest.domain.model.AppTheme

sealed interface SettingsEvent {
    data class ThemeChanged(val theme: AppTheme) : SettingsEvent
}

