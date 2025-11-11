package com.chililabs.giphytest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chililabs.giphytest.ui.navigation.Route
import com.chililabs.giphytest.ui.navigation.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _currentRoute = MutableStateFlow<Route>(SearchRoute)
    val currentRoute: StateFlow<Route> = _currentRoute

    private val _topBarConfig = MutableStateFlow(TopBarConfig())
    val topBarConfig: StateFlow<TopBarConfig> = _topBarConfig

    fun setCurrentRoute(route: Route) {
        viewModelScope.launch {
            _currentRoute.emit(route)
            _topBarConfig.emit(TopBarConfig())
        }
    }

    fun updateTopBarConfig(config: TopBarConfig) {
        viewModelScope.launch {
            _topBarConfig.emit(config)
        }
    }
}