package com.cubecenter.app.model

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.cubecenter.app.ui.theme.CubeBlue
import com.cubecenter.app.ui.theme.CubeGreen
import com.cubecenter.app.ui.theme.CubeGrey
import com.cubecenter.app.ui.theme.CubeOrange
import com.cubecenter.app.ui.theme.CubeRed
import com.cubecenter.app.ui.theme.CubeWhite
import com.cubecenter.app.ui.theme.CubeYellow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FaceType {
    UP, LEFT, FRONT, RIGHT, BACK, DOWN
}

data class CubeUiState(
    val currentFace: FaceType = FaceType.UP,
    val faces: Map<FaceType, List<Color?>> = FaceType.entries.associateWith { List(9) { null } },
    val colorCounts: Map<Color, Int> = mapOf(
        CubeWhite to 0, CubeYellow to 0, CubeRed to 0,
        CubeOrange to 0, CubeBlue to 0, CubeGreen to 0
    ),
    val history: List<FaceType> = emptyList()
)

class CubeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CubeUiState())
    val uiState: StateFlow<CubeUiState> = _uiState.asStateFlow()

    fun setColor(index: Int, color: Color) {
        val currentState = _uiState.value
        val currentFaceColors = currentState.faces[currentState.currentFace]?.toMutableList() ?: return
        
        if (currentFaceColors[index] != null) return
        if ((currentState.colorCounts[color] ?: 0) >= 9) return

        currentFaceColors[index] = color
        
        val newCounts = currentState.colorCounts.toMutableMap()
        newCounts[color] = (newCounts[color] ?: 0) + 1

        val newFaces = currentState.faces.toMutableMap()
        newFaces[currentState.currentFace] = currentFaceColors

        val newHistory = currentState.history + currentState.currentFace

        _uiState.value = currentState.copy(
            faces = newFaces,
            colorCounts = newCounts,
            history = newHistory
        )
    }

    fun undo() {
        val currentState = _uiState.value
        if (currentState.history.isEmpty()) return

        val lastFace = currentState.history.last()
        val faceColors = currentState.faces[lastFace]?.toMutableList() ?: return
        
        val lastFilledIndex = faceColors.indexOfLast { it != null }
        if (lastFilledIndex == -1) return

        val removedColor = faceColors[lastFilledIndex]
        faceColors[lastFilledIndex] = null

        val newCounts = currentState.colorCounts.toMutableMap()
        if (removedColor != null) {
            newCounts[removedColor] = (newCounts[removedColor] ?: 1) - 1
        }

        val newFaces = currentState.faces.toMutableMap()
        newFaces[lastFace] = faceColors

        val newHistory = currentState.history.dropLast(1)

        _uiState.value = currentState.copy(
            currentFace = lastFace,
            faces = newFaces,
            colorCounts = newCounts,
            history = newHistory
        )
    }

    fun changeFace(direction: Int) {
        val faces = FaceType.entries
        val currentIndex = faces.indexOf(_uiState.value.currentFace)
        val newIndex = (currentIndex + direction + faces.size) % faces.size
        _uiState.value = _uiState.value.copy(currentFace = faces[newIndex])
    }
}
