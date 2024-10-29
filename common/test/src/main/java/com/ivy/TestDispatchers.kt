package com.ivy

import com.ivy.core.domain.pure.util.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatchers(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
): DispatcherProvider {
    override val main: CoroutineDispatcher
        // use the testDispatcher for each one:
        get() = testDispatcher
    override val io: CoroutineDispatcher
        // use the testDispatcher for each one:
        get() = testDispatcher
    override val default: CoroutineDispatcher
        // use the testDispatcher for each one:
        get() = testDispatcher
    override val unconfined: CoroutineDispatcher
        // use the testDispatcher for each one:
        get() = testDispatcher
}