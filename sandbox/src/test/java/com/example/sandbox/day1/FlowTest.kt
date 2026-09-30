package com.example.sandbox.day1

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class FlowTest {

    private val flowPractice = FlowPractice()

    @Test
    fun testColdFlow() = runTest {
        val list = flowPractice.createColdFlow().toList()
        assertEquals(listOf(1, 2, 3), list)
    }

    @Test
    fun testStateFlow() = runTest {
        // Initial state
        assertEquals("Loading", flowPractice.uiState.value)

        // Update state
        flowPractice.updateState("Success")
        assertEquals("Success", flowPractice.uiState.value)
    }

    @Test
    fun testCombineOperator() = runTest {
        val flow1 = flowOf("Item", "Product")
        val flow2 = flowOf(1, 2)

        val combinedFlow = flowPractice.combineFlows(flow1, flow2)
        val resultList = combinedFlow.toList()

        assertTrue(resultList.isNotEmpty())
    }
}
