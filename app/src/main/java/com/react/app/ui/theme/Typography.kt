package com.react.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

val AppTypography = Typography(
    displayLarge = androidx.compose.ui.text.style.TextStyle(fontFamily = FontFamily.Default, fontSize = 32.sp),
    displayMedium = androidx.compose.ui.text.style.TextStyle(fontFamily = FontFamily.Default, fontSize = 28.sp),
    titleLarge = androidx.compose.ui.text.style.TextStyle(fontFamily = FontFamily.Default, fontSize = 24.sp),
    titleMedium = androidx.compose.ui.text.style.TextStyle(fontFamily = FontFamily.Default, fontSize = 20.sp),
    bodyLarge = androidx.compose.ui.text.style.TextStyle(fontFamily = FontFamily.Default, fontSize = 18.sp),
    bodyMedium = androidx.compose.ui.text.style.TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp)
)
