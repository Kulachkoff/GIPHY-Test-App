package com.chililabs.giphytest.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.chililabs.giphytest.data.local.entity.FavoriteGifEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteGifDao {

    @Query("SELECT * FROM favorite_gifs ORDER BY addedAt DESC")
    fun getAll(): Flow<List<FavoriteGifEntity>>

    @Upsert
    suspend fun upsert(entity: FavoriteGifEntity)

    @Query("DELETE FROM favorite_gifs WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_gifs WHERE id = :id)")
    suspend fun exists(id: String): Boolean
}