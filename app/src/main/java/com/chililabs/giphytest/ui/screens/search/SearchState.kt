package com.chililabs.giphytest.ui.screens.search

import androidx.compose.runtime.Immutable

@Immutable
data class SearchState(
    val query: String = "",
    val isOnline: Boolean = true
)