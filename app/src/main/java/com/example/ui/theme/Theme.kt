package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryBlueContainer,
    onPrimaryContainer = OnPrimaryBlueContainer,
    background = BackgroundSlate,
    onBackground = OnBackgroundDark,
    surface = CardBackground,
    onSurface = OnBackgroundDark,
    outline = CardStroke,
    error = ExpenseRed
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueContainer,
    onPrimary = OnPrimaryBlueContainer,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = OnPrimaryWhite,
    background = OnBackgroundDark,
    onBackground = BackgroundSlate,
    surface = OnBackgroundDark,
    onSurface = BackgroundSlate,
    outline = SecondaryTextGrey,
    error = ExpenseRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
