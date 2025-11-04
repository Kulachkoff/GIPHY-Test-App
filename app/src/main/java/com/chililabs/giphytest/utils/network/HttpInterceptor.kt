package com.chililabs.giphytest.utils.network

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class HttpInterceptor(
    private val apiKey: String,
    private val hostFilter: (HttpUrl) -> Boolean = { url ->
        url.host.endsWith("giphy.com")
    }
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url

        if (!hostFilter(url) || url.queryParameter("api_key") != null) {
            return chain.proceed(request)
        }

        val newUrl = url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()

        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}