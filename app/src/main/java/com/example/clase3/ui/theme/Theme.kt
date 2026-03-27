package com.example.clase3.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    onPrimary = White,
    background = White,
    onBackground = DarkBlue,
    surface = White,
    onSurface = DarkBlue,
    secondary = DarkBlue,
    onSecondary = White
)

@Composable
fun Clase3Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
