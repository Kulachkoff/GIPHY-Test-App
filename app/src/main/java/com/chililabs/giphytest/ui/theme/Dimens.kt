package com.chililabs.giphytest.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * UI spacing constants (paddings, corner radii, sizes, etc.)
 *
 * For Non-UI constants, use [com.chililabs.giphytest.utils.constants.Constants]
 */
object Dimens {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp

    // Screen-level spacing
    val screenHorizontal: Dp = sm
    val screenVertical: Dp = md

    // Component-specific spacing
    val searchFieldVertical: Dp = 12.dp
    val searchFieldCornerRadius: Dp = sm
    val trendingTagsHorizontal: Dp = sm
    val trendingTagsVertical: Dp = sm
    val trendingTagsSpacing: Dp = sm
    val offlineBannerVertical: Dp = xs

    // Card/Item spacing
    val cardPadding: Dp = md
    val cardCornerRadius: Dp = 12.dp
    val gridItemCornerRadius: Dp = md
    val gridItemSpacing: Dp = sm

    // Details screen
    val detailsImagePadding: Dp = md
    val detailsContentPadding: Dp = md
    val detailsSpacerHeight: Dp = sm

    // Settings screen
    val settingsScreenPadding: Dp = md
    val settingsSectionVertical: Dp = sm
    val settingsItemTop: Dp = md
    val settingsItemBottom: Dp = sm
}

