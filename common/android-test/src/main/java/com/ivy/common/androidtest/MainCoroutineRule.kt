package com.ivy.common.androidtest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutineRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    // called before a test
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    // called after a test
    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}