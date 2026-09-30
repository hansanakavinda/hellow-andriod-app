package com.example.sandbox.day1

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

class Day1Test {

    @Test
    fun testResultSuccess() {
        val successResult: Result<String> = Result.Success("Hello Kotlin")
        
        when (successResult) {
            is Result.Success -> assertEquals("Hello Kotlin", successResult.data)
            is Result.Error -> fail("Should not be error")
            is Result.Loading -> fail("Should not be loading")
        }
    }

    @Test
    fun testResultError() {
        val errorResult: Result<String> = Result.Error(IOException("Network failure"))
        
        when (errorResult) {
            is Result.Success -> fail("Should not be success")
            is Result.Error -> assertEquals("Network failure", errorResult.exception.message)
            is Result.Loading -> fail("Should not be loading")
        }
    }

    @Test
    fun testNiAResultFlowSuccess() = runTest {
        // Create a flow emitting a successful user name
        val flow = flowOf("Hansana")
        
        // Convert to Result flow using NiA's asResult()
        val results = flow.asResult().toList()
        
        // Verify emissions: [Loading, Success("Hansana")]
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertEquals("Hansana", (results[1] as Result.Success).data)
    }

    @Test
    fun testNiAResultFlowError() = runTest {
        // Create a flow that throws an exception
        val flow = flow {
            emit("Start")
            throw IOException("Database crashed")
        }
        
        // Convert to Result flow and collect emissions
        val results = runCatching { flow.asResult().toList() }.getOrElse { emptyList() }
        
        // Verify emissions: [Loading, Success("Start"), Error]
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertTrue(results[2] is Result.Error)
        assertEquals("Database crashed", (results[2] as Result.Error).exception.message)
    }
}
