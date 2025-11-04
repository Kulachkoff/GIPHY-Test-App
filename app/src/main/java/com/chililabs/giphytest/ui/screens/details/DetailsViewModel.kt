package com.chililabs.giphytest.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.usecase.GetGifByIdUseCase
import com.chililabs.giphytest.domain.usecase.IsFavoriteUseCase
import com.chililabs.giphytest.domain.usecase.ToggleFavoriteUseCase
import com.chililabs.giphytest.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getGifById: GetGifByIdUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val isFavorite: IsFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DetailsState, DetailsEvent>(
    initialState = DetailsState(
        gifId = requireNotNull(savedStateHandle["gifId"]) { "gifId required" }
    )
) {
    init {
        onEvent(DetailsEvent.Load)
    }

    override suspend fun handleEvent(event: DetailsEvent) {
        when (event) {
            DetailsEvent.Load,
            DetailsEvent.Retry -> load()
            DetailsEvent.Share -> state.value.gif?.url?.let { sendEffect(DetailsEffect.ShareLink(it)) }
            DetailsEvent.Back -> sendEffect(DetailsEffect.NavigateBack)
            DetailsEvent.Favorite -> toggleFavorite()
        }
    }

    private suspend fun load() {
        updateState { copy(isLoading = true, errorMessage = null) }

        val gifId = state.value.gifId
        val favoriteStatus = isFavorite(gifId)

        getGifById(gifId)
            .reduceResult(
                onSuccessState = { gif ->
                    copy(
                        gif = gif,
                        isLoading = false,
                        errorMessage = null,
                        isFavorite = favoriteStatus
                    )
                },
                onErrorState = { e ->
                    copy(
                        gif = null,
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load",
                        isFavorite = favoriteStatus
                    )
                },
                toastOnError = true
            )
    }

    fun toggleFavorite() = viewModelScope.launch {
        val currentGif = state.value.gif
        val gif = currentGif ?: Gif(id = state.value.gifId)
        val nowFavorite = toggleFavorite(gif)
        updateState { copy(isFavorite = nowFavorite) }
    }
}
