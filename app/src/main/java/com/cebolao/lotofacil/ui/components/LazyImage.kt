package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Lazy-loaded image component for optimized rendering.
 * Delays loading to reduce initial render time.
 * 
 * @param painterResourceId The drawable resource ID
 * @param contentDescription Accessibility description
 * @param modifier UI modifier
 * @param contentScale How to scale the image
 * @param delayMillis Delay before loading (default: 50ms)
 * @param showPlaceholder Show loading indicator
 */
@Composable
fun LazyImage(
    painterResourceId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    delayMillis: Long = 50L,
    showPlaceholder: Boolean = false
) {
    var isLoaded by remember { mutableStateOf(false) }
    
    LaunchedEffect(painterResourceId) {
        delay(delayMillis)
        isLoaded = true
    }
    
    AnimatedVisibility(
        visible = isLoaded,
        enter = fadeIn(),
        modifier = modifier
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = painterResourceId),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
    }
    
    if (!isLoaded && showPlaceholder) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier,
                strokeWidth = 2.dp
            )
        }
    }
}
