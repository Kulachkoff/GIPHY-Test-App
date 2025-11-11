package com.chililabs.giphytest.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chililabs.giphytest.ui.navigation.DetailsRoute
import com.chililabs.giphytest.ui.navigation.FavoritesRoute
import com.chililabs.giphytest.ui.navigation.Navigation
import com.chililabs.giphytest.ui.navigation.SearchRoute

@Composable
fun HostScreen() {
    val navController = rememberNavController()
    val items = listOf(
        SearchRoute,
        FavoritesRoute
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val current by navController.currentBackStackEntryAsState()
                val currentRoute = current?.destination?.route

                items.forEach { route ->
                    val selected = currentRoute == route.route
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
                            when (route) {
                                is SearchRoute -> Icons.Default.Search
                                is FavoritesRoute -> {
                                    if (currentRoute == route.route) Icons.Default.Favorite
                                    else Icons.Default.FavoriteBorder
                                }
                                is DetailsRoute -> null
                            }?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = null
                                )
                            }
                        },
                        label = { Text(text = route.title) }
                    )
                }
            }
        }
    ) { padding ->
        Navigation(
            modifier = Modifier.padding(padding),
            navController = navController
        )
    }
}

@Preview
@Composable
private fun HostScreenPreview() {
    HostScreen()
}