package com.chililabs.giphytest.domain.handler

sealed interface Result<out D> {
    data class Success<out D>(val data: D) : Result<D>
    data class Error<out D>(val error: Throwable) : Result<D>
}