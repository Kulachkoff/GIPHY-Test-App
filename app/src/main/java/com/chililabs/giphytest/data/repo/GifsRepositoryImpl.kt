package com.chililabs.giphytest.data.repo

import com.chililabs.giphytest.data.mapper.toDomain
import com.chililabs.giphytest.data.remote.api.GiphyApi
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.GifsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class GifsRepositoryImpl @Inject constructor(
    private val giphyApi: GiphyApi
) : GifsRepository {

    override fun getById(gifId: String): Flow<Gif?> = flow {
        try {
            val gifDto = giphyApi.getById(gifId)?.data
            emit(gifDto?.toDomain())
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch GIF with id: $gifId")
            emit(null)
        }
    }
}