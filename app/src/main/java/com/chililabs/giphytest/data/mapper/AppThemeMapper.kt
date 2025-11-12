package com.chililabs.giphytest.data.mapper

import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.domain.model.AppTheme.Dark
import com.chililabs.giphytest.domain.model.AppTheme.Light
import com.chililabs.giphytest.proto.ThemeMode
import timber.log.Timber

fun AppTheme.toThemeMode(): ThemeMode {
    return when (this) {
        Light -> ThemeMode.THEME_MODE_LIGHT
        Dark -> ThemeMode.THEME_MODE_DARK
        AppTheme.System -> ThemeMode.THEME_MODE_SYSTEM
    }
}

fun ThemeMode.toAppTheme(): AppTheme {
    return when (this) {
        ThemeMode.THEME_MODE_LIGHT -> Light
        ThemeMode.THEME_MODE_DARK -> Dark
        ThemeMode.THEME_MODE_SYSTEM -> AppTheme.System
        else -> {
            AppTheme.System.also {
                Timber
                    .tag("AppThemeMapper")
                    .e(IllegalArgumentException(), "Unknown Theme Mode. Setting System as default")
            }
        }
    }
}