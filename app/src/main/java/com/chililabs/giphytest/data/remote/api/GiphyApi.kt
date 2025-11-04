package com.chililabs.giphytest.data.remote.api

import com.chililabs.giphytest.data.remote.model.SingleGifResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GiphyApi {
    @GET("v1/gifs/{id}")
    suspend fun getById(
        @Path("id") id: String
    ): SingleGifResponse?
}