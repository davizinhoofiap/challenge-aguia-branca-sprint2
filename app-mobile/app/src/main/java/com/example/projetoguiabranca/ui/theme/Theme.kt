package com.example.projetoguiabranca.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulPrimario,
    secondary = AzulSecundario,
    onSecondary = Color.White,
    background = CinzaFundo,
    onBackground = TextoPrimario,
    surface = CardBranco,
    onSurface = TextoPrimario,
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = TextoSecundario,
    outline = CinzaBorda,
    error = CorErro,
    onError = Color.White
)

@Composable
fun ProjetoÁguiaBrancaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desabilitado para garantir consistência de contraste visual
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AzulPrimario.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}