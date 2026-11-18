package com.cubecenter.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.cubecenter.app.model.CubeColor
import com.cubecenter.app.model.Face
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class FaceSquare(
    val face: Face,
    val index: Int,
    val path: Path,
    val color: Color,
    val z: Float,
    val center: Offset
)

@Composable
fun Cube3DView(
    faces: Map<Face, Array<CubeColor>>,
    onSquareClick: (Face, Int) -> Unit,
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
            .pointerInput(faces) {
                detectTapGestures { offset ->
                    val tapSquares = mutableListOf<FaceSquare>()
                    val cubeSize = min(size.width, size.height) * 0.35f
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
                        val cameraZ = 5f
                        val pz = z + cameraZ
                        val factor = 1f / pz
                        return Offset(cx + x * factor * cubeSize, cy - y * factor * cubeSize)
                    }

                    fun addFaceForTap(face: Face, ax1: Char, ax2: Char, axF: Char, fVal: Float) {
                        val colors = faces[face] ?: return
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

                                val nx1 = r2.first - r1.first
                                val ny1 = r2.second - r1.second
                                val nz1 = r2.third - r1.third

                                val nx2 = r4.first - r1.first
                                val ny2 = r4.second - r1.second
                                val nz2 = r4.third - r1.third

                                val normalX = ny1 * nz2 - nz1 * ny2
                                val normalY = nz1 * nx2 - nx1 * nz2
                                val normalZ = nx1 * ny2 - ny1 * nx2

                                val centerX = (r1.first + r2.first + r3.first + r4.first) / 4f
                                val centerY = (r1.second + r2.second + r3.second + r4.second) / 4f
                                val centerZ = (r1.third + r2.third + r3.third + r4.third) / 4f

                                val viewX = -centerX
                                val viewY = -centerY
                                val viewZ = -5f - centerZ

                                val dot = normalX * viewX + normalY * viewY + normalZ * viewZ

                                if (dot < 0f) {
                                    index++
                                    continue
                                }

                                val zAvgTap = (r1.third + r2.third + r3.third + r4.third) / 4f

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
                                
                                val center = Offset((o1.x+o2.x+o3.x+o4.x)/4f, (o1.y+o2.y+o3.y+o4.y)/4f)
                                val color = colors.getOrNull(index)?.toComposeColor() ?: Color.Gray
                                
                                // Senin bulduğun zAvgTap düzeltmesi burada!
                                tapSquares.add(FaceSquare(face, index, path, color, zAvgTap, center))
                                index++
                            }
                        }
                    }

                    addFaceForTap(Face.F, 'x', 'y', 'z', 1f)
                    addFaceForTap(Face.U, 'x', 'z', 'y', 1f)
                    addFaceForTap(Face.R, 'y', 'z', 'x', 1f)
                    addFaceForTap(Face.B, 'x', 'y', 'z', -1f)
                    addFaceForTap(Face.D, 'x', 'z', 'y', -1f)
                    addFaceForTap(Face.L, 'y', 'z', 'x', -1f)

                    tapSquares.sortByDescending { it.z }
                    for (sq in tapSquares) {
                        if (sq.path.contains(offset)) {
                            onSquareClick(sq.face, sq.index)
                            break
                        }
                    }
                }
            }
    ) {
        val cubeSize = min(size.width, size.height) * 0.35f
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
            val cameraZ = 5f
            val pz = z + cameraZ
            val factor = 1f / pz
            return Offset(cx + x * factor * cubeSize, cy - y * factor * cubeSize)
        }

        val squares = mutableListOf<FaceSquare>()

        fun addFaceForDraw(face: Face, ax1: Char, ax2: Char, axF: Char, fVal: Float) {
            val colors = faces[face] ?: return
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

                    val nx1 = r2.first - r1.first
                    val ny1 = r2.second - r1.second
                    val nz1 = r2.third - r1.third

                    val nx2 = r4.first - r1.first
                    val ny2 = r4.second - r1.second
                    val nz2 = r4.third - r1.third

                    val normalX = ny1 * nz2 - nz1 * ny2
                    val normalY = nz1 * nx2 - nx1 * nz2
                    val normalZ = nx1 * ny2 - ny1 * nx2

                    val centerX = (r1.first + r2.first + r3.first + r4.first) / 4f
                    val centerY = (r1.second + r2.second + r3.second + r4.second) / 4f
                    val centerZ = (r1.third + r2.third + r3.third + r4.third) / 4f

                    val viewX = -centerX
                    val viewY = -centerY
                    val viewZ = -5f - centerZ

                    val dot = normalX * viewX + normalY * viewY + normalZ * viewZ

                    if (dot < 0f) {
                        index++
                        continue
                    }

                    val zAvgDraw = (r1.third + r2.third + r3.third + r4.third) / 4f

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
                    
                    val center = Offset((o1.x+o2.x+o3.x+o4.x)/4f, (o1.y+o2.y+o3.y+o4.y)/4f)
                    val color = colors.getOrNull(index)?.toComposeColor() ?: Color.Gray
                    squares.add(FaceSquare(face, index, path, color, zAvgDraw, center))
                    index++
                }
            }
        }

        addFaceForDraw(Face.F, 'x', 'y', 'z', 1f)
        addFaceForDraw(Face.U, 'x', 'z', 'y', 1f)
        addFaceForDraw(Face.R, 'y', 'z', 'x', 1f)
        addFaceForDraw(Face.B, 'x', 'y', 'z', -1f)
        addFaceForDraw(Face.D, 'x', 'z', 'y', -1f)
        addFaceForDraw(Face.L, 'y', 'z', 'x', -1f)

        squares.sortBy { it.z }

        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 36f
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }

        for (sq in squares) {
            drawPath(sq.path, sq.color)
            drawPath(sq.path, Color.Black, style = Stroke(width = 4f))

            if (sq.index == 4) {
                drawCircle(Color.Black, radius = 6f, center = sq.center)
            }
        }

        val labelDist = 1.15f
        val labelData = listOf(
            Pair(Face.F, Triple(0f, 0f, labelDist)),
            Pair(Face.B, Triple(0f, 0f, -labelDist)),
            Pair(Face.U, Triple(0f, labelDist, 0f)),
            Pair(Face.D, Triple(0f, -labelDist, 0f)),
            Pair(Face.R, Triple(labelDist, 0f, 0f)),
            Pair(Face.L, Triple(-labelDist, 0f, 0f))
        )

        for (pair in labelData) {
            val face = pair.first
            val coords = pair.second
            
            val r = rotate(coords.first, coords.second, coords.third)
            if (r.third < 0f) {
                val pt = project(r.first, r.second, r.third)
                val text: String = when(face) {
                    Face.U -> "ÜST"
                    Face.D -> "ALT"
                    Face.F -> "ÖN"
                    Face.B -> "ARKA"
                    Face.L -> "SOL"
                    Face.R -> "SAĞ"
                }
                drawContext.canvas.nativeCanvas.drawText(
                    text,
                    pt.x,
                    pt.y + 12f,
                    textPaint
                )
            }
        }
    }
}
