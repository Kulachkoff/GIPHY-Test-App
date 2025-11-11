package com.chililabs.giphytest.ui.screens.search

import com.chililabs.giphytest.ui.base.BaseEffect

sealed interface SearchEffect : BaseEffect {
    data class NavigateToDetails(val gifId: String) : SearchEffect
}