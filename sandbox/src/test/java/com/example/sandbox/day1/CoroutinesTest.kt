package com.example.sandbox.day1

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class CoroutinesTest {

    private val practice = CoroutinesPractice()

    @Test
    fun testPerformParallelTasks() = runTest {
        val result = practice.performParallelTasks()
        assertEquals("Result 1 & Result 2", result)
    }

    @Test
    fun testPerformResilientTasks() = runTest {
        val (goodData, error) = practice.performResilientTasks()
        
        // Good task succeeded even though the other task failed (thanks to supervisorScope)
        assertEquals("Safe Data", goodData)
        assertNotNull(error)
        assertTrue(error is IllegalStateException)
        assertEquals("Task failed!", error?.message)
    }

    @Test
    fun testFetchDataFromNetwork() = runTest {
        val data = practice.fetchDataFromNetwork()
        assertEquals("Network DataResponse", "Network Data Response", data)
    }
}
