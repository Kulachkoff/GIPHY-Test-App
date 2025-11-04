package com.chililabs.giphytest.utils.network

interface NetworkStateManager {
    suspend fun <T> tryWithConnection(block: suspend () -> T): T
}