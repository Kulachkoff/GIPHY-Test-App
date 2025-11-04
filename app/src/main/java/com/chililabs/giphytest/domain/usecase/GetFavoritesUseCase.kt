package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.FavoritesRepository
import com.chililabs.giphytest.utils.annotation.IODispatcher
import com.chililabs.giphytest.utils.ext.mapToResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

typealias GetFavoritesResult = Result<List<Gif>>

class GetFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<GetFavoritesResult> =
        favoritesRepository.getFavorites()
            .mapToResult()
            .flowOn(ioDispatcher)
}