package com.example.sistemgestiondeportiva.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun FuturisticBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF081225),
                        Color(0xFF0A0E1A),
                        Color(0xFF101B3A)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1200f, 2000f)
                )
            )
    ) {
        // Subtle neon glows
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.7f }
        ) {
            // Top-right cyan glow
            drawCircle(
                color = primary.copy(alpha = 0.18f),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width * 1.05f, size.height * 0.05f)
            )
            // Bottom-left purple glow
            drawCircle(
                color = secondary.copy(alpha = 0.12f),
                radius = size.minDimension * 0.7f,
                center = Offset(size.width * -0.1f, size.height * 1.05f)
            )
        }

        Surface(
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            content()
        }
    }
}
