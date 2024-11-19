package com.example.bdroomguiaejemplo.Screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.bdroomguiaejemplo.Repository.ClienteRepository
import com.example.bdroomguiaejemplo.Repository.ProductoRepository
import com.example.bdroomguiaejemplo.Repository.VentaRepository
import com.example.bdroomguiaejemplo.Repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.material3.*
import android.util.Log
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    productoRepository: ProductoRepository,
    clienteRepository: ClienteRepository,
    ventaRepository: VentaRepository,
    usuarioRepository: UsuarioRepository
) {
    val navController = rememberNavController()
    var userRole by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    val currentUser = auth.currentUser
    var showSplashOnLogout by remember { mutableStateOf(false) }

    // Mostrar la SplashScreen mientras carga la aplicación
    LaunchedEffect(currentUser) {
        isLoading = true
        if (currentUser != null) {
            val userId = currentUser.uid
            try {
                val document = FirebaseFirestore.getInstance().collection("Usuarios").document(userId).get().await()
                userRole = document.getString("rol")
            } catch (e: Exception) {
                userRole = null
                Log.e("MainApp", "Error al obtener el rol del usuario: ${e.message}")
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
            userRole = null // Restablecer el rol si el usuario es nulo
        }
    }

    // Mostrar la splash screen si es la primera carga o al cerrar sesión
    if (isLoading || showSplashOnLogout) {
        SplashScreen(navController = navController, initialLaunch = !showSplashOnLogout)
    } else {
        if (currentUser == null || userRole == null) {
            // Navegación de autenticación para login/registro
            AuthNav(navController = navController, onLoginSuccess = { userId ->
                coroutineScope.launch {
                    isLoading = true
                    try {
                        val document = FirebaseFirestore.getInstance().collection("Usuarios").document(userId).get().await()
                        userRole = document.getString("rol")
                        isLoading = false
                    } catch (e: Exception) {
                        isLoading = false
                        Log.e("MainApp", "Error al establecer el rol después del inicio de sesión: ${e.message}")
                    }
                }
            })
        } else {
            // Interfaz principal de la aplicación si hay un usuario autenticado y se ha obtenido el rol
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Gestión de Inventarios") },
                        actions = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    try {
                                        // Mostrar la SplashScreen
                                        showSplashOnLogout = true
                                        delay(2000) // Tiempo para mostrar la SplashScreen

                                        // Cerrar sesión
                                        auth.signOut()
                                        userRole = null // Restablecer el rol al cerrar sesión
                                        showSplashOnLogout = false

                                        // Navegar al login después de cerrar sesión y restablecer la pila de navegación
                                        navController.navigate("login") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    } catch (e: Exception) {
                                        Log.e("MainApp", "Error al cerrar sesión: ${e.message}")
                                    }
                                }
                            }) {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                            }
                        }
                    )
                },
                bottomBar = {
                    if (userRole == "Dueño" || userRole == "Empleado") {
                        AppBottomNavigation(navController)
                    }
                }
            ) { innerPadding ->
                Navigation(
                    navController = navController,
                    productoRepository = productoRepository,
                    clienteRepository = clienteRepository,
                    ventaRepository = ventaRepository,
                    usuarioRepository = usuarioRepository,
                    modifier = Modifier.padding(innerPadding),
                    userRole = userRole!!
                )
            }
        }
    }
}
