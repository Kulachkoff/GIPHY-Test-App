package com.chililabs.giphytest.data.repo

import com.chililabs.giphytest.data.mapper.toDomain
import com.chililabs.giphytest.data.remote.api.GiphyApi
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.GifsRepository
import com.chililabs.giphytest.utils.ext.flowCatching
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GifsRepositoryImpl @Inject constructor(
    private val giphyApi: GiphyApi
) : GifsRepository {

    override fun getById(gifId: String): Flow<Gif?> = flowCatching(
        message = "Failed to fetch GIF with id: $gifId"
    ) {
        val gifDto = giphyApi.getById(gifId)?.data
        emit(gifDto?.toDomain())
    }
}