package com.chililabs.giphytest.ui.screens.details

sealed interface DetailsEvent {
    data object Load : DetailsEvent
    data object Share : DetailsEvent
    data object Retry : DetailsEvent
    data object Favorite : DetailsEvent
    data object Back : DetailsEvent
}