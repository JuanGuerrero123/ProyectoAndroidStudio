package com.example.bdroomguiaejemplo.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bdroomguiaejemplo.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondo4), // Asegúrate de que la imagen esté en el directorio drawable
                contentDescription = "Fondo de pantalla",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo de la empresa
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de la Empresa",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 24.dp)
                )

                // Título de la aplicación
                Text(
                    "Gestión de Inventarios",
                    fontSize = 30.sp,
                    color = Color(0xFF0047AB),  // Azul oscuro similar al del logo
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de correo electrónico
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = Color(0xFF0047AB)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color(0xFF0047AB),
                        focusedBorderColor = Color(0xFF0047AB),
                        focusedLabelColor = Color(0xFF0047AB),
                        unfocusedLabelColor = Color(0xFF0047AB),
                        cursorColor = Color(0xFF0047AB),
                        focusedTextColor = Color.Black, // Color del texto en foco
                        unfocusedTextColor = Color.Black // Color del texto sin foco
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

// Campo de contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color(0xFF0047AB)) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color(0xFF0047AB),
                        focusedBorderColor = Color(0xFF0047AB),
                        focusedLabelColor = Color(0xFF0047AB),
                        unfocusedLabelColor = Color(0xFF0047AB),
                        cursorColor = Color(0xFF0047AB),
                        focusedTextColor = Color.Black, // Color del texto en foco
                        unfocusedTextColor = Color.Black // Color del texto sin foco
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de iniciar sesión
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                                val userId = authResult.user?.uid
                                if (userId != null) {
                                    onLoginSuccess(userId)
                                } else {
                                    snackbarHostState.showSnackbar("No se pudo iniciar sesión. Inténtalo de nuevo.")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error de inicio de sesión: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(8.dp)), // Sombra para dar profundidad al botón
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0047AB)) // Azul oscuro para el botón
                ) {
                    Text("Iniciar Sesión", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enlace para registrarse
                TextButton(onClick = { onNavigateToRegister() }) {
                    Text("¿No tienes cuenta? Regístrate aquí", color = Color(0xFF0047AB))
                }
            }
        }
    }
}
