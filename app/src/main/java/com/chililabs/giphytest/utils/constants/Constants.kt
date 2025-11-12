package com.chililabs.giphytest.utils.constants

/**
 * Non-UI constants (timing, configuration, etc.)
 *
 * For UI dimensions, use [com.chililabs.giphytest.ui.theme.Dimens]
 */
object Constants {
    // Timing constants
    const val SEARCH_DEBOUNCE_MS = 500L
    const val AUTOCOMPLETE_DEBOUNCE_MS = 300L

    // Grid configuration (non-UI dimensions)
    const val GRID_THREE_COLUMNS_MIN_WIDTH_DP = 600

    // Grid UI values (for AndroidView compatibility)
    // Note: These are used with Giphy SDK's AndroidView which requires Int/Float
    const val GRID_CELL_PADDING = 8
    const val GRID_ITEM_CORNER_RADIUS = 16f
    const val GRID_PLACEHOLDER_COLOR = 0xFFE0E0E0.toInt()
}