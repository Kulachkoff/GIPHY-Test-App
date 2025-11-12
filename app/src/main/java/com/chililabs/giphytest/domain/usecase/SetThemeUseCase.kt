package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.domain.repo.AppSettingsRepository
import com.chililabs.giphytest.utils.annotation.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(theme: AppTheme) = withContext(ioDispatcher) {
        appSettingsRepository.setTheme(theme)
    }
}

