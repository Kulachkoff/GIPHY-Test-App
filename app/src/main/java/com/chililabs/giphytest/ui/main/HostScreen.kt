package com.chililabs.giphytest.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chililabs.giphytest.R
import com.chililabs.giphytest.ui.main.components.AppTopBar
import com.chililabs.giphytest.ui.navigation.FavoritesRoute
import com.chililabs.giphytest.ui.navigation.Navigation
import com.chililabs.giphytest.ui.navigation.SearchRoute
import com.chililabs.giphytest.ui.navigation.SettingsRoute
import com.chililabs.giphytest.utils.ext.getTitleRes
import com.chililabs.giphytest.utils.ext.hasBottomBar
import com.chililabs.giphytest.utils.ext.hasTopBar

@Composable
fun HostScreen(
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentRoute by navigationViewModel.currentRoute.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val items = listOf(
        SearchRoute,
        FavoritesRoute
    )

    val topBarConfig by navigationViewModel.topBarConfig.collectAsState()

    Scaffold(
        topBar = {
            if (currentRoute.hasTopBar())
                AppTopBar(
                    currentRoute = currentRoute,
                    onBackClick = { navController.popBackStack() },
                    title = topBarConfig.title,
                    actions = {
                        if (currentRoute == SearchRoute) {
                            IconButton(
                                onClick = { navController.navigate(SettingsRoute.route) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = stringResource(R.string.cd_settings)
                                )
                            }
                        }
                        topBarConfig.actions.forEach { action ->
                            when (action) {
                                is TopBarAction.Share -> {
                                    IconButton(
                                        onClick = action.onClick
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = stringResource(R.string.cd_share)
                                        )
                                    }
                                }
                                is TopBarAction.Favorite -> {
                                    val icon = if (action.isFavorite) Icons.Default.Favorite
                                        else Icons.Default.FavoriteBorder
                                    val description = if (action.isFavorite) stringResource(R.string.cd_remove_from_favorites)
                                        else stringResource(R.string.cd_add_to_favorites)

                                    IconButton(
                                        onClick = action.onClick
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = description
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
        },
        bottomBar = {
            if (currentRoute.hasBottomBar()) {
                NavigationBar {
                    items.forEach { route ->
                        val selected = currentRoute == route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(route.route) {
                                    popUpTo(SearchRoute.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                val icon = if (currentRoute == route) route.selectedIcon else route.icon
                                val description = route.iconDescription

                                Icon(
                                    imageVector = icon,
                                    contentDescription = description
                                )
                            },
                            label = { Text(text = stringResource(route.getTitleRes())) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Navigation(
            modifier = Modifier.padding(padding),
            navController = navController,
            navigationViewModel = navigationViewModel
        )
    }
}

@Preview
@Composable
private fun HostScreenPreview() {
    HostScreen()
}