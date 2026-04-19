package com.example.safarisafe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    error = ErrorRed,
    errorContainer = ErrorContainer,
    tertiary = TertiaryRed,
    background = SurfaceBackground,
    surface = SurfaceBackground,
    surfaceVariant = SurfaceContainerHighest,
    secondary = SecondaryGreen,
    secondaryContainer = SecondaryContainerGreen,
    onSurface = TextPrimary,
    onSurfaceVariant = OnSurfaceVariant
)

@Composable
fun SafariSafeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}