package com.example.todoapp.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AppOrange,
    onPrimary = AppWhite,
    secondary = AppBlue,
    background = AppBlack,
    onBackground = AppWhite,
    surface = AppPanel,
    onSurface = AppWhite,
    onSurfaceVariant = AppGray,
    outline = AppDivider
)

private val LightColorScheme = lightColorScheme(
    primary = AppOrange,
    onPrimary = AppWhite,
    secondary = AppBlue,
    background = AppBlack,
    onBackground = AppWhite,
    surface = AppPanel,
    onSurface = AppWhite,
    onSurfaceVariant = AppGray,
    outline = AppDivider
)

@Composable
fun TodoAppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
