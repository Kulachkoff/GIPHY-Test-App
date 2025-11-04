package com.chililabs.giphytest.data.mapper

import com.chililabs.giphytest.data.local.entity.FavoriteGifEntity
import com.chililabs.giphytest.data.remote.model.GifDto
import com.chililabs.giphytest.domain.model.Gif

fun GifDto.toDomain(): Gif {
    return Gif(
        id = this.id,
        title = this.title,
        username = this.username,
        url = this.url
    )
}

fun FavoriteGifEntity.toDomain(): Gif {
    return Gif(
        id = this.id,
        title = this.title,
        username = this.username
    )
}

fun List<FavoriteGifEntity>.toDomainList(): List<Gif> {
    return this.map { it.toDomain() }
}

fun Gif.toEntity(): FavoriteGifEntity {
    return FavoriteGifEntity(
        id = this.id,
        title = this.title,
        username = this.username
    )
}