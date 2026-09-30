package com.example.sandbox.day1

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class CapstoneTest {

    @Test
    fun testPipelineSuccess() = runTest {
        val pipeline = CapstonePipeline()
        
        assertTrue(pipeline.pipelineState.value is Result.Loading)

        pipeline.runPipeline(shouldFailFirst = false)

        val state = pipeline.pipelineState.value
        assertTrue(state is Result.Success)
        assertEquals("Parsed: Raw JSON Data", (state as Result.Success).data)
    }

    @Test
    fun testPipelineRetryOnFailure() = runTest {
        val pipeline = CapstonePipeline()

        pipeline.runPipeline(shouldFailFirst = true)

        val state = pipeline.pipelineState.value
        assertTrue(state is Result.Success)
        assertEquals("Parsed: Raw JSON Data", (state as Result.Success).data)
    }

    @Test
    fun testNiAFlowPipelineSuccess() = runTest {
        val pipeline = CapstonePipeline()
        val results = pipeline.fetchStreamPipeline(fail = false).toList()

        // NiA asResult() emits [Loading, Success]
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertEquals("Parsed: NiA Flow Data", (results[1] as Result.Success).data)
    }

    @Test
    fun testNiAFlowPipelineError() = runTest {
        val pipeline = CapstonePipeline()
        val results = pipeline.fetchStreamPipeline(fail = true).toList()

        // NiA asResult() emits [Loading, Error] on exception
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        assertEquals("NiA Stream Network Error", (results[1] as Result.Error).exception.message)
    }
}
