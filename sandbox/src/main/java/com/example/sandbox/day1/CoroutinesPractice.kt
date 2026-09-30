package com.example.sandbox.day1

import kotlinx.coroutines.*

/**
 * Module 2: Coroutines Deep Dive Practice
 * Topics: Structured Concurrency, supervisorScope, cancellation, exception handling, and Dispatchers.
 */
class CoroutinesPractice {

    /**
     * 1. Structured Concurrency with coroutineScope:
     * If any child task fails, the entire scope fails and cancels sibling tasks.
     */
    suspend fun performParallelTasks(): String = coroutineScope {
        val deferred1 = async {
            delay(100)
            "Result 1"
        }
        val deferred2 = async {
            delay(150)
            "Result 2"
        }
        
        // Await both results
        "${deferred1.await()} & ${deferred2.await()}"
    }

    /**
     * 2. supervisorScope:
     * Child failures do NOT propagate to cancel sibling tasks.
     */
    suspend fun performResilientTasks(): Pair<String?, Throwable?> = supervisorScope {
        val deferredGood = async {
            delay(100)
            "Safe Data"
        }
        
        val deferredBad = async {
            delay(50)
            throw IllegalStateException("Task failed!")
        }

        val goodResult = runCatching { deferredGood.await() }.getOrNull()
        val badResult = runCatching { deferredBad.await() }.exceptionOrNull()

        Pair(goodResult, badResult)
    }

    /**
     * 3. Using Dispatchers (Dispatchers.IO):
     * withContext switches the thread context for blocking operations (Network/Disk).
     */
    suspend fun fetchDataFromNetwork(): String = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(200)
        "Network Data Response"
    }
}
