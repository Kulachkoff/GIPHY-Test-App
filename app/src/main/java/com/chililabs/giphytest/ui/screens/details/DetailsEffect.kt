package com.chililabs.giphytest.ui.screens.details

import com.chililabs.giphytest.ui.base.BaseEffect

sealed interface DetailsEffect : BaseEffect {
    data class ShareLink(val url: String) : DetailsEffect
    data object NavigateBack : DetailsEffect
}