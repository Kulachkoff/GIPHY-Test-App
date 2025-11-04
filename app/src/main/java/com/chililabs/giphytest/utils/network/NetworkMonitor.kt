package com.chililabs.giphytest.utils.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor { val isOnline: StateFlow<Boolean> }