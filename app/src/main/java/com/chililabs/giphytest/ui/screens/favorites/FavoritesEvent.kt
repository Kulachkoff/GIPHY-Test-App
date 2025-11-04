package com.chililabs.giphytest.ui.screens.favorites

sealed interface FavoritesEvent {
    data object Refresh : FavoritesEvent
    data class ItemClicked(val gifId: String) : FavoritesEvent
}