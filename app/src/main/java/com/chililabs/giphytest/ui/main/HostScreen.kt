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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chililabs.giphytest.ui.main.components.AppTopBar
import com.chililabs.giphytest.ui.navigation.FavoritesRoute
import com.chililabs.giphytest.ui.navigation.Navigation
import com.chililabs.giphytest.ui.navigation.SearchRoute
import com.chililabs.giphytest.ui.navigation.SettingsRoute
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
                            IconButton(onClick = { navController.navigate(SettingsRoute.route) }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                        topBarConfig.actions.forEach { action ->
                            when (action) {
                                is TopBarAction.Share -> {
                                    IconButton(onClick = action.onClick) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share"
                                        )
                                    }
                                }
                                is TopBarAction.Favorite -> {
                                    IconButton(onClick = action.onClick) {
                                        Icon(
                                            imageVector = if (action.isFavorite) {
                                                Icons.Default.Favorite
                                            } else {
                                                Icons.Default.FavoriteBorder
                                            },
                                            contentDescription = if (action.isFavorite) {
                                                "Remove from favorites"
                                            } else {
                                                "Add to favorites"
                                            }
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
                            label = { Text(text = route.title) }
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