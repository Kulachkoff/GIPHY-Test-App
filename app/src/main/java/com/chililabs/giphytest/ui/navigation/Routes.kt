package com.chililabs.giphytest.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.chililabs.giphytest.utils.ext.capitalize

sealed interface AppRoute {
    val route: String
    val title: String
    val args: List<NamedNavArgument> get() = emptyList()
    fun build(): String = route
}

object SearchRoute : AppRoute {
    override val route get() = "search"
    override val title get() = route.capitalize()
}

object FavoritesRoute : AppRoute {
    override val route get() = "favorites"
    override val title get() = route.capitalize()
}

data class DetailsRoute(val gifId: String) : AppRoute {
    override val route get() = PATTERN
    override val title get() = TITLE
    override val args get() = ARGS
    override fun build(): String = "details/$gifId"

    companion object {
        const val PATTERN = "details/{gifId}"
        const val TITLE = "Details"
        val ARGS = listOf(navArgument("gifId") { type = NavType.StringType })
    }
}