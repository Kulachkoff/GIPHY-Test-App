package com.chililabs.giphytest.ui.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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

@Preview
@Composable
private fun DetailsScreenPreview() {
    DetailsScreen(
        state = DetailsState(gifId = ""),
        onEvent = {}
    )
}
