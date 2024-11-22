package com.siaptekno.storyapp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

// Utility function to test LiveData in unit tests
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2, // Maximum wait time
    timeUnit: TimeUnit = TimeUnit.SECONDS, // Time unit for waiting
    afterObserve: () -> Unit = {} // Optional block to execute after observing
): T {
    var data: T? = null
    val latch = CountDownLatch(1) // Synchronization aid for waiting
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value // Capture LiveData value
            latch.countDown() // Decrement latch to unblock waiting
            this@getOrAwaitValue.removeObserver(this) // Remove observer after value is set
        }
    }
    this.observeForever(observer) // Observe LiveData permanently during test

    try {
        afterObserve.invoke() // Execute optional block
        if (!latch.await(time, timeUnit)) { // Wait for LiveData to emit value
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer) // Ensure observer is removed even if test fails
    }

    @Suppress("UNCHECKED_CAST")
    return data as T // Return captured data
}
