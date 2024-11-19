package com.example.bdroomguiaejemplo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.bdroomguiaejemplo.R

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1E88E5), // Azul primario oscuro
    secondary = Color(0xFFD32F2F), // Rojo para elementos secundarios
    tertiary = Color(0xFF03DAC6), // Verde menta para destacar
    background = Color(0xFF121212), // Fondo oscuro
    surface = Color(0xFF1E1E1E), // Superficie oscura para tarjetas y otros elementos
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2), // Azul primario claro
    secondary = Color(0xFFC2185B), // Rosa oscuro para elementos secundarios
    tertiary = Color(0xFF03DAC6), // Verde menta para destacar
    background = Color(0xFFEFEFEF), // Fondo claro
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// Definir la familia de fuentes utilizando el archivo robotoregular.ttf
val RobotoFontFamily = FontFamily(
    Font(R.font.robotoregular, FontWeight.Normal)
)

// Crear una nueva tipografía utilizando la familia de fuentes Roboto
val AppTypography = Typography(
    bodyLarge = Typography().bodyLarge.copy(fontFamily = RobotoFontFamily),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = RobotoFontFamily),
    bodySmall = Typography().bodySmall.copy(fontFamily = RobotoFontFamily),
    titleLarge = Typography().titleLarge.copy(fontFamily = RobotoFontFamily),
    titleMedium = Typography().titleMedium.copy(fontFamily = RobotoFontFamily),
    titleSmall = Typography().titleSmall.copy(fontFamily = RobotoFontFamily),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = RobotoFontFamily),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = RobotoFontFamily),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = RobotoFontFamily),
    displayLarge = Typography().displayLarge.copy(fontFamily = RobotoFontFamily),
    displayMedium = Typography().displayMedium.copy(fontFamily = RobotoFontFamily),
    displaySmall = Typography().displaySmall.copy(fontFamily = RobotoFontFamily)
)

@Composable
fun BDRoomGuiaEjemploTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Aplica la tipografía personalizada
        content = content
    )
}
