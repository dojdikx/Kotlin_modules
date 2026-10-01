package com.example.kotlinmodule4.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DayScheme = lightColorScheme(
    primary = Sky,
    onPrimary = Color.White,
    secondary = Accent,
    onSecondary = Color.White,
    background = Paper,
    surface = CardPaper,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun KotlinModule4Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DayScheme,
        typography = Typography,
        content = content
    )
}
