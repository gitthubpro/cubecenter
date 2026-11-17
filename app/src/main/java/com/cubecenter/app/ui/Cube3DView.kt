package com.cubecenter.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.cubecenter.app.model.FaceType
import com.cubecenter.app.ui.theme.CubeGrey
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class FaceSquare(
    val path: Path,
    val color: Color,
    val z: Float
)

@Composable
fun Cube3DView(
    faces: Map<FaceType, List<Color?>>,
    modifier: Modifier = Modifier
) {
    var rotationX by remember { mutableStateOf(0.5f) }
    var rotationY by remember { mutableStateOf(-0.785f) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    rotationY -= dragAmount.x * 0.01f
                    rotationX += dragAmount.y * 0.01f
                }
            }
    ) {
        val cubeSize = min(size.width, size.height) * 0.25f
        val cx = size.width / 2
        val cy = size.height / 2

        fun rotate(x: Float, y: Float, z: Float): Triple<Float, Float, Float> {
            val cosY = cos(rotationY)
            val sinY = sin(rotationY)
            val x1 = x * cosY + z * sinY
            val z1 = -x * sinY + z * cosY

            val cosX = cos(rotationX)
            val sinX = sin(rotationX)
            val y1 = y * cosX - z1 * sinX
            val z2 = y * sinX + z1 * cosX

            return Triple(x1, y1, z2)
        }

        fun project(x: Float, y: Float, z: Float): Offset {
            val cameraZ = 4f
            val pz = z + cameraZ
            val factor = 1f / pz
            return Offset(cx + x * factor * cubeSize, cy - y * factor * cubeSize)
        }

        val squares = mutableListOf<FaceSquare>()

        fun addFace(face: FaceType, ax1: Char, ax2: Char, axF: Char, fVal: Float) {
            val colors = faces[face] ?: List(9) { null }
            val steps = listOf(-1f, -1f/3f, 1f/3f, 1f)
            var index = 0
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    val u1 = steps[i]
                    val u2 = steps[i+1]
                    val v1 = steps[j]
                    val v2 = steps[j+1]

                    fun getPt(u: Float, v: Float): Triple<Float, Float, Float> {
                        var x = 0f; var y = 0f; var z = 0f
                        when (axF) {
                            'x' -> { x = fVal; if (ax1 == 'y') { y = u; z = v } else { y = v; z = u } }
                            'y' -> { y = fVal; if (ax1 == 'x') { x = u; z = v } else { x = v; z = u } }
                            'z' -> { z = fVal; if (ax1 == 'x') { x = u; y = v } else { x = v; y = u } }
                        }
                        return Triple(x, y, z)
                    }

                    val p1 = getPt(u1, v1)
                    val p2 = getPt(u2, v1)
                    val p3 = getPt(u2, v2)
                    val p4 = getPt(u1, v2)

                    val r1 = rotate(p1.first, p1.second, p1.third)
                    val r2 = rotate(p2.first, p2.second, p2.third)
                    val r3 = rotate(p3.first, p3.second, p3.third)
                    val r4 = rotate(p4.first, p4.second, p4.third)

                    val zAvg = (r1.third + r2.third + r3.third + r4.third) / 4f

                    val o1 = project(r1.first, r1.second, r1.third)
                    val o2 = project(r2.first, r2.second, r2.third)
                    val o3 = project(r3.first, r3.second, r3.third)
                    val o4 = project(r4.first, r4.second, r4.third)

                    val path = Path().apply {
                        moveTo(o1.x, o1.y)
                        lineTo(o2.x, o2.y)
                        lineTo(o3.x, o3.y)
                        lineTo(o4.x, o4.y)
                        close()
                    }
                    
                    val color = colors.getOrNull(index) ?: CubeGrey
                    squares.add(FaceSquare(path, color, zAvg))
                    index++
                }
            }
        }

        addFace(FaceType.FRONT, 'x', 'y', 'z', 1f)
        addFace(FaceType.BACK, 'x', 'y', 'z', -1f)
        addFace(FaceType.UP, 'x', 'z', 'y', 1f)
        addFace(FaceType.DOWN, 'x', 'z', 'y', -1f)
        addFace(FaceType.RIGHT, 'y', 'z', 'x', 1f)
        addFace(FaceType.LEFT, 'y', 'z', 'x', -1f)

        squares.sortBy { it.z }

        for (sq in squares) {
            drawPath(sq.path, sq.color)
            drawPath(sq.path, Color.Black, style = Stroke(width = 2f))
        }
    }
}
