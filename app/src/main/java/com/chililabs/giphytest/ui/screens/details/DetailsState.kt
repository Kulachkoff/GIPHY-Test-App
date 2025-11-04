package com.chililabs.giphytest.ui.screens.details

import androidx.compose.runtime.Immutable
import com.chililabs.giphytest.domain.model.Gif

@Immutable
data class DetailsState(
    val gifId: String,
    val gif: Gif? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)