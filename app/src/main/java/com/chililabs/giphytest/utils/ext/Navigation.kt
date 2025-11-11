package com.chililabs.giphytest.utils.ext

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.chililabs.giphytest.ui.base.BaseEffect
import com.chililabs.giphytest.ui.base.BaseViewModel
import com.chililabs.giphytest.ui.base.CommonEffect
import kotlinx.coroutines.flow.StateFlow

inline fun <reified VM : BaseViewModel<S, E>, S, E> NavGraphBuilder.screenWithBaseVM(
    route: String,
    navController: NavHostController,
    arguments: List<NamedNavArgument> = emptyList(),
    reverse: Boolean = false,
    durationMs: Int = 300,
    crossinline stateOf: (VM) -> StateFlow<S>,
    crossinline onEffect: (effect: BaseEffect, ctx: Context, entry: NavBackStackEntry, nav: NavHostController) -> Unit = { _, _, _, _ -> },
    crossinline content: @Composable (state: S, onEvent: (E) -> Unit, entry: NavBackStackEntry) -> Unit
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
        val onEvent = remember(viewModel) { { e: E -> viewModel.onEvent(e) } }
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

        content(state, onEvent, entry)
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
