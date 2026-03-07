package com.example.englishtobengaliphonix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TracingScreen()
                }
            }
        }
    }
}

@Composable
fun TracingScreen() {
    // Example SVG path (You will need the actual Bengali SVG paths here)
    // This is a simple curved path for demonstration
    val dummySvgPath = "M 200 200 Q 400 50 600 200 T 800 500"

    var userPath by remember { mutableStateOf(Path()) }
    var isTracing by remember { mutableStateOf(false) }

    // Parse the SVG string into a Compose Path
    val letterPath = remember {
        PathParser.createPathFromPathData(dummySvgPath).asComposePath()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Trace the path",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Box(
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF0F0F0))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isTracing = true
                            userPath = Path().apply { moveTo(offset.x, offset.y) }
                        },
                        onDragEnd = {
                            isTracing = false
                            // Here you would validate if the path covers the letter
                        },
                        onDrag = { change, _ ->
                            if (isTracing) {
                                userPath.lineTo(change.position.x, change.position.y)
                                // Trigger recomposition to draw the new line
                                userPath = Path().apply { addPath(userPath) }
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 1. Draw the template (background letter)
                drawPath(
                    path = letterPath,
                    color = Color.LightGray,
                    style = Stroke(
                        width = 80f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 2. Draw the user's tracing progress
                drawPath(
                    path = userPath,
                    color = Color(0xFF58CC02), // Duolingo Green
                    style = Stroke(
                        width = 80f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { userPath = Path() }) {
            Text("Clear")
        }
    }
}