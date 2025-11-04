package com.chililabs.giphytest.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Gif(
    val id: String,
    val title: String? = null,
    val username: String? = null,
    val url: String? = null
)