package com.chililabs.giphytest.ui.screens.favorites

import com.chililabs.giphytest.domain.model.Gif

data class FavoritesState(
    val items: List<Gif> = emptyList(),
    val isEmpty: Boolean = true
)