package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.repo.FavoritesRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {

    suspend operator fun invoke(gifId: String) =
        favoritesRepository.isFavorite(gifId)
}