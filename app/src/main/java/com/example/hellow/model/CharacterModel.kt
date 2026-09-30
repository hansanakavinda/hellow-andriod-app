package com.example.hellow.model

data class RickCharacter(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val imageUrl: String
)

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
    data object Loading : Result<Nothing>
}
