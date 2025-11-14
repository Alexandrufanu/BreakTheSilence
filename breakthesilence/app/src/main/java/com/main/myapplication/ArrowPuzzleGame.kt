package com.main.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ArrowPuzzleGame(modifier: Modifier = Modifier) {
    var arrowPosition by remember { mutableStateOf(Offset(200f, 400f)) }
    var isMoving by remember { mutableStateOf(false) }

    // Animation loop for moving arrow
    LaunchedEffect(isMoving) {
        if (isMoving) {
            while (isMoving) {
                delay(16) // ~60 FPS

                // Move arrow upward
                val newY = arrowPosition.y - 5f

                // Stop if arrow goes off screen
                if (newY < -50f) {
                    isMoving = false
                } else {
                    arrowPosition = arrowPosition.copy(y = newY)
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Game canvas
        Canvas(
            modifier = Modifier
                .size(400.dp)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        // Check if arrow was tapped
                        if (!isMoving) {
                            val dx = tapOffset.x - arrowPosition.x
                            val dy = tapOffset.y - arrowPosition.y
                            val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                            if (distance < 40f) {
                                isMoving = true
                            }
                        }
                    }
                }
        ) {
            // Draw background
            drawRect(
                color = Color(0xFFF5F5F5),
                size = size
            )

            // Draw arrow
            drawArrow(
                position = arrowPosition,
                color = if (isMoving) Color(0xFF4CAF50) else Color(0xFF2196F3)
            )
        }
    }
}

// Draw a simple upward-pointing arrow
fun DrawScope.drawArrow(position: Offset, color: Color) {
    val arrowSize = 40f

    // Arrow triangle
    val path = Path().apply {
        moveTo(position.x, position.y - arrowSize / 2)
        lineTo(position.x - arrowSize / 3, position.y + arrowSize / 2)
        lineTo(position.x + arrowSize / 3, position.y + arrowSize / 2)
        close()
    }

    drawPath(
        path = path,
        color = color
    )

    // Arrow outline
    drawPath(
        path = path,
        color = Color.Black,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
    )
}
