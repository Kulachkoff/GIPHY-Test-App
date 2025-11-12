package com.chililabs.giphytest.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.chililabs.giphytest.domain.usecase.GetAutocompleteSuggestionsUseCase
import com.chililabs.giphytest.domain.usecase.GetTrendingSearchesUseCase
import com.chililabs.giphytest.ui.base.BaseViewModel
import com.chililabs.giphytest.utils.Constants.AUTOCOMPLETE_DEBOUNCE_MS
import com.chililabs.giphytest.utils.Constants.SEARCH_DEBOUNCE_MS
import com.chililabs.giphytest.utils.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    private val savedStateHandle: SavedStateHandle,
    private val getTrendingSearchesUseCase: GetTrendingSearchesUseCase,
    private val getAutocompleteSuggestionsUseCase: GetAutocompleteSuggestionsUseCase
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

        queries
            .debounce(AUTOCOMPLETE_DEBOUNCE_MS)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    updateState { copy(autocompleteSuggestions = emptyList()) }
                }
            }
            .flatMapLatest { query ->
                if (query.isNotBlank()) {
                    getAutocompleteSuggestionsUseCase(query)
                } else {
                    emptyFlow()
                }
            }
            .reduceResult(
                onSuccessState = { suggestions ->
                    copy(autocompleteSuggestions = suggestions)
                },
                onErrorState = {
                    copy(autocompleteSuggestions = emptyList())
                }
            )

        val restored: String = savedStateHandle["query"] ?: ""
        queries.value = restored

        loadTrendingSearches()
    }

    override suspend fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> onQueryChanged(event.query)

            is SearchEvent.ItemClicked -> sendEffect(SearchEffect.NavigateToDetails(event.gifId))

            is SearchEvent.ErrorOccurred -> Unit

            is SearchEvent.Retry -> Unit

            is SearchEvent.TrendingTagClicked -> onTagSelected(event.tag)

            is SearchEvent.SuggestionClicked -> onSuggestionClicked(event.suggestion)

            is SearchEvent.LoadTrendingSearches -> loadTrendingSearches()
        }
    }

    private fun onQueryChanged(query: String) {
        viewModelScope.launch {
            queries.emit(query)
            // Clear selected tag if query doesn't match any trending tag or is blank
            val currentSelectedTag = state.value.selectedTrendingTag
            if (currentSelectedTag != null && (query.isBlank() || query != currentSelectedTag)) {
                updateState { copy(selectedTrendingTag = null) }
            }
        }
    }

    private fun onTagSelected(tag: String) {
        viewModelScope.launch {
            if (state.value.selectedTrendingTag == tag) return@launch

            queries.emit(tag)
            updateState {
                copy(
                    query = tag,
                    selectedTrendingTag = tag
                )
            }
        }
    }

    private fun onSuggestionClicked(suggestion: String) {
        viewModelScope.launch {
            queries.emit(suggestion)
            updateState {
                copy(
                    query = suggestion,
                    selectedTrendingTag = null
                )
            }
        }
    }

    private fun loadTrendingSearches() {
        getTrendingSearchesUseCase()
            .reduceResult(
                onSuccessState = { trendingSearches ->
                    copy(trendingSearches = trendingSearches)
                },
                onErrorState = {
                    copy(trendingSearches = emptyList())
                },
                toastOnError = true
            )
    }
}