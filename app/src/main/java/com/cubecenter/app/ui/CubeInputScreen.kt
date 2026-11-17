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
import com.cubecenter.app.model.CubeColor
import com.cubecenter.app.model.CubeViewModel
import com.cubecenter.app.ui.theme.*

@Composable
fun CubeInputScreen(viewModel: CubeViewModel = viewModel<CubeViewModel>()) {
    val cubeState by viewModel.cubeState.collectAsState()
    val selectedFace by viewModel.selectedFace.collectAsState()

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
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(DarkSurface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Cube3DView(
                faces = cubeState.faces,
                onSquareClick = { face, index ->
                    // Tıklanan yüzü seçili yap ve rengi ata
                    // Şimdilik sadece altyapı, tam etkileşim bir sonraki adımda
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
        
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(Color.Transparent)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(9) { index ->
                    val color = faceColors[index].toComposeColor()
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(color, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    )
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
                        .clickable {
                            val nextEmptyIndex = faceColors.indexOfFirst { it == CubeColor.WHITE && cubeState.faces[selectedFace]?.get(it) == null }
                            // Basit mantık: boş yere veya sıradaki yere rengi ata
                            // Not: Yeni motorda başlangıçta tüm renkler dolu olduğu için burası değişecek
                        }
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
