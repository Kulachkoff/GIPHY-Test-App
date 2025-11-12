package com.chililabs.giphytest.domain.repo

import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.proto.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val settings: Flow<AppSettings>
    val theme: Flow<AppTheme>

    suspend fun setTheme(theme: AppTheme)
}

