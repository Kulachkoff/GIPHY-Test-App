package com.chililabs.giphytest.data.remote.model

data class AutocompleteResponse(
    val data: List<AutocompleteItem>
)

data class AutocompleteItem(
    val name: String
)

