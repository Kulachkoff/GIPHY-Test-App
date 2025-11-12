package com.chililabs.giphytest.data.remote.api

import com.chililabs.giphytest.data.remote.model.SingleGifResponse
import com.chililabs.giphytest.data.remote.model.TrendingSearchesResponse
import com.chililabs.giphytest.data.remote.model.AutocompleteResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GiphyApi {
    @GET("v1/gifs/{id}")
    suspend fun getById(
        @Path("id") id: String
    ): SingleGifResponse?

    @GET("v1/trending/searches")
    suspend fun getTrendingSearches(): TrendingSearchesResponse?

    @GET("v1/gifs/search/tags")
    suspend fun getAutocompleteSuggestions(
        @Query("q") query: String
    ): AutocompleteResponse?
}