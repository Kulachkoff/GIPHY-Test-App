package com.chililabs.giphytest.di

import android.content.Context
import com.chililabs.giphytest.data.local.AppDatabase
import com.chililabs.giphytest.data.local.dao.FavoriteGifDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteGifDao =
        database.favoriteGifDao()
}