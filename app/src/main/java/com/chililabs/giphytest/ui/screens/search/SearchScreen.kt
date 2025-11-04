package com.chililabs.giphytest.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chililabs.giphytest.ui.screens.search.components.GiphyGrid

@Composable
fun SearchScreen(
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!state.isOnline) {
            Text(
                text = "You’re offline",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var text by rememberSaveable { mutableStateOf(state.query) }
            LaunchedEffect(state.query) {
                if (text != state.query) text = state.query
            }

            TextField(
                modifier = Modifier.weight(1f),
                value = text,
                onValueChange = {
                    text = it
                    onEvent(SearchEvent.QueryChanged(it))
                },
                placeholder = { Text("Search GIFs") },
                singleLine = true
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            GiphyGrid(
                query = state.query,
                isOnline = state.isOnline,
                onItemSelected = { id -> onEvent(SearchEvent.ItemClicked(id)) },
                onError = { msg -> /* TODO: Do we need to do anything here? */ }
            )

            if (state.isLoadingFirstPage) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.errorMessage?.let { errorMessage ->
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = errorMessage)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onEvent(SearchEvent.Retry) }
                    ) {
                        Text(text = "Retry")
                    }
                }
            }
        }
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
