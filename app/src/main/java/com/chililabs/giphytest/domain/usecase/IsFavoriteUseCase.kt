package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.repo.FavoritesRepository
import com.chililabs.giphytest.utils.annotation.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(gifId: String) = withContext(ioDispatcher) {
        favoritesRepository.isFavorite(gifId)
    }
}