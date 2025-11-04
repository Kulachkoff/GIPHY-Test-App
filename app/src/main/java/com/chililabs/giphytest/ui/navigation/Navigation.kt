package com.chililabs.giphytest.ui.navigation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chililabs.giphytest.ui.base.BaseEffect
import com.chililabs.giphytest.ui.base.BaseViewModel
import com.chililabs.giphytest.ui.base.CommonEffect
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
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = SearchRoute.route
    ) {
        screenWithBaseVM<SearchViewModel, SearchState, SearchEvent>(
            route = SearchRoute.route,
            navController = navController,
            stateOf = { it.state },
            onEffect = { effect, _, _, navController ->
                when (effect) {
                    is SearchEffect.NavigateToDetails -> {
                        navController.navigateToDetails(effect.id)
                    }

                    else -> Unit
                }
            },
            content = { state, dispatch, _ ->
                SearchScreen(
                    state = state,
                    onEvent = dispatch
                )
            }
        )

        screenWithBaseVM<FavoritesViewModel, FavoritesState, FavoritesEvent>(
            route = FavoritesRoute.route,
            navController = navController,
            reverse = true,
            stateOf = { it.state },
            onEffect = { effect, _, _, navController ->
                when (effect) {
                    is FavoritesEffect.NavigateToDetails -> {
                        navController.navigateToDetails(effect.id)
                    }

                    else -> Unit
                }
            },
            content = { state, dispatch, _ ->
                FavoritesScreen(
                    state = state,
                    onEvent = dispatch
                )
            }
        )

        screenWithBaseVM<DetailsViewModel, DetailsState, DetailsEvent>(
            route = DetailsRoute.PATTERN,
            navController = navController,
            arguments = DetailsRoute.ARGS,
            reverse = true,
            stateOf = { it.state },
            onEffect = { effect, context, _, navController ->
                when (effect) {
                    is DetailsEffect.NavigateBack -> navController.popBackStack()
                    is DetailsEffect.ShareLink -> {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, effect.url)
                        }
                        context.startActivity(Intent.createChooser(send, "Share GIF"))
                    }

                    else -> Unit
                }
            },
            content = { state, dispatch, _ ->
                DetailsScreen(
                    state = state,
                    onEvent = dispatch
                )
            }
        )
    }
}

fun NavController.navigateToDetails(gifId: String) = navigate(DetailsRoute(gifId).build())

inline fun <reified VM : BaseViewModel<S, E>, S, E> NavGraphBuilder.screenWithBaseVM(
    route: String,
    navController: NavHostController,
    arguments: List<NamedNavArgument> = emptyList(),
    reverse: Boolean = false,
    durationMs: Int = 300,
    crossinline stateOf: (VM) -> StateFlow<S>,
    crossinline onEffect: (effect: BaseEffect, ctx: Context, entry: NavBackStackEntry, nav: NavHostController) -> Unit = { _, _, _, _ -> },
    crossinline content: @Composable (state: S, dispatch: (E) -> Unit, entry: NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { enterTx(reverse, durationMs) },
        exitTransition = { exitTx(reverse, durationMs) },
        popEnterTransition = { popEnterTx(reverse, durationMs) },
        popExitTransition = { popExitTx(reverse, durationMs) }
    ) { entry ->
        val viewModel: VM = hiltViewModel(entry)
        val state by stateOf(viewModel).collectAsStateWithLifecycle()
        val dispatch = remember(viewModel) { { e: E -> viewModel.onEvent(e) } }
        val context = LocalContext.current

        LaunchedEffect(viewModel, navController) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is CommonEffect.ShowToast -> Toast
                        .makeText(
                            context,
                            effect.message,
                            if (effect.long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
                        )
                        .show()

                    else -> Unit
                }
                onEffect(effect, context, entry, navController)
            }
        }

        content(state, dispatch, entry)
    }
}

fun NavGraphBuilder.screen(
    route: String,
    reverse: Boolean = false,
    durationMs: Int = 300,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        enterTransition = { enterTx(reverse, durationMs) },
        exitTransition = { exitTx(reverse, durationMs) },
        popEnterTransition = { popEnterTx(reverse, durationMs) },
        popExitTransition = { popExitTx(reverse, durationMs) }
    ) { entry -> content(entry) }
}

fun enterTx(reverse: Boolean, duration: Int) =
    slideInHorizontally(
        animationSpec = tween(duration),
        initialOffsetX = { w -> if (reverse) +w else -w }
    )

fun exitTx(reverse: Boolean, duration: Int) =
    slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { w -> if (reverse) +w else -w }
    )

fun popEnterTx(reverse: Boolean, duration: Int) =
    slideInHorizontally(
        animationSpec = tween(duration),
        initialOffsetX = { w -> if (reverse) +w else -w }
    )

fun popExitTx(reverse: Boolean, duration: Int) =
    slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { w -> if (reverse) +w else -w }
    )
