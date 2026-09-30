package com.example.sandbox.day1

import org.junit.Test
import org.junit.Assert.*

class SeniorIdiomsTest {

    private val practice = SeniorIdiomsPractice()

    @Test
    fun testReifiedFilter() {
        val mixedList: List<Any> = listOf(1, "Hello", 2.0, "Kotlin", true)
        val strings: List<String> = practice.filterIsInstance<String>(mixedList)
        
        assertEquals(2, strings.size)
        assertEquals("Hello", strings[0])
        assertEquals("Kotlin", strings[1])
    }

    @Test
    fun testLazyDelegation() {
        // Access lazy property
        val value = practice.expensiveValue
        assertEquals("Initialized Expensive Resource", value)
    }

    @Test
    fun testDslBuilder() {
        val tableRows = practice.htmlTable {
            row("Header")
            row("Body Row 1")
            row("Body Row 2")
        }

        assertEquals(3, tableRows.size)
        assertEquals("Header", tableRows[0])
        assertEquals("Body Row 1", tableRows[1])
    }
}
