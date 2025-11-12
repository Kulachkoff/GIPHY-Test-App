package com.chililabs.giphytest.ui.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.chililabs.giphytest.R
import com.chililabs.giphytest.ui.theme.Dimens
import com.chililabs.giphytest.utils.constants.Constants.GRID_THREE_COLUMNS_MIN_WIDTH_DP
import com.giphy.sdk.ui.views.GPHMediaView

@Composable
fun FavoritesScreen(
    state: FavoritesState,
    onEvent: (FavoritesEvent) -> Unit
) {
    val columns = if (LocalConfiguration.current.screenWidthDp >= GRID_THREE_COLUMNS_MIN_WIDTH_DP) 3 else 2

    if (state.isEmpty) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.favorites_empty))
        }
        return
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(Dimens.gridItemSpacing),
        verticalArrangement = Arrangement.spacedBy(Dimens.gridItemSpacing),
        horizontalArrangement = Arrangement.spacedBy(Dimens.gridItemSpacing)
    ) {
        items(
            items = state.items,
            key = { it.id }
        ) { gif ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(Dimens.cardCornerRadius))
                    .clickable { onEvent(FavoritesEvent.ItemClicked(gif.id)) }
            ) {
                AndroidView(
                    modifier = Modifier.matchParentSize(),
                    factory = { context ->
                        GPHMediaView(context).apply {
                            isClickable = true
                            setOnClickListener { onEvent(FavoritesEvent.ItemClicked(gif.id)) }
                        }
                    },
                    update = { view ->
                        if (view.tag != gif.id) {
                            view.setMediaWithId(gif.id)
                            view.tag = gif.id
                            view.setOnClickListener { onEvent(FavoritesEvent.ItemClicked(gif.id)) }
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun FavoritesScreenPreview() {
    FavoritesScreen(
        state = FavoritesState(),
        onEvent = {}
    )
}