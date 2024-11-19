package com.example.bdroomguiaejemplo.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.bdroomguiaejemplo.Model.Venta
import com.example.bdroomguiaejemplo.Model.Producto
import com.example.bdroomguiaejemplo.Model.Cliente
import com.example.bdroomguiaejemplo.R
import com.example.bdroomguiaejemplo.Repository.ClienteRepository
import com.example.bdroomguiaejemplo.Repository.ProductoRepository
import com.example.bdroomguiaejemplo.Repository.VentaRepository
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen(
    navController: NavHostController,
    ventaRepository: VentaRepository,
    productoRepository: ProductoRepository,
    clienteRepository: ClienteRepository
) {
    val productos = remember { mutableStateListOf<Producto>() }
    val ventas = remember { mutableStateListOf<Venta>() }
    val clientes = remember { mutableStateListOf<Cliente>() }
    var mostrarHistorial by remember { mutableStateOf(false) }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var mostrarDialogoProducto by remember { mutableStateOf(false) }
    var mostrarDialogoCliente by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        scope.launch {
            productos.addAll(productoRepository.obtenerProductos())
            clientes.addAll(clienteRepository.obtenerClientes())
            ventas.addAll(ventaRepository.obtenerVentas())
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondo5),
                contentDescription = "Fondo de pantalla",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Título principal
                Text(
                    "Registrar Nueva Venta",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0047AB), // Color azul para el título
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de selección
                ActionButton("Seleccionar Producto", Color(0xFF2196F3)) {
                    mostrarDialogoProducto = true
                }

                selectedProducto?.let {
                    Text("Producto seleccionado: ${it.nombre}", color = Color.Black, modifier = Modifier.padding(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                ActionButton("Seleccionar Cliente", Color(0xFF2196F3)) {
                    mostrarDialogoCliente = true
                }

                selectedCliente?.let {
                    Text("Cliente seleccionado: ${it.nombre}", color = Color.Black, modifier = Modifier.padding(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de entrada de cantidad
                InputField(
                    "Cantidad",
                    cantidad,
                    { if (it.all { char -> char.isDigit() }) cantidad = it },
                    labelColor = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de registrar venta
                ActionButton("Registrar Venta", Color(0xFF4CAF50)) {
                    scope.launch {
                        registrarVenta(
                            selectedProducto,
                            selectedCliente,
                            cantidad,
                            snackbarHostState,
                            productoRepository,
                            ventaRepository,
                            productos,
                            ventas
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de mostrar/ocultar historial
                ActionButton(
                    if (mostrarHistorial) "Ocultar Historial" else "Mostrar Historial",
                    Color(0xFF4CAF50)
                ) {
                    mostrarHistorial = !mostrarHistorial
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar historial de ventas
                if (mostrarHistorial && ventas.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        items(ventas) { venta ->
                            VentaCard(venta, productos, clientes)
                        }
                    }
                }

                if (mostrarDialogoProducto) {
                    MostrarDialogoSeleccionProducto(productos, snackbarHostState) {
                        selectedProducto = it
                        mostrarDialogoProducto = false
                    }
                }

                if (mostrarDialogoCliente) {
                    MostrarDialogoSeleccionCliente(clientes, snackbarHostState) {
                        selectedCliente = it
                        mostrarDialogoCliente = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaCard(venta: Venta, productos: List<Producto>, clientes: List<Cliente>) {
    val productoNombre = productos.find { it.id == venta.productoId }?.nombre ?: "Producto no encontrado"
    val clienteNombre = clientes.find { it.id == venta.clienteId }?.nombre ?: "Cliente no encontrado"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(15.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0047AB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Producto: $productoNombre", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Cliente: $clienteNombre", fontSize = 16.sp, color = Color.White)
            Text("Cantidad: ${venta.cantidad}", fontSize = 16.sp, color = Color.White)
            Text("Fecha: ${venta.fecha}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun MostrarDialogoSeleccionProducto(
    productos: List<Producto>,
    snackbarHostState: SnackbarHostState,
    onProductoSeleccionado: (Producto) -> Unit
) {
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = {
            val downloadUri = null
            onProductoSeleccionado(Producto(
            "0",
            "xxxx",
            0.0,
            0,
            downloadUri.toString()
        )) },
        title = { Text("Seleccionar Producto", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn {
                items(productos) { producto ->
                    Button(
                        onClick = {
                            onProductoSeleccionado(producto)
                            scope.launch {
                                snackbarHostState.showSnackbar("Producto seleccionado: ${producto.nombre}")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(producto.nombre)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val downloadUri = null
                onProductoSeleccionado(Producto(
                "0",
                "xxxx",
                0.0,
                0,
                downloadUri.toString()
            )) }) {
                Text("Cancelar", color = Color.White)
            }
        }
    )
}

@Composable
fun MostrarDialogoSeleccionCliente(
    clientes: List<Cliente>,
    snackbarHostState: SnackbarHostState,
    onClienteSeleccionado: (Cliente) -> Unit
) {
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onClienteSeleccionado(Cliente("0", "xxxx", "")) },
        title = { Text("Seleccionar Cliente", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn {
                items(clientes) { cliente ->
                    Button(
                        onClick = {
                            onClienteSeleccionado(cliente)
                            scope.launch {
                                snackbarHostState.showSnackbar("Cliente seleccionado: ${cliente.nombre}")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(cliente.nombre)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onClienteSeleccionado(Cliente("0", "xxxx", "")) }) {
                Text("Cancelar", color = Color.White)
            }
        }
    )
}

suspend fun registrarVenta(
    selectedProducto: Producto?,
    selectedCliente: Cliente?,
    cantidad: String,
    snackbarHostState: SnackbarHostState,
    productoRepository: ProductoRepository,
    ventaRepository: VentaRepository,
    productos: MutableList<Producto>,
    ventas: MutableList<Venta>
) {
    if (selectedProducto != null && selectedCliente != null && cantidad.isNotEmpty()) {
        val cantidadInt = cantidad.toInt()
        if (selectedProducto.stock >= cantidadInt) {
            val nuevaVenta = Venta(
                productoId = selectedProducto.id,
                clienteId = selectedCliente.id,
                cantidad = cantidadInt,
                fecha = Date()
            )

            val productoActualizado = selectedProducto.copy(
                stock = selectedProducto.stock - cantidadInt
            )
            productoRepository.actualizarProducto(productoActualizado)
            ventaRepository.insertarVenta(nuevaVenta)

            productos.clear()
            productos.addAll(productoRepository.obtenerProductos())
            ventas.clear()
            ventas.addAll(ventaRepository.obtenerVentas())

            snackbarHostState.showSnackbar("Venta registrada con éxito.")
        } else {
            snackbarHostState.showSnackbar("Stock insuficiente para la venta.")
        }
    } else {
        snackbarHostState.showSnackbar("Debe seleccionar producto, cliente y cantidad.")
    }
}
