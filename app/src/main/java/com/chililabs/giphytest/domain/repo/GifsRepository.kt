package com.chililabs.giphytest.domain.repo

import com.chililabs.giphytest.domain.model.Gif
import kotlinx.coroutines.flow.Flow

interface GifsRepository {
    fun getById(gifId: String): Flow<Gif?>
}