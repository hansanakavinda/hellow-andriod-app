package com.example.hellow.network

import com.example.hellow.model.RickCharacter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RickAndMortyRepository {

    suspend fun searchCharacters(query: String): List<RickCharacter> = withContext(Dispatchers.IO) {
        val urlString = if (query.isBlank()) {
            "https://rickandmortyapi.com/api/character"
        } else {
            "https://rickandmortyapi.com/api/character/?name=${query.trim()}"
        }

        val url = URL(urlString)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
        }

        try {
            when (val responseCode = connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                    parseCharacters(responseString)
                }
                HttpURLConnection.HTTP_NOT_FOUND -> {
                    // API returns 404 when no characters match the search query
                    emptyList()
                }
                else -> {
                    throw Exception("HTTP error code: $responseCode")
                }
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseCharacters(jsonStr: String): List<RickCharacter> {
        val jsonObject = JSONObject(jsonStr)
        val resultsArray = jsonObject.getJSONArray("results")
        val list = mutableListOf<RickCharacter>()

        for (i in 0 until resultsArray.length()) {
            val item = resultsArray.getJSONObject(i)
            list.add(
                RickCharacter(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    status = item.getString("status"),
                    species = item.getString("species"),
                    imageUrl = item.getString("image")
                )
            )
        }
        return list
    }
}
