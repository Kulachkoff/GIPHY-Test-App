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
import androidx.compose.ui.unit.dp

@Composable
fun TrendingTagsRow(
    modifier: Modifier = Modifier,
    trendingSearches: List<String>,
    selectedTag: String? = null,
    onTagClicked: (String) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
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

