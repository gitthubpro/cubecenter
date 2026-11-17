package com.cubecenter.app.model

import kotlin.random.Random

class RubikCube(
    val faces: Map<Face, Array<CubeColor>> = Face.values().associateWith { face ->
        Array(9) { getInitialColorForFace(face) }
    }
) {

    companion object {
        fun getInitialColorForFace(face: Face): CubeColor {
            return when (face) {
                Face.U -> CubeColor.WHITE
                Face.D -> CubeColor.YELLOW
                Face.L -> CubeColor.ORANGE
                Face.R -> CubeColor.RED
                Face.F -> CubeColor.GREEN
                Face.B -> CubeColor.BLUE
            }
        }

        fun generateScramble(): List<String> {
            val moves = listOf("U", "D", "L", "R", "F", "B")
            val modifiers = listOf("", "'", "2")
            val scramble = mutableListOf<String>()
            var lastMove = ""
            for (i in 1..20) {
                var nextMove = moves[Random.nextInt(moves.size)]
                while (nextMove == lastMove) {
                    nextMove = moves[Random.nextInt(moves.size)]
                }
                val mod = modifiers[Random.nextInt(modifiers.size)]
                scramble.add(nextMove + mod)
                lastMove = nextMove
            }
            return scramble
        }
    }

    fun isSolved(): Boolean {
        for (face in Face.values()) {
            val stickers = faces[face] ?: return false
            val firstColor = stickers[0]
            if (stickers.any { it != firstColor }) {
                return false
            }
        }
        return true
    }

    private fun rotateFace90CW(oldFace: Array<CubeColor>): Array<CubeColor> {
        val newFace = Array(9) { CubeColor.WHITE }
        newFace[0] = oldFace[6]
        newFace[1] = oldFace[3]
        newFace[2] = oldFace[0]
        newFace[3] = oldFace[7]
        newFace[4] = oldFace[4]
        newFace[5] = oldFace[1]
        newFace[6] = oldFace[8]
        newFace[7] = oldFace[5]
        newFace[8] = oldFace[2]
        return newFace
    }

    fun copy(): RubikCube {
        val newMap = faces.mapValues { (_, arr) -> arr.copyOf() }
        return RubikCube(newMap)
    }

    fun makeMove(move: String): RubikCube {
        val cube = this.copy()
        val baseMove = move.replace("'", "").replace("2", "")
        val isDouble = move.contains("2")
        val isCounter = move.contains("'")

        val repetitions = if (isDouble) 2 else if (isCounter) 3 else 1
        for (i in 1..repetitions) {
            cube.applySingleCWMove(baseMove)
        }
        return cube
    }

    private fun applySingleCWMove(baseMove: String) {
        val u = faces[Face.U]!!
        val d = faces[Face.D]!!
        val l = faces[Face.L]!!
        val r = faces[Face.R]!!
        val f = faces[Face.F]!!
        val b = faces[Face.B]!!

        when (baseMove) {
            "U" -> {
                faces[Face.U]!!.copyFrom(rotateFace90CW(u))
                val temp0 = f[0]; val temp1 = f[1]; val temp2 = f[2]
                f[0] = r[2]; f[1] = r[1]; f[2] = r[0]
                r[0] = b[2]; r[1] = b[1]; r[2] = b[0]
                b[0] = l[2]; b[1] = l[1]; b[2] = l[0]
                l[0] = temp2; l[1] = temp1; l[2] = temp0
            }
            "D" -> {
                faces[Face.D]!!.copyFrom(rotateFace90CW(d))
                val temp6 = f[6]; val temp7 = f[7]; val temp8 = f[8]
                f[6] = l[8]; f[7] = l[7]; f[8] = l[6]
                l[6] = b[8]; l[7] = b[7]; l[8] = b[6]
                b[6] = r[8]; b[7] = r[7]; b[8] = r[6]
                r[6] = temp8; r[7] = temp7; r[8] = temp6
            }
            "L" -> {
                faces[Face.L]!!.copyFrom(rotateFace90CW(l))
                val temp0 = u[0]; val temp3 = u[3]; val temp6 = u[6]
                u[0] = b[8]; u[3] = b[5]; u[6] = b[2]
                b[8] = d[0]; b[5] = d[3]; b[2] = d[6]
                d[0] = f[0]; d[3] = f[3]; d[6] = f[6]
                f[0] = temp0; f[3] = temp3; f[6] = temp6
            }
            "R" -> {
                faces[Face.R]!!.copyFrom(rotateFace90CW(r))
                val temp2 = u[2]; val temp5 = u[5]; val temp8 = u[8]
                u[2] = f[2]; u[5] = f[5]; u[8] = f[8]
                f[2] = d[2]; f[5] = d[5]; f[8] = d[8]
                d[2] = b[6]; d[5] = b[3]; d[8] = b[0]
                b[6] = temp2; b[3] = temp5; b[0] = temp8
            }
            "F" -> {
                faces[Face.F]!!.copyFrom(rotateFace90CW(f))
                val temp6 = u[6]; val temp7 = u[7]; val temp8 = u[8]
                u[6] = l[6]; u[7] = l[3]; u[8] = l[0]
                l[0] = d[0]; l[3] = d[1]; l[6] = d[2]
                d[0] = r[8]; d[1] = r[5]; d[2] = r[2]
                r[2] = temp6; r[5] = temp7; r[8] = temp8
            }
            "B" -> {
                faces[Face.B]!!.copyFrom(rotateFace90CW(b))
                val temp0 = u[0]; val temp1 = u[1]; val temp2 = u[2]
                u[0] = r[0]; u[1] = r[3]; u[2] = r[6]
                r[0] = d[8]; r[3] = d[7]; r[6] = d[6]
                d[8] = l[8]; d[7] = l[5]; d[6] = l[2]
                l[8] = temp0; l[5] = temp1; l[2] = temp2
            }
        }
    }

    private fun Array<CubeColor>.copyFrom(source: Array<CubeColor>) {
        source.copyInto(this)
    }
}
