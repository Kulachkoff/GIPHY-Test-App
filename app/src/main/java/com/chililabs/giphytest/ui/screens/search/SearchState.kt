package com.chililabs.giphytest.ui.screens.search

data class SearchState(
    val query: String = "",
    val isOnline: Boolean = true,
    val isLoadingFirstPage: Boolean = false,
    val errorMessage: String? = null
)