package com.chililabs.giphytest.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chililabs.giphytest.domain.handler.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S, E>(
    initialState: S
) : ViewModel() {

    // -------------------------------------- State --------------------------------------
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state
    protected fun updateState(reducer: S.() -> S) = _state.update { it.reducer() }

    // -------------------------------------- Effects --------------------------------------
    private val _effects = Channel<BaseEffect>(Channel.BUFFERED)
    val effects: Flow<BaseEffect> = _effects.receiveAsFlow()

    protected fun sendEffect(effect: BaseEffect) { _effects.trySend(effect) }
    protected fun sendToast(msg: String, long: Boolean = false) =
        sendEffect(CommonEffect.ShowToast(msg, long))

    // -------------------------------------- Events --------------------------------------
    private val events = MutableSharedFlow<E>(extraBufferCapacity = 64)
    init { events.onEach { handleEvent(it) }.launchIn(viewModelScope) }
    open fun onEvent(event: E) { events.tryEmit(event) }

    protected abstract suspend fun handleEvent(event: E)

    /**
     * This extension function is used
     * to update corresponding Screen State with the Result data.
     *
     * If need to additionally transform any incoming data and work with it further,
     * consider using [com.chililabs.giphytest.utils.ext.collectResult] extension function
     */
    protected fun <T> Flow<Result<T>>.reduceResult(
        onSuccessState: (S.(T) -> S)? = null,
        onErrorState: (S.(Throwable) -> S)? = null,
        toastOnError: Boolean = false,
        errorMessage: (Throwable) -> String = { it.message ?: "Something went wrong" },
    ) = onEach { result ->
        when (result) {
            is Result.Success -> if (onSuccessState != null) updateState { onSuccessState(result.data) }
            is Result.Error -> {
                if (onErrorState != null) updateState { onErrorState(result.error) }
                if (toastOnError) sendToast(errorMessage(result.error))
            }
        }
    }.launchIn(viewModelScope)
}