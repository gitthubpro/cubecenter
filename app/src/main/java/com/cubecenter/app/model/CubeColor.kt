package com.cubecenter.app.model

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

enum class CubeColor(val hex: String, val displayName: String) {
    WHITE("#FBFBFC", "Beyaz"),
    YELLOW("#FDEF15", "Sarı"),
    ORANGE("#FF5A00", "Turuncu"),
    RED("#DF1C25", "Kırmızı"),
    GREEN("#00E676", "Yeşil"),
    BLUE("#008AF1", "Mavi");

    fun toComposeColor(): Color {
        return Color(AndroidColor.parseColor(hex))
    }
}
