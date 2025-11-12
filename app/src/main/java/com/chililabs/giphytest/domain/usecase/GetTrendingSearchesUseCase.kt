package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.repo.TrendingSearchesRepository
import com.chililabs.giphytest.utils.annotation.IODispatcher
import com.chililabs.giphytest.utils.ext.mapToResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

typealias GetTrendingSearchesResult = Result<List<String>>

class GetTrendingSearchesUseCase @Inject constructor(
    private val repository: TrendingSearchesRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<GetTrendingSearchesResult> =
        repository.getTrendingSearches()
            .mapToResult()
            .flowOn(ioDispatcher)
}

