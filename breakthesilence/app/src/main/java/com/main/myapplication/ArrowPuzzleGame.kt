package com.main.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import kotlin.math.sqrt

// Arrow data class
data class Arrow(
    val position: Offset,
    val isMoving: Boolean = false,
    val size: Float = 40f,
    val speed: Float = 1f,
    val initialPosition: Offset = position  // Store the starting position
) {
    fun move(): Arrow {
        return if (isMoving) {
            // Keep initialPosition when moving
            copy(position = position.copy(y = position.y - speed))
        } else {
            this
        }
    }
    
    fun isOffScreen(): Boolean {
        return position.y < -50f
    }
    
    fun contains(tapPosition: Offset): Boolean {
        val dx = tapPosition.x - position.x
        val dy = tapPosition.y - position.y
        val distance = sqrt(dx * dx + dy * dy)
        return distance < size
    }
    
    fun reset(): Arrow {
        return copy(position = initialPosition, isMoving = false)
    }
}

@Composable
fun ArrowPuzzleGame(modifier: Modifier = Modifier) {
    var arrow by remember { mutableStateOf(Arrow(position = Offset(200f, 400f))) }

    // Animation loop for moving arrow
    LaunchedEffect(arrow.isMoving) {
        if (arrow.isMoving) {
            while (arrow.isMoving && !arrow.isOffScreen()) {
                delay(1) // ~60 FPS
                arrow = arrow.move()
            }
            
            // Stop when off screen
            if (arrow.isOffScreen()) {
                arrow = arrow.copy(isMoving = false)
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
                        if (!arrow.isMoving && arrow.contains(tapOffset)) {
                            arrow = arrow.copy(isMoving = true)
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
                arrow = arrow,
                color = if (arrow.isMoving) Color(0xFF4CAF50) else Color(0xFF2196F3)
            )
        }
        
        // Reset button
        Button(
            onClick = {
                arrow = arrow.reset()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Reset")
        }
    }
}

// Draw a simple upward-pointing arrow
fun DrawScope.drawArrow(arrow: Arrow, color: Color) {
    // Arrow triangle
    val path = Path().apply {
        moveTo(arrow.position.x, arrow.position.y - arrow.size / 2)
        lineTo(arrow.position.x - arrow.size / 3, arrow.position.y + arrow.size / 2)
        lineTo(arrow.position.x + arrow.size / 3, arrow.position.y + arrow.size / 2)
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

