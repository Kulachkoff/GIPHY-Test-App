package com.chililabs.giphytest.data.repo

import com.chililabs.giphytest.data.local.dao.FavoriteGifDao
import com.chililabs.giphytest.data.mapper.toDomainList
import com.chililabs.giphytest.data.mapper.toEntity
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteGifDao: FavoriteGifDao
) : FavoritesRepository {

    override fun getFavorites(): Flow<List<Gif>> =
        favoriteGifDao.getAll().map { list -> list.toDomainList() }

    override suspend fun toggleFavorite(gif: Gif): Boolean {
        val nowFavorite = !favoriteGifDao.exists(gif.id)
        if (nowFavorite) {
            favoriteGifDao.upsert(gif.toEntity())
        } else {
            favoriteGifDao.deleteById(gif.id)
        }
        return nowFavorite
    }

    override suspend fun isFavorite(gifId: String): Boolean = favoriteGifDao.exists(gifId)
}