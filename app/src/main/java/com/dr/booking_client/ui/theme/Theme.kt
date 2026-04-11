package com.dr.booking_client.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary          = Teal700,
    onPrimary        = Color.White,
    primaryContainer = Teal200,
    secondary        = Teal500,
    background       = BackgroundGrey,
    surface          = CardSurface,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    error            = ErrorRed,
)

@Composable
fun SharmaClinicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography  = Typography,
        content     = content
    )
}