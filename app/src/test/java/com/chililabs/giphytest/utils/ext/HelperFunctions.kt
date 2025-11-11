package com.chililabs.giphytest.utils.ext

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle

internal const val DEFAULT_DEBOUNCE_MS = 500L // Should match Constants.SEARCH_DEBOUNCE_MS

@OptIn(ExperimentalCoroutinesApi::class)
internal fun TestScope.advancePastDebounce(ms: Long = DEFAULT_DEBOUNCE_MS) {
    // Need this to let VM process event emissions, advance past the debounce and update process
    advanceUntilIdle()
    advanceTimeBy(ms)
    advanceUntilIdle()
}

internal inline fun TestScope.emitAndWait(
    ms: Long = DEFAULT_DEBOUNCE_MS,
    block: () -> Unit
) {
    block()
    advancePastDebounce(ms)
}

@OptIn(ExperimentalCoroutinesApi::class)
internal inline fun TestScope.invokeAndWait(block: () -> Unit) {
    block()
    advanceUntilIdle()
}