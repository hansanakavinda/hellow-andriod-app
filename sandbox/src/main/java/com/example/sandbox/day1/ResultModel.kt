package com.example.sandbox.day1

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Module 1 & 2 Practice: Result sealed interface & Now in Android (NiA) pattern.
 * North Star: Google's Now in Android (NiA) architecture guidelines.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
    data object Loading : Result<Nothing>
}

/**
 * NiA standard extension function to convert any Flow<T> into Flow<Result<T>>.
 * Emits Loading initially, Success when data arrives, or Error if an exception is thrown.
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = this
    .map<T, Result<T>> { Result.Success(it) }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it)) }
