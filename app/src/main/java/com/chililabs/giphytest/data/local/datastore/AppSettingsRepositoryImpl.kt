package com.chililabs.giphytest.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.domain.repo.AppSettingsRepository
import com.chililabs.giphytest.proto.AppSettings
import com.chililabs.giphytest.proto.ThemeMode
import com.chililabs.giphytest.proto.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepositoryImpl @Inject constructor(
    private val appSettings: DataStore<AppSettings>
) : AppSettingsRepository {

    override val settings: Flow<AppSettings> = appSettings.data

    override val theme: Flow<AppTheme> = settings.map { userSettings ->
        when (userSettings.themeMode) {
            ThemeMode.THEME_MODE_LIGHT -> AppTheme.Light
            ThemeMode.THEME_MODE_DARK -> AppTheme.Dark
            ThemeMode.THEME_MODE_SYSTEM -> AppTheme.System
            else -> AppTheme.System
        }
    }

    override suspend fun setTheme(theme: AppTheme) {
        try {
            appSettings.updateData { currentSettings ->
                currentSettings.copy {
                    this.themeMode = when (theme) {
                        AppTheme.Light -> ThemeMode.THEME_MODE_LIGHT
                        AppTheme.Dark -> ThemeMode.THEME_MODE_DARK
                        AppTheme.System -> ThemeMode.THEME_MODE_SYSTEM
                    }
                }
            }
        } catch (ioException: IOException) {
            Timber.tag("NiaPreferences").e(ioException, "Failed to update App Theme")
        }
    }
}

