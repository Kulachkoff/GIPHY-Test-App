package com.chililabs.giphytest.ui.screens.settings

import androidx.lifecycle.viewModelScope
import com.chililabs.giphytest.domain.usecase.GetThemeUseCase
import com.chililabs.giphytest.domain.usecase.SetThemeUseCase
import com.chililabs.giphytest.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : BaseViewModel<SettingsState, SettingsEvent>(
    initialState = SettingsState()
) {

    init {
        getThemeUseCase()
            .onEach { theme ->
                updateState { copy(theme = theme) }
            }
            .launchIn(viewModelScope)
    }

    override suspend fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ThemeChanged -> {
                setThemeUseCase(event.theme)
            }
        }
    }
}

