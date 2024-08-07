package com.example.vybes.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.vybes.R

val scriptoFontFamily = FontFamily(
    Font(R.font.scripto, FontWeight.Normal)
)

val logoStyle = TextStyle(
    fontFamily = scriptoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 36.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)

val songTitleStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    color = White
)

val artistsStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = Color.LightGray
)

val disabledStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = Color.DarkGray
)