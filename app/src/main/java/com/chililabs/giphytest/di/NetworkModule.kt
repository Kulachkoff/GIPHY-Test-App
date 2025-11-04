package com.chililabs.giphytest.di

import com.chililabs.giphytest.BuildConfig
import com.chililabs.giphytest.data.remote.api.GiphyApi
import com.chililabs.giphytest.utils.ext.stripApiKey
import com.chililabs.giphytest.utils.network.HttpInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpInterceptor(): HttpInterceptor =
        HttpInterceptor(BuildConfig.GIPHY_API_KEY)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val redactingLogger = HttpLoggingInterceptor.Logger { message ->
            val redacted = message.stripApiKey()
            Timber.tag("OkHttp").i(redacted)
        }

        return HttpLoggingInterceptor(redactingLogger).also {
            it.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        httpInterceptor: HttpInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideGiphyApi(retrofit: Retrofit): GiphyApi =
        retrofit.create(GiphyApi::class.java)
}


