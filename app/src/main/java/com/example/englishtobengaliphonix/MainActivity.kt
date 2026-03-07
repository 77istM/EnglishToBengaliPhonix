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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import kotlin.math.min

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
    var userPath by remember { mutableStateOf(Path()) }
    var isTracing by remember { mutableStateOf(false) }

    // Parse and scale the SVG path
    val letterPath = remember {
        // Taking the first section of your SVG for demonstration.
        // You'll need to use your combined string from AlphabetPaths.kt
        val rawPath = PathParser.createPathFromPathData(
            "M2734.1,373.1c3.5,6.8,5.9,13.2,7.2,19.1s1.9,11.5,1.9,16.9c0,13.4-1.3,24.8-3.9,34.2c-2.6,9.4-7.2,17.5-13.8,24.4 c-15.1,16.5-33.2,24.7-54.4,24.7c-19.5,0-36.6-4.7-51.2-14.1c-14.6-9.4-27.1-21.6-37.4-36.5c-10.4-14.9-18.8-31.6-25.4-50.1 c-6.6-18.5-11.4-36.8-14.5-54.9l14.1,6c1.6,8.7,3.9,17.6,6.9,26.7c2.9,9.1,6.5,17.9,10.6,26.5s8.9,16.8,14.3,24.7 c5.4,7.9,11.4,15,18,21.4c-6.6-14.1-11.6-31.2-15.2-51.4s-5.9-42.2-7.1-66.2l13.1,8.1c1.4,12.2,3.2,25.7,5.5,40.2 c2.2,14.6,5.8,28.2,10.8,40.9c4.9,12.7,11.7,23.3,20.3,31.8s19.9,12.7,34.1,12.7c14.8,0,28.7-5.1,41.7-15.2 c12-9.9,18-22.6,18-38.1c0-8.7-3.4-17.2-10.2-25.4c-2.6,4.7-6,9.4-10.2,13.9c-4.2,4.6-8.9,8.9-14.1,12.9 c-10.6,8.5-19.4,12.7-26.5,12.7c-8.7,0-16.4-3.2-22.9-9.5c-6.6-6.4-9.9-14.7-9.9-25.1v-73.8h-107.7l-19.4-19.8h191.3 c8.5,0,12.7-3.5,12.7-10.6c0-6.1-3.9-10.8-11.6-14.1c-5.9-2.3-11.9-3.5-18-3.5c-3.1,0-7.1,0.2-12.2,0.5 c-5.1,0.4-10.5,0.8-16.4,1.2c-3.3,0.2-6.9,0.4-10.9,0.5s-7.9,0.2-11.8,0.4c-3.9,0.1-7.4,0.2-10.4,0.2c-3.1,0-5.4,0-7.1,0 c-15.3,0-28.8-3.8-40.6-11.3c-14.1-8.5-21.2-19.6-21.2-33.5c0-12,3.2-19.6,9.5-22.9c1.2,0.5,2.6,1.1,4.2,1.8 c1.6,0.7,3.1,1.3,4.2,1.8c-4.9,4-7.4,8.7-7.4,14.1c0,7.5,4.7,13.4,14.1,17.6c3.3,1.4,6.9,2.6,10.9,3.5s8.2,1.4,12.7,1.4 c3.1,0,7.1-0.1,12-0.4c4.9-0.2,10.8-0.6,17.6-1.1c3.3-0.2,7-0.5,11.1-0.7s8.1-0.4,12-0.5s7.4-0.2,10.6-0.4 c3.2-0.1,5.6-0.2,7.2-0.2c17.9,0,32.1,4.6,42.5,13.8c10.5,9.2,15.7,23.3,15.7,42.4h19.8l19.1,19.8h-113.3v60.8 c0,4,1.2,7.8,3.5,11.3s5.5,5.3,9.5,5.3c4.7-0.5,9.1-2.6,13.1-6.4s8-8.4,12-13.8c7.8-11.3,12.9-16.9,15.5-16.9 C2717.3,350.9,2726.3,358.3,2734.1,373.1z"
        )
        val composePath = rawPath.asComposePath()

        // Calculate scaling
        val bounds = composePath.getBounds()
        val targetSize = 800f
        val scale = min(targetSize / bounds.width, targetSize / bounds.height)

        val matrix = android.graphics.Matrix()
        matrix.postScale(scale, scale)
        // Adjust translation to centre the path. You may need to tweak these values based on your full SVG.
        matrix.postTranslate(-bounds.left * scale + 50f, -bounds.top * scale + 50f)

        rawPath.transform(matrix)
        rawPath.asComposePath()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Trace the alphabet",
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
                            // Create a new path to force UI update
                            val newPath = Path().apply {
                                addPath(userPath)
                                moveTo(offset.x, offset.y)
                            }
                            userPath = newPath
                        },
                        onDragEnd = {
                            isTracing = false
                        },
                        onDrag = { change, _ ->
                            if (isTracing) {
                                // Create a new path and append the line segment
                                val newPath = Path().apply {
                                    addPath(userPath)
                                    lineTo(change.position.x, change.position.y)
                                }
                                userPath = newPath
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 1. Draw the background letter outline
                drawPath(
                    path = letterPath,
                    color = Color.LightGray,
                    style = Stroke(
                        width = 80f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 2. Clip all subsequent drawing to the letter's shape + stroke width
                // We create a "fat" version of the path to clip against
                clipPath(path = letterPath) { // Note: clipPath clips to the *fill* of the path.
                    // Since your path is an outline, we might need a different approach if you want to clip to the stroke bounds.
                    // For now, let's just draw the trace.

                    drawPath(
                        path = userPath,
                        color = Color(0xFF58CC02),
                        style = Stroke(
                            width = 60f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { userPath = Path() }) {
            Text("Clear")
        }
    }
}