package com.chililabs.giphytest.utils.ext

import com.chililabs.giphytest.domain.handler.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

typealias UnitFlow = Flow<Unit>

fun unitFlow(action: suspend () -> Unit): UnitFlow = flow {
    action()
    emit(Unit)
}

fun <D> Flow<D>.mapToResult(): Flow<Result<D>> =
    map<D, Result<D>> { data -> Result.Success(data) }
        .catch { error -> emit(Result.Error(error)) }
        .logOnError()


fun <D, R> Flow<Result<D>>.mapFromResult(
    transform: suspend (value: D) -> R
): Flow<R> = map { result ->
    when (result) {
        is Result.Success -> transform(result.data)
        is Result.Error -> throw result.error
    }
}

fun <D> Flow<Result<D>>.onError(action: suspend (Throwable) -> Unit): Flow<Result<D>> =
    onEach { if (it is Result.Error) action(it.error) }

fun <D> Flow<Result<D>>.logOnError(): Flow<Result<D>> =
    onError { it.message?.let { msg -> Timber.tag("Flow").e(msg) } }

fun <T> flowCatching(
    tag: String = "Flow",
    message: String = "An error occurred",
    block: suspend kotlinx.coroutines.flow.FlowCollector<T?>.() -> Unit
): Flow<T?> = flow(block).catchAndEmitNull(tag, message)

fun <T> Flow<T?>.catchAndEmitNull(
    tag: String = "Flow",
    message: String = "An error occurred"
): Flow<T?> = catch { e ->
    Timber.tag(tag).e(e, message)
    emit(null)
}

/**
 * This extension function is primarily used in the ViewModels
 * allowing to collect and work with any incoming Result data.
 *
 * If only need to update the Screen State with the Result data
 * without any transformations - consider using [com.chililabs.giphytest.ui.base.BaseViewModel.reduceResult]
 */
suspend fun <D> Flow<Result<D>>.collectResult(
    onSuccess: suspend (D) -> Unit = {},
    onError: (Throwable) -> Unit = {},
    onCompletion: (Result<D>) -> Unit = {}
) {
    collect { result ->
        onCompletion(result)
        when (result) {
            is Result.Success -> onSuccess(result.data)
            is Result.Error -> onError(result.error)
        }
    }
}