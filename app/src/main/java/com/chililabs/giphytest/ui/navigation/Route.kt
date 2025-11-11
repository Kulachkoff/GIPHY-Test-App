package com.chililabs.giphytest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.chililabs.giphytest.utils.ext.capitalize

enum class ScaffoldConfig {
    FULL,
    NO_TOP_BAR,
    NO_BOTTOM_BAR,
    NO_SCAFFOLD
}

sealed class Route(
    val route: String,
    val title: String,
    val scaffoldConfig: ScaffoldConfig,
    val hasNavigationButton: Boolean,
    val args: List<NamedNavArgument> = emptyList()
) {
    data object Main : Route("main", "Main", ScaffoldConfig.FULL, false) {
        sealed class Tab(
            route: String,
            scaffoldConfig: ScaffoldConfig = ScaffoldConfig.FULL,
            hasNavigationButton: Boolean = false,
            val icon: ImageVector,
            val selectedIcon: ImageVector,
            val iconDescription: String = route,
            title: String = route.capitalize()
        ) : Route(route, title, scaffoldConfig, hasNavigationButton) {
            data object Search : Tab(
                route = "search",
                icon = Icons.Default.Search,
                selectedIcon = Icons.Default.Search
            )
            data object Favorites : Tab(
                route = "favorites",
                icon = Icons.Default.FavoriteBorder,
                selectedIcon = Icons.Default.Favorite
            )
        }

        data object Common {
            data object Details : Route(
                route = "details/{gifId}",
                title = "Details",
                scaffoldConfig = ScaffoldConfig.NO_BOTTOM_BAR,
                hasNavigationButton = true,
                args = listOf(navArgument("gifId") { type = NavType.StringType })
            ) {
                fun build(gifId: String): String = "details/$gifId"
            }
        }
    }
}