package com.chililabs.giphytest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_gifs")
data class FavoriteGifEntity(
    @PrimaryKey val id: String,
    val title: String? = null,
    val username: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)