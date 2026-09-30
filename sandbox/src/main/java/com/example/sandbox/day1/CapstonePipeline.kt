package com.example.sandbox.day1

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

class CapstonePipeline {

    private val _pipelineState = MutableStateFlow<Result<String>>(Result.Loading)
    val pipelineState: StateFlow<Result<String>> = _pipelineState.asStateFlow()

    /**
     * 1. Imperative Suspending Pipeline (with manual retry and timeout)
     */
    suspend fun runPipeline(
        shouldFailFirst: Boolean = false,
        timeoutMs: Long = 1000L
    ) {
        _pipelineState.value = Result.Loading

        var attempts = 0
        val maxAttempts = 3

        while (attempts < maxAttempts) {
            try {
                val rawData = withTimeout(timeoutMs.milliseconds) {
                    delay(100.milliseconds)
                    if (shouldFailFirst && attempts == 0) {
                        attempts++
                        throw IOException("Network error on attempt 1")
                    }
                    "Raw JSON Data"
                }

                val parsedResult = parseData(rawData)
                _pipelineState.value = Result.Success(parsedResult)
                return
            } catch (e: TimeoutCancellationException) {
                _pipelineState.value = Result.Error(e)
                return
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                attempts++
                if (attempts >= maxAttempts) {
                    _pipelineState.value = Result.Error(e)
                    return
                }
                delay(50.milliseconds)
            }
        }
    }

    /**
     * 2. Pure Reactive Now in Android (NiA) Flow Pipeline using asResult():
     */
    fun fetchStreamPipeline(fail: Boolean = false): Flow<Result<String>> {
        return flow {
            delay(100.milliseconds)
            if (fail) {
                throw IOException("NiA Stream Network Error")
            }
            emit("NiA Flow Data")
        }
            .map { parseData(it) }
            .asResult()
    }

    private fun parseData(raw: String): String {
        return "Parsed: $raw"
    }
}
