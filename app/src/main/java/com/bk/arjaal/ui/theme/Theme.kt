package com.bk.arjaal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.bk.arjaal.ui.theme.ui.theme.*

private val DarkColorPalette = darkColors(
    primary = LPrimary,
    primaryVariant = Ljust1,
    secondary = LSecondary
)

private val LightColorPalette = lightColors(
    primary = LPrimary,
    primaryVariant = Ljust1,
    secondary = LSecondary,
    surface = LSurface,
    background = LBackground

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ARJaalTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}