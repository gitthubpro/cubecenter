package com.cubecenter.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.cubecenter.app.model.FaceType
import com.cubecenter.app.ui.theme.CubeGrey
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Cube3DView(
    faces: Map<FaceType, List<Color?>>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cubeSize = size.minDimension * 0.4f
        val cx = size.width / 2
        val cy = size.height / 2

        fun project(x: Float, y: Float, z: Float): Offset {
            val angle = Math.toRadians(30.0).toFloat()
            val px = (x - z) * cos(angle)
            val py = (x + z) * sin(angle) - y
            return Offset(cx + px * cubeSize, cy + py * cubeSize)
        }

        fun drawFace(
            colors: List<Color?>,
            fixAxis: Char,
            axisValue: Float,
            normalDir: Float
        ) {
            val steps = listOf(-1f, -1f/3f, 1f/3f, 1f)
            var index = 0
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    val u1 = steps[i]
                    val u2 = steps[i+1]
                    val v1 = steps[j]
                    val v2 = steps[j+1]

                    val p1: Offset
                    val p2: Offset
                    val p3: Offset
                    val p4: Offset

                    when (fixAxis) {
                        'z' -> {
                            val z = axisValue * normalDir
                            p1 = project(u1, v2, z)
                            p2 = project(u2, v2, z)
                            p3 = project(u2, v1, z)
                            p4 = project(u1, v1, z)
                        }
                        'x' -> {
                            val x = axisValue * normalDir
                            p1 = project(x, v2, u1)
                            p2 = project(x, v2, u2)
                            p3 = project(x, v1, u2)
                            p4 = project(x, v1, u1)
                        }
                        'y' -> {
                            val y = axisValue * normalDir
                            p1 = project(u1, y, v1)
                            p2 = project(u2, y, v1)
                            p3 = project(u2, y, v2)
                            p4 = project(u1, y, v2)
                        }
                        else -> return
                    }

                    val path = Path().apply {
                        moveTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(p3.x, p3.y)
                        lineTo(p4.x, p4.y)
                        close()
                    }

                    val color = colors.getOrNull(index) ?: CubeGrey
                    drawPath(path, color)
                    drawPath(path, Color.Black, style = Stroke(width = 2f))
                    index++
                }
            }
        }

        drawFace(faces[FaceType.FRONT] ?: List(9) { null }, 'z', 1f, 1f)
        drawFace(faces[FaceType.UP] ?: List(9) { null }, 'y', 1f, 1f)
        drawFace(faces[FaceType.RIGHT] ?: List(9) { null }, 'x', 1f, 1f)
    }
}
