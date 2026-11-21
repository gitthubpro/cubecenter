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

    private val _selectedColor = MutableStateFlow(CubeColor.WHITE)
    val selectedColor: StateFlow<CubeColor> = _selectedColor.asStateFlow()

    fun setColor(face: Face, index: Int) {
        val currentCube = _cubeState.value
        val currentColors = currentCube.faces[face]?.copyOf() ?: return
        
        currentColors[index] = _selectedColor.value
        
        val newFaces = currentCube.faces.toMutableMap()
        newFaces[face] = currentColors
        
        _cubeState.value = RubikCube(newFaces)
    }

    fun setFace(face: Face) {
        _selectedFace.value = face
    }

    fun setSelectedColor(color: CubeColor) {
        _selectedColor.value = color
    }

    fun changeFace(direction: Int) {
        val faces = Face.values()
        val currentIndex = faces.indexOf(_selectedFace.value)
        val newIndex = (currentIndex + direction + faces.size) % faces.size
        _selectedFace.value = faces[newIndex]
    }
}
