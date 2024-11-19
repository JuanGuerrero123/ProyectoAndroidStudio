package com.example.bdroomguiaejemplo.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.window.PopupProperties
import com.example.bdroomguiaejemplo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val roles = listOf("Dueño", "Empleado")
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
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

            if (isLoading) {
                SplashScreen()
            } else {
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

                    // Título del formulario
                    Text(
                        "Registrar Usuario",
                        fontSize = 24.sp,
                        color = Color(0xFF0047AB),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campos de entrada
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico", color = Color(0xFF0047AB)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color(0xFF0047AB),
                            focusedBorderColor = Color(0xFF0047AB),
                            cursorColor = Color(0xFF0047AB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = Color(0xFF0047AB)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color(0xFF0047AB),
                            focusedBorderColor = Color(0xFF0047AB),
                            cursorColor = Color(0xFF0047AB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Contraseña", color = Color(0xFF0047AB)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color(0xFF0047AB),
                            focusedBorderColor = Color(0xFF0047AB),
                            cursorColor = Color(0xFF0047AB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Menú desplegable para seleccionar el rol
                    OutlinedTextField(
                        value = selectedRole ?: "Seleccione un rol",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol", color = Color(0xFF0047AB)) },
                        trailingIcon = {
                            IconButton(onClick = { isDropdownExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF0047AB))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color(0xFF0047AB),
                            focusedBorderColor = Color(0xFF0047AB),
                            cursorColor = Color(0xFF0047AB)
                        )
                    )

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        properties = PopupProperties(focusable = false),
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxWidth()
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role, color = Color.Black) },
                                onClick = {
                                    selectedRole = role
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de registro
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || selectedRole == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Todos los campos son obligatorios.")
                                }
                            } else if (password != confirmPassword) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Las contraseñas no coinciden.")
                                }
                            } else {
                                isLoading = true
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val userId = task.result?.user?.uid ?: ""
                                            val user = hashMapOf(
                                                "email" to email,
                                                "rol" to selectedRole
                                            )
                                            firestore.collection("Usuarios").document(userId)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    isLoading = false
                                                    onRegisterSuccess()
                                                }
                                                .addOnFailureListener {
                                                    isLoading = false
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Error al guardar los datos.")
                                                    }
                                                }
                                        } else {
                                            isLoading = false
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Error en el registro.")
                                            }
                                        }
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0047AB))
                    ) {
                        Text("Registrarse", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)), // Fondo semi-transparente para indicar la carga
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF0047AB))
    }
}
