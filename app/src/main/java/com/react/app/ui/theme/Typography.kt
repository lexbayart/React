package com.react.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

val AppTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Default, fontSize = 32.sp),
    displayMedium = TextStyle(fontFamily = FontFamily.Default, fontSize = 28.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Default, fontSize = 24.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Default, fontSize = 20.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontSize = 18.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp)
)
