package com.chililabs.giphytest.ui.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.giphy.sdk.ui.views.GPHMediaView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    onEvent: (DetailsEvent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(text = state.gif?.title ?: "GIF") },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(DetailsEvent.Back) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(DetailsEvent.Share) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }

                    IconButton(
                        onClick = { onEvent(DetailsEvent.Favorite) }
                    ) {
                        val icon =
                            if (state.isFavorite) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder
                        val contentDescription = if (state.isFavorite) "Unfavorite" else "Favorite"

                        Icon(
                            imageVector = icon,
                            contentDescription = contentDescription
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
            key(state.gifId) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .aspectRatio(1f)
                        .padding(16.dp),
                    factory = { context ->
                        GPHMediaView(context).apply {
                            setMediaWithId(
                                state.gif?.id ?: state.gifId
                            )
                        }
                    }
                )
            }

            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                state.gif?.username?.let {
                    Text(
                        text = "By @$it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                state.gif?.url?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.errorMessage?.let { errorMessage ->
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = errorMessage)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onEvent(DetailsEvent.Retry) }
                    ) { Text(text = "Retry") }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DetailsScreenPreview() {
    DetailsScreen(
        state = DetailsState(gifId = ""),
        onEvent = {}
    )
}
