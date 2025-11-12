package com.chililabs.giphytest.ui.screens.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chililabs.giphytest.ui.theme.Dimens

@Composable
fun TrendingTagsRow(
    modifier: Modifier = Modifier,
    trendingSearches: List<String>,
    selectedTag: String? = null,
    onTagClicked: (String) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.trendingTagsSpacing),
        contentPadding = PaddingValues(
            horizontal = Dimens.trendingTagsHorizontal,
            vertical = Dimens.trendingTagsVertical
        )
    ) {
        items(
            items = trendingSearches,
            key = { it }
        ) { tag ->
            FilterChip(
                selected = tag == selectedTag,
                onClick = { onTagClicked(tag) },
                label = { Text("#$tag") }
            )
        }
    }
}

