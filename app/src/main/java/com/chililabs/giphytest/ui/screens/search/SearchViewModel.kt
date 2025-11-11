package com.chililabs.giphytest.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.chililabs.giphytest.ui.base.BaseViewModel
import com.chililabs.giphytest.utils.Constants.SEARCH_DEBOUNCE_MS
import com.chililabs.giphytest.utils.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<SearchState, SearchEvent>(
    initialState = SearchState()
) {
    private val queries = MutableStateFlow("")

    init {
        networkMonitor.isOnline
            .onEach { online -> updateState { copy(isOnline = online) } }
            .launchIn(viewModelScope)

        queries
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .onEach { query ->
                updateState { copy(query = query) }
                savedStateHandle["query"] = query
            }
            .launchIn(viewModelScope)

        val restored: String = savedStateHandle["query"] ?: ""
        queries.value = restored
    }

    override suspend fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> queries.emit(event.value)

            is SearchEvent.ItemClicked ->
                sendEffect(SearchEffect.NavigateToDetails(event.gifId))

            is SearchEvent.ErrorOccurred -> {}

            is SearchEvent.Retry -> {}
        }
    }
}