package com.chililabs.giphytest.ui.screens.favorites

import com.chililabs.giphytest.domain.usecase.GetFavoritesUseCase
import com.chililabs.giphytest.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavorites: GetFavoritesUseCase
) : BaseViewModel<FavoritesState, FavoritesEvent>(
    initialState = FavoritesState()
) {

    init {
        observeFavorites()
            .reduceResult(
                onSuccessState = { list ->
                    copy(
                        items = list,
                        isEmpty = list.isEmpty()
                    )
                },
                onErrorState = {
                    copy(
                        items = emptyList(),
                        isEmpty = true
                    )
                },
                toastOnError = true
            )
    }

    override suspend fun handleEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.ItemClicked ->
                sendEffect(FavoritesEffect.NavigateToDetails(event.gifId))

            FavoritesEvent.Refresh -> Unit
        }
    }
}
