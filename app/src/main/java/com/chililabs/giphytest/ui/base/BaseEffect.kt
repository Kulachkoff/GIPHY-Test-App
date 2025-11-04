package com.chililabs.giphytest.ui.base

interface BaseEffect

sealed class CommonEffect : BaseEffect {
    data class ShowToast(
        val message: String,
        val long: Boolean = false
    ) : CommonEffect()

    object HideKeyboard : CommonEffect()
}