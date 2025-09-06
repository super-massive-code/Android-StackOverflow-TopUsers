package com.supermassivecode.stackoverlow.data.remote

import android.net.Uri
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.net.toUri

interface StackOverflowApiService {
    fun getUsersSortedByReputation(limit: Int): String
}

class StackOverflowApiServiceImpl(
    private val baseUri: Uri = "https://api.stackexchange.com/2.2/".toUri(),
    private val connectTimeoutMs: Int = 10_000,
    private val readTimeoutMs: Int = 10_000,
): StackOverflowApiService {

    override fun getUsersSortedByReputation(limit: Int): String {
        val url = baseUri.buildUpon()
            .appendPath("users")
            .appendQueryParameter("page", "1")
            .appendQueryParameter("pagesize", limit.toString())
            .appendQueryParameter("order", "desc")
            .appendQueryParameter("sort", "reputation")
            .appendQueryParameter("site", "stackoverflow")
            .build()
            .toString()
        return makeGetRequest(url)
    }

    private fun makeGetRequest(urlString: String): String {
        val url = URL(urlString)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = connectTimeoutMs
            readTimeout = readTimeoutMs
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                throw IOException("HTTP error code: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }
}