package com.example.bdroomguiaejemplo.Screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bdroomguiaejemplo.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(navController: NavHostController, initialLaunch: Boolean) {
    // Añadir animación de escala al logotipo
    val infiniteTransition = rememberInfiniteTransition()
    val scale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Pantalla Splash con logo animado y fondo con la nueva imagen geométrica
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0047AB)),  // Aplicar un color de fondo mientras carga la imagen de fondo geométrica
        contentAlignment = Alignment.Center
    ) {
        // Imagen del fondo geométrico
        Image(
            painter = painterResource(id = R.drawable.fondo3), // Asegúrate de que la imagen esté en drawable
            contentDescription = "Fondo geométrico",
            contentScale = ContentScale.Crop,  // Ajustar la imagen para que cubra toda la pantalla
            modifier = Modifier.fillMaxSize() // Llenar toda la pantalla
        )

        // Imagen del logo con animación de escala
        Image(
            painter = painterResource(id = R.drawable.logo), // Asegúrate de que la imagen del logo esté en drawable
            contentDescription = "Logo de la App",
            modifier = Modifier
                .size(250.dp)  // Aumentar el tamaño del logo para que sea más visible
                .scale(scale.value)  // Aplicar animación de escala
        )
    }

    // Navegar al login después del retraso definido
    LaunchedEffect(Unit) {
        val delayTime = if (initialLaunch) 4000L else 3000L
        delay(delayTime) // Duración del Splash en milisegundos

        try {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Reiniciar la pila para evitar volver a la splash screen
                launchSingleTop = true // Evitar múltiples instancias de la pantalla de login
            }
        } catch (e: Exception) {
            // En caso de que ocurra un error, simplemente registrarlo para depuración
            e.printStackTrace()
        }
    }
}
