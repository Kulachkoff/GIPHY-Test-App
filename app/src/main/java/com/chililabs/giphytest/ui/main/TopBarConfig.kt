package com.chililabs.giphytest.ui.main

sealed interface TopBarAction {
    data class Share(val onClick: () -> Unit) : TopBarAction
    data class Favorite(val isFavorite: Boolean, val onClick: () -> Unit) : TopBarAction
}

data class TopBarConfig(
    val title: String? = null,
    val actions: List<TopBarAction> = emptyList()
)

