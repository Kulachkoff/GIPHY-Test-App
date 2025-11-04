package com.chililabs.giphytest.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import com.chililabs.giphytest.ui.navigation.FavoritesRoute
import com.chililabs.giphytest.ui.navigation.Navigation
import com.chililabs.giphytest.ui.navigation.SearchRoute

@Composable
fun HostScreen() {
    val navController = rememberNavController()
    val items = listOf(
        SearchRoute.route to "Search",
        FavoritesRoute.route to "Favorites"
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val current by navController.currentBackStackEntryAsState()
                val currentRoute = current?.destination?.route

                items.forEach { (route, label) ->
                    val selected = currentRoute == route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(SearchRoute.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    if (route == SearchRoute.route) Icons.Default.Search
                                    else Icons.Default.Favorite,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = label) }
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