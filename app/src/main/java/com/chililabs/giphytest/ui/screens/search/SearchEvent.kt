package com.chililabs.giphytest.ui.screens.search

sealed interface SearchEvent {
    data class QueryChanged(val value: String) : SearchEvent
    data class ItemClicked(val gifId: String) : SearchEvent
    data class ErrorOccurred(val message: String) : SearchEvent
    data object Retry : SearchEvent
}