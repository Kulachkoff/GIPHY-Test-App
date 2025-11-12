package com.chililabs.giphytest.ui.screens.search

import androidx.compose.runtime.Immutable

@Immutable
data class SearchState(
    val query: String = "",
    val isOnline: Boolean = true,
    val trendingSearches: List<String> = emptyList(),
    val autocompleteSuggestions: List<String> = emptyList(),
    val selectedTrendingTag: String? = null
)