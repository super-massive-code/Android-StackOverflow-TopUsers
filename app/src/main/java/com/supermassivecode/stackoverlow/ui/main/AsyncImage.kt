package com.supermassivecode.stackoverlow.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.supermassivecode.stackoverlow.data.local.ImageDownloader
import com.supermassivecode.stackoverlow.data.local.ImageLoadState


@Composable
fun rememberImageLoader(): ImageDownloader {
    val context = LocalContext.current
    return remember { ImageDownloader(context) }
}

@Composable
fun rememberImageLoadState(
    url: String,
    imageDownloader: ImageDownloader = rememberImageLoader()
): MutableState<ImageLoadState> {
    val coroutineScope = rememberCoroutineScope()
    return remember(url) {
        imageDownloader.loadImageForCompose(url, coroutineScope)
    }
}


@Composable
fun AsyncImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    imageDownloader: ImageDownloader = rememberImageLoader(),
    loading: @Composable () -> Unit = {
        Box(modifier = modifier) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    },
    error: @Composable (Throwable) -> Unit = { exception ->
        Box(modifier = modifier) {
            Text(
                text = "Failed to load image",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    },
    success: @Composable (ImageBitmap) -> Unit = { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
) {
    val imageState by rememberImageLoadState(url, imageDownloader)

    when (val state = imageState) {
        is ImageLoadState.Loading -> loading()
        is ImageLoadState.Success -> success(state.bitmap)
        is ImageLoadState.Error -> error(state.exception)
        is ImageLoadState.Idle -> loading()
    }
}
