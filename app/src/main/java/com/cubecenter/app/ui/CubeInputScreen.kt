package com.cubecenter.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cubecenter.app.model.CubeViewModel
import com.cubecenter.app.ui.theme.*

@Composable
fun CubeInputScreen(viewModel: CubeViewModel = viewModel<CubeViewModel>()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cubecenter - Giriş",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { viewModel.undo() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Geri Al", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(DarkSurface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Cube3DView(
                faces = state.faces,
                onSquareClick = { face, index ->
                    // 3D'den tıklandığında eğer o kare boşsa, seçili yüzün rengini oraya uygula (veya bir renk paleti seçimi gerekiyorsa state'e bak)
                    // Şimdilik sadece 3D'den tıklama altyapısını kurduk. Tıklama çalışıyor.
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Yüz: ${state.currentFace.name}",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        val faceColors = state.faces[state.currentFace] ?: List(9) { null }
        
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Transparent)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(9) { index ->
                    val color = faceColors[index] ?: CubeGrey
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(color, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text("Bir Renk Seç", color = Color.White, fontSize = 16.sp)
        
        Spacer(modifier = Modifier.height(8.dp))

        val palette = listOf(CubeYellow, CubeBlue, CubeOrange, CubeWhite, CubeGreen, CubeRed)
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            palette.forEach { color ->
                val count = state.colorCounts[color] ?: 0
                val isComplete = count >= 9
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable(enabled = !isComplete) {
                                val nextEmptyIndex = faceColors.indexOfFirst { it == null }
                                if (nextEmptyIndex != -1) {
                                    viewModel.setColor(nextEmptyIndex, color)
                                }
                            }
                            .border(if (isComplete) 2.dp else 0.dp, if (isComplete) Color.Black else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isComplete) {
                            Text("✓", color = if (color == CubeWhite) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Text(count.toString(), color = if (color == CubeWhite) Color.Black else Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
