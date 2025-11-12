package com.chililabs.giphytest.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.chililabs.giphytest.R
import com.chililabs.giphytest.ui.screens.search.components.GiphyGrid
import com.chililabs.giphytest.ui.screens.search.components.OfflineBanner
import com.chililabs.giphytest.ui.screens.search.components.SearchField
import com.chililabs.giphytest.ui.screens.search.components.TrendingTagsRow
import com.chililabs.giphytest.ui.theme.Dimens

@Composable
fun SearchScreen(
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(state.query) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenHorizontal)
    ) {
        OfflineBanner(
            visible = !state.isOnline,
            text = stringResource(R.string.search_offline_message)
        )

        SearchField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.searchFieldVertical),
            value = text,
            onValueChange = {
                text = it
                onEvent(SearchEvent.QueryChanged(it))
            },
            onClear = {
                text = ""
                onEvent(SearchEvent.QueryChanged(""))
            },
            placeholder = stringResource(R.string.search_placeholder),
            singleLine = true,
            shape = RoundedCornerShape(Dimens.searchFieldCornerRadius),
            leadingIcon = Icons.Default.Search,
            trailingIcon = Icons.Default.Close,
            suggestions = state.autocompleteSuggestions,
            onSuggestionSelected = { suggestion ->
                text = suggestion
                focusManager.clearFocus()
                onEvent(SearchEvent.SuggestionClicked(suggestion))
            }
        )


        if (state.trendingSearches.isNotEmpty()) {
            TrendingTagsRow(
                modifier = Modifier.fillMaxWidth(),
                trendingSearches = state.trendingSearches,
                selectedTag = state.selectedTrendingTag,
                onTagClicked = { tag ->
                    text = tag
                    focusManager.clearFocus()
                    onEvent(SearchEvent.TrendingTagClicked(tag))
                }
            )
        }

        // Giphy SDK has a native error handling
        // It shows its own "Oh NO! Something went wrong" message with a Retry button
        GiphyGrid(
            modifier = Modifier.weight(1f),
            query = state.query,
            isOnline = state.isOnline,
            onItemSelected = { gifId -> onEvent(SearchEvent.ItemClicked(gifId)) },
            onError = { message -> onEvent(SearchEvent.ErrorOccurred(message)) }
        )
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        state = SearchState(),
        onEvent = {}
    )
}
