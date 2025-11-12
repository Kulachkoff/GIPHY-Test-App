package com.chililabs.giphytest.data.repo

import com.chililabs.giphytest.data.remote.api.GiphyApi
import com.chililabs.giphytest.domain.repo.TrendingSearchesRepository
import com.chililabs.giphytest.utils.ext.flowCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrendingSearchesRepositoryImpl @Inject constructor(
    private val giphyApi: GiphyApi
) : TrendingSearchesRepository {

    override fun getTrendingSearches(): Flow<List<String>> = flowCatching(
        message = "Failed to fetch trending searches"
    ) {
        val response = giphyApi.getTrendingSearches()
        emit(response?.data)
    }.map { it ?: emptyList() }

    override fun getAutocompleteSuggestions(query: String): Flow<List<String>> = flowCatching(
        message = "Failed to fetch autocomplete suggestions for: $query"
    ) {
        if (query.isBlank()) {
            emit(emptyList())
        } else {
            val response = giphyApi.getAutocompleteSuggestions(query)
            emit(response?.data?.map { it.name })
        }
    }.map { it ?: emptyList() }
}

