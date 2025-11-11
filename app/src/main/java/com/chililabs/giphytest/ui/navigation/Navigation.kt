package com.chililabs.giphytest.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.chililabs.giphytest.ui.main.NavigationViewModel
import com.chililabs.giphytest.ui.main.TopBarAction
import com.chililabs.giphytest.ui.main.TopBarConfig
import com.chililabs.giphytest.ui.screens.details.DetailsEffect
import com.chililabs.giphytest.ui.screens.details.DetailsEvent
import com.chililabs.giphytest.ui.screens.details.DetailsScreen
import com.chililabs.giphytest.ui.screens.details.DetailsState
import com.chililabs.giphytest.ui.screens.details.DetailsViewModel
import com.chililabs.giphytest.ui.screens.favorites.FavoritesEffect
import com.chililabs.giphytest.ui.screens.favorites.FavoritesEvent
import com.chililabs.giphytest.ui.screens.favorites.FavoritesScreen
import com.chililabs.giphytest.ui.screens.favorites.FavoritesState
import com.chililabs.giphytest.ui.screens.favorites.FavoritesViewModel
import com.chililabs.giphytest.ui.screens.search.SearchEffect
import com.chililabs.giphytest.ui.screens.search.SearchEvent
import com.chililabs.giphytest.ui.screens.search.SearchScreen
import com.chililabs.giphytest.ui.screens.search.SearchState
import com.chililabs.giphytest.ui.screens.search.SearchViewModel
import com.chililabs.giphytest.utils.ext.screenWithBaseVM

typealias MainRoute = Route.Main
typealias SearchRoute = Route.Main.Tab.Search
typealias FavoritesRoute = Route.Main.Tab.Favorites
typealias DetailsRoute = Route.Main.Common.Details

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainRoute.route
    ) {
        mainGraph(navController, navigationViewModel)
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel
) {
    navigation(
        startDestination = SearchRoute.route,
        route = MainRoute.route
    ) {
        tabsGraph(navController, navigationViewModel)
        detailsGraph(navController, navigationViewModel)
    }
}

fun NavGraphBuilder.tabsGraph(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel
) {
    screenWithBaseVM<SearchViewModel, SearchState, SearchEvent>(
        route = SearchRoute.route,
        navController = navController,
        stateOf = { it.state },
        onEffect = { effect, _, _, navHostController ->
            when (effect) {
                is SearchEffect.NavigateToDetails -> navHostController.navigateToDetails(effect.gifId)
                else -> Unit
            }
        }
    ) { state, onEvent, _ ->
        LaunchedEffect(Unit) {
            navigationViewModel.setCurrentRoute(SearchRoute)
        }

        SearchScreen(state = state, onEvent = onEvent)
    }

    screenWithBaseVM<FavoritesViewModel, FavoritesState, FavoritesEvent>(
        route = FavoritesRoute.route,
        navController = navController,
        reverse = true,
        stateOf = { it.state },
        onEffect = { effect, _, _, navHostControllerav ->
            when (effect) {
                is FavoritesEffect.NavigateToDetails -> navHostControllerav.navigateToDetails(effect.gifId)
                else -> Unit
            }
        }
    ) { state, onEvent, _ ->
        LaunchedEffect(Unit) {
            navigationViewModel.setCurrentRoute(FavoritesRoute)
        }

        FavoritesScreen(state = state, onEvent = onEvent)
    }
}

fun NavGraphBuilder.detailsGraph(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel
) {
    screenWithBaseVM<DetailsViewModel, DetailsState, DetailsEvent>(
        route = DetailsRoute.route,
        navController = navController,
        arguments = DetailsRoute.args,
        reverse = true,
        stateOf = { it.state },
        onEffect = { effect, context, _, navHostController ->
            when (effect) {
                is DetailsEffect.NavigateBack -> navHostController.popBackStack()
                is DetailsEffect.ShareLink -> {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.url)
                    }
                    context.startActivity(Intent.createChooser(send, "Share GIF"))
                }
                else -> Unit
            }
        }
    ) { state, onEvent, _ ->
        LaunchedEffect(Unit) {
            navigationViewModel.setCurrentRoute(DetailsRoute)
        }

        // Update top bar with dynamic content from DetailsState
        LaunchedEffect(state.gif, state.isFavorite) {
            val title = state.gif?.title ?: "Details"
            val actions = buildList {
                state.gif?.url?.let {
                    add(TopBarAction.Share(onClick = { onEvent(DetailsEvent.Share) }))
                }
                add(
                    TopBarAction.Favorite(
                        isFavorite = state.isFavorite,
                        onClick = { onEvent(DetailsEvent.Favorite) }
                    )
                )
            }
            navigationViewModel.updateTopBarConfig(
                TopBarConfig(title = title, actions = actions)
            )
        }

        DetailsScreen(
            state = state,
            onEvent = onEvent
        )
    }
}

fun NavController.navigateToDetails(gifId: String) = navigate(DetailsRoute.build(gifId))