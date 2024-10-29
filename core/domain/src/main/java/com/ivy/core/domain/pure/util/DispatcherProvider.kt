package com.ivy.core.domain.pure.util

import kotlinx.coroutines.CoroutineDispatcher

// combine the dispatchers our app needs:
// we can pass in a different implementation of a coroutine
// dispatcher, depending on where in our code we are.
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}