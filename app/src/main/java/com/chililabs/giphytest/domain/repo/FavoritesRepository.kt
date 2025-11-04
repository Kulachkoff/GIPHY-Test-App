package com.chililabs.giphytest.domain.repo

import com.chililabs.giphytest.domain.model.Gif
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<List<Gif>>
    suspend fun toggleFavorite(gif: Gif): Boolean
    suspend fun isFavorite(gifId: String): Boolean
}
