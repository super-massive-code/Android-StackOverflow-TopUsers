package com.supermassivecode.stackoverlow.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

sealed class ImageLoadState {
    object Loading : ImageLoadState()
    data class Success(val bitmap: ImageBitmap) : ImageLoadState()
    data class Error(val exception: Throwable) : ImageLoadState()
    object Idle : ImageLoadState()
}

class ImageDownloader(private val context: Context) {
    private val cacheDir = File(context.cacheDir, "image_cache")
    private val memoryCache = ConcurrentHashMap<String, ImageBitmap>()
    private val loadingJobs = ConcurrentHashMap<String, Job>()

    companion object {
        private const val MAX_MEMORY_CACHE_SIZE = 20
        private const val CONNECTION_TIMEOUT = 10000
        private const val READ_TIMEOUT = 10000
    }

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    private fun getCacheKey(url: String): String {
        return MessageDigest.getInstance("MD5")
            .digest(url.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    private fun getCacheFile(url: String): File {
        return File(cacheDir, getCacheKey(url) + ".cache")
    }

    private fun loadBitmapFromFile(file: File): Bitmap? {
        return try {
            val source = ImageDecoder.createSource(file)
            ImageDecoder.decodeBitmap(source)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun downloadImage(url: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.apply {
                connectTimeout = CONNECTION_TIMEOUT
                readTimeout = READ_TIMEOUT
                doInput = true
                useCaches = true
            }
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap ?: throw Exception("Failed to decode bitmap from stream")
            } else {
                throw Exception("HTTP ${connection.responseCode}: ${connection.responseMessage}")
            }

        } finally {
            connection.disconnect()
        }
    }

    private suspend fun saveBitmapToDiskCache(bitmap: Bitmap, file: File) = withContext(Dispatchers.IO) {
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addToMemoryCache(key: String, bitmap: ImageBitmap) {
        if (memoryCache.size >= MAX_MEMORY_CACHE_SIZE) {
            // Remove oldest entry
            memoryCache.keys.firstOrNull().let { memoryCache.remove(it) }
        }
        memoryCache[key] = bitmap
    }

    suspend fun loadImage(url: String): ImageLoadState {
        val cacheKey = getCacheKey(url)

        // Check memory cache first
        memoryCache[cacheKey]?.let {
            return ImageLoadState.Success(it)
        }

        // Check disk cache
        val cacheFile = getCacheFile(url)
        if (cacheFile.exists()) {
            loadBitmapFromFile(cacheFile)?.let { bitmap ->
                val imageBitmap = bitmap.asImageBitmap()
                addToMemoryCache(cacheKey, imageBitmap)
                return ImageLoadState.Success(imageBitmap)
            }
        }

        // Download from network
        return try {
            val bitmap = downloadImage(url)
            val imageBitmap = bitmap.asImageBitmap()

            addToMemoryCache(cacheKey, imageBitmap)
            saveBitmapToDiskCache(bitmap, cacheFile)

            ImageLoadState.Success(imageBitmap)
        } catch (e: Exception) {
            ImageLoadState.Error(e)
        }
    }

    fun loadImageForCompose(
        url: String,
        coroutineScope: CoroutineScope
    ): MutableState<ImageLoadState> {
        val state = mutableStateOf<ImageLoadState>(ImageLoadState.Idle)

        // Cancel previous jor if running
        loadingJobs[url]?.cancel()

        state.value = ImageLoadState.Loading

        val job = coroutineScope.launch {
            try {
                val result = loadImage(url)
                ensureActive()
                state.value = result
            } catch (e: CancellationException) {
                // do nowt
            } catch (e: Exception) {
                ensureActive()
                state.value = ImageLoadState.Error(e)
            } finally {
                loadingJobs.remove(url)
            }
        }

        loadingJobs[url] = job
        return state
    }
}