package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.FavoritesRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {

    suspend operator fun invoke(gif: Gif) =
        favoritesRepository.toggleFavorite(gif)
}