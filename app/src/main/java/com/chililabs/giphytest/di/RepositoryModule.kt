package com.chililabs.giphytest.di

import com.chililabs.giphytest.data.repo.FavoritesRepositoryImpl
import com.chililabs.giphytest.data.repo.GifsRepositoryImpl
import com.chililabs.giphytest.domain.repo.FavoritesRepository
import com.chililabs.giphytest.domain.repo.GifsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindGifsRepository(impl: GifsRepositoryImpl): GifsRepository

    @Binds
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository
}