package com.example.bdroomguiaejemplo.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bdroomguiaejemplo.Model.Cliente
import com.example.bdroomguiaejemplo.R
import com.example.bdroomguiaejemplo.Repository.ClienteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    navController: NavHostController,
    clienteRepository: ClienteRepository
) {
    var clientes by remember { mutableStateOf<List<Cliente>>(emptyList()) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clienteSeleccionado by remember { mutableStateOf<Cliente?>(null) }
    var mostrarLista by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        scope.launch {
            clientes = clienteRepository.obtenerClientes()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondo5), // Asegúrate de tener esta imagen en drawable
                contentDescription = "Fondo de pantalla",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Gestión de Clientes",
                    fontSize = 28.sp,
                    color = Color(0xFF0047AB), // Cambié el color a azul para que sea visible
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Campos de entrada
                InputField("Nombre", nombre, onValueChange = { nombre = it }, labelColor = Color.White)
                InputField("Correo", correo, onValueChange = { correo = it }, labelColor = Color.White)

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para agregar clientes
                ActionButton("Agregar Cliente", Color(0xFF0047AB)) {
                    if (nombre.isBlank() || correo.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Todos los campos son obligatorios.") }
                    } else {
                        val cliente = Cliente(nombre = nombre, correo = correo)
                        scope.launch {
                            clienteRepository.insertarCliente(cliente)
                            clientes = clienteRepository.obtenerClientes()
                            snackbarHostState.showSnackbar("Cliente agregado con éxito.")
                            nombre = ""
                            correo = ""
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para mostrar/ocultar la lista de clientes
                ActionButton(if (mostrarLista) "Ocultar Lista" else "Listar Clientes", Color(0xFF4CAF50)) {
                    mostrarLista = !mostrarLista
                    if (mostrarLista) {
                        scope.launch {
                            clientes = clienteRepository.obtenerClientes()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de clientes
                if (mostrarLista) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(clientes) { cliente ->
                            ClienteCard(
                                cliente = cliente,
                                clienteRepository = clienteRepository,
                                onClienteSeleccionado = { clienteSeleccionado = it },
                                onClienteActualizado = {
                                    scope.launch {
                                        clientes = clienteRepository.obtenerClientes()
                                    }
                                },
                                scope = scope,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }

                clienteSeleccionado?.let {
                    ClienteInfoDialog(
                        cliente = it,
                        clienteRepository = clienteRepository,
                        onDismiss = { clienteSeleccionado = null },
                        onClienteActualizado = {
                            scope.launch {
                                clientes = clienteRepository.obtenerClientes()
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteInfoDialog(
    cliente: Cliente,
    clienteRepository: ClienteRepository,
    onDismiss: () -> Unit,
    onClienteActualizado: () -> Unit
) {
    var nombre by remember { mutableStateOf(cliente.nombre) }
    var correo by remember { mutableStateOf(cliente.correo) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Editar Cliente",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0047AB)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InputField("Nombre", nombre, onValueChange = { nombre = it }, labelColor = Color(0xFF0047AB))
                InputField("Correo", correo, onValueChange = { correo = it }, labelColor = Color(0xFF0047AB))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clienteActualizado = cliente.copy(nombre = nombre, correo = correo)
                    scope.launch {
                        clienteRepository.actualizarCliente(clienteActualizado)
                        onClienteActualizado()
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                Text("Cancelar", color = Color.White)
            }
        }
    )
}

@Composable
fun ClienteCard(
    cliente: Cliente,
    clienteRepository: ClienteRepository,
    onClienteSeleccionado: (Cliente) -> Unit,
    onClienteActualizado: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(15.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0047AB))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cliente.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row {
                IconButtonWithLabel(
                    icon = Icons.Default.Info,
                    label = "Info",
                    onClick = { onClienteSeleccionado(cliente) },
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButtonWithLabel(
                    icon = Icons.Default.Delete,
                    label = "Eliminar",
                    onClick = {
                        scope.launch {
                            clienteRepository.eliminarCliente(cliente.id)
                            snackbarHostState.showSnackbar("Cliente eliminado con éxito.")
                            // Actualiza la lista inmediatamente
                            onClienteActualizado()
                        }
                    },
                    tint = Color.White
                )
            }
        }
    }
}
