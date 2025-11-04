package com.chililabs.giphytest.ui.screens.favorites

import com.chililabs.giphytest.ui.base.BaseEffect

sealed interface FavoritesEffect : BaseEffect {
    data class NavigateToDetails(val id: String) : FavoritesEffect
}