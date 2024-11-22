package com.siaptekno.storyapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// JUnit rule to set and reset the main coroutine dispatcher for tests
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher() // Default test dispatcher
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher to the test dispatcher
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain() // Reset the main dispatcher after tests complete
    }
}
