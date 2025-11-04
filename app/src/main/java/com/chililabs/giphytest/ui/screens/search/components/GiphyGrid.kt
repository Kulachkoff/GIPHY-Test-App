package com.chililabs.giphytest.ui.screens.search.components

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.GiphyLoadingProvider
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GiphyGridView

@Composable
fun GiphyGrid(
    modifier: Modifier = Modifier,
    query: String,
    isOnline: Boolean,
    onItemSelected: (String) -> Unit,
    onError: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val columns = if (configuration.screenWidthDp >= 600) 3 else 2
    val vertical = configuration.orientation != Configuration.ORIENTATION_LANDSCAPE

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            GiphyGridView(context).apply {
                direction = if (vertical) GiphyGridView.VERTICAL else GiphyGridView.HORIZONTAL
                spanCount = columns
                cellPadding = 8
                setGiphyLoadingProvider(object : GiphyLoadingProvider {
                    override fun getLoadingDrawable(position: Int): Drawable {
                        return GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 16f
                            setColor(0xFFE0E0E0.toInt())
                        }
                    }
                })
                callback = object : GPHGridCallback {
                    override fun contentDidUpdate(resultCount: Int) {
                        if (resultCount == -1) onError("Failed to load GIFs")
                    }

                    override fun didSelectMedia(media: Media) {
                        onItemSelected(media.id)
                    }
                }
            }
        },
        update = { view ->
            if (!isOnline) return@AndroidView
            val content = if (query.isBlank()) GPHContent.trendingGifs
            else GPHContent.searchQuery(query, mediaType = MediaType.gif)
            view.content = content
        }
    )
}
