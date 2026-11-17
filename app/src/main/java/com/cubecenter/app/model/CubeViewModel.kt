package com.cubecenter.app.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CubeViewModel : ViewModel() {
    private val _cubeState = MutableStateFlow(RubikCube())
    val cubeState: StateFlow<RubikCube> = _cubeState.asStateFlow()

    private val _selectedFace = MutableStateFlow(Face.U)
    val selectedFace: StateFlow<Face> = _selectedFace.asStateFlow()

    fun setColor(index: Int, color: CubeColor) {
        val currentCube = _cubeState.value
        val face = _selectedFace.value
        val currentColors = currentCube.faces[face]?.copyOf() ?: return
        
        currentColors[index] = color
        
        val newFaces = currentCube.faces.toMutableMap()
        newFaces[face] = currentColors
        
        _cubeState.value = RubikCube(newFaces)
    }

    fun changeFace(direction: Int) {
        val faces = Face.values()
        val currentIndex = faces.indexOf(_selectedFace.value)
        val newIndex = (currentIndex + direction + faces.size) % faces.size
        _selectedFace.value = faces[newIndex]
    }
}
