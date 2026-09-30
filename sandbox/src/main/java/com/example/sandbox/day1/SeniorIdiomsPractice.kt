package com.example.sandbox.day1

/**
 * Module 4: Senior Kotlin Idioms Practice
 * Topics: inline/reified, property delegation, and DSL builders.
 */
class SeniorIdiomsPractice {

    /**
     * 1. inline & reified:
     * Reified allows checking generic types at runtime (normally erased due to Java type erasure).
     */
    inline fun <reified T> filterIsInstance(list: List<Any>): List<T> {
        return list.filter { it is T }.map { it as T }
    }

    /**
     * 2. Delegation (by lazy):
     * Computes the value only on first access and caches it.
     */
    val expensiveValue: String by lazy {
        // Heavy computation simulation
        "Initialized Expensive Resource"
    }

    /**
     * 3. DSL Builder (Domain Specific Language):
     * Uses lambda with receiver to create clean, declarative builders.
     */
    class TableBuilder {
        val rows = mutableListOf<String>()

        fun row(content: String) {
            rows.add(content)
        }
    }

    fun htmlTable(block: TableBuilder.() -> Unit): List<String> {
        val builder = TableBuilder()
        builder.block()
        return builder.rows
    }
}
