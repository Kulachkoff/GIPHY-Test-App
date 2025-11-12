package com.chililabs.giphytest.domain.repo

import kotlinx.coroutines.flow.Flow

interface TrendingSearchesRepository {
    fun getTrendingSearches(): Flow<List<String>>
    fun getAutocompleteSuggestions(query: String): Flow<List<String>>
}

