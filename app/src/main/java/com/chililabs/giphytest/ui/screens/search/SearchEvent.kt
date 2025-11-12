package com.chililabs.giphytest.ui.screens.search

sealed interface SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent
    data class ItemClicked(val gifId: String) : SearchEvent
    data class ErrorOccurred(val message: String) : SearchEvent
    data class TrendingTagClicked(val tag: String) : SearchEvent
    data class SuggestionClicked(val suggestion: String) : SearchEvent
    data object Retry : SearchEvent
    data object LoadTrendingSearches : SearchEvent
}