package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.GifsRepository
import com.chililabs.giphytest.utils.annotation.IODispatcher
import com.chililabs.giphytest.utils.exception.GifNotFoundException
import com.chililabs.giphytest.utils.ext.mapToResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

typealias GetGifResult = Result<Gif>

class GetGifByIdUseCase @Inject constructor(
    private val favoritesRepository: GifsRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(gifId: String): Flow<GetGifResult> =
        favoritesRepository.getById(gifId)
            .map { gif -> gif ?: throw GifNotFoundException(gifId) }
            .mapToResult()
            .flowOn(ioDispatcher)
}