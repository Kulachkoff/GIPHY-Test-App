package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.domain.repo.AppSettingsRepository
import com.chililabs.giphytest.utils.annotation.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<AppTheme> =
        appSettingsRepository.theme
            .flowOn(ioDispatcher)
}

