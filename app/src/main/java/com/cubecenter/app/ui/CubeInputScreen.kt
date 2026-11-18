package com.cubecenter.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cubecenter.app.model.CubeColor
import com.cubecenter.app.model.CubeViewModel
import com.cubecenter.app.ui.theme.*

@Composable
fun CubeInputScreen(viewModel: CubeViewModel = viewModel<CubeViewModel>()) {
    val cubeState by viewModel.cubeState.collectAsState()
    val selectedFace by viewModel.selectedFace.collectAsState()
    
    var selectedIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cubecenter - Giriş",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(DarkSurface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // İŞTE EKSİK OLAN PARAMETRE BURADA EKLENDİ
            Cube3DView(
                faces = cubeState.faces,
                onSquareClick = { face, index ->
                    viewModel.setFace(face)
                    selectedIndex = index
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Yüz: ${selectedFace.name}",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        val faceColors = cubeState.faces[selectedFace] ?: Array(9) { CubeColor.WHITE }
        
        Column(
            modifier = Modifier.size(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        val color = faceColors[index].toComposeColor()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(color, RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedIndex == index) 3.dp else 1.dp,
                                    color = if (selectedIndex == index) Color.Yellow else Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedIndex = index }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Bir Renk Seç", color = Color.White, fontSize = 16.sp)
        
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CubeColor.values().forEach { cubeColor ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(cubeColor.toComposeColor())
                        .clickable { viewModel.setColor(selectedIndex, cubeColor) }
                        .border(1.dp, Color.Black, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { viewModel.changeFace(-1) }) {
                Text("< Önceki")
            }
            Button(onClick = { viewModel.changeFace(1) }) {
                Text("Sonraki >")
            }
        }
    }
}
