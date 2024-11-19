package com.example.bdroomguiaejemplo.Screen

import android.graphics.Color.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bdroomguiaejemplo.Model.Producto
import com.example.bdroomguiaejemplo.Model.Venta
import com.example.bdroomguiaejemplo.R
import com.example.bdroomguiaejemplo.Repository.ProductoRepository
import com.example.bdroomguiaejemplo.Repository.VentaRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceVentasScreen(
    navController: NavController,
    ventaRepository: VentaRepository,
    productoRepository: ProductoRepository
) {
    var totalVentas by remember { mutableStateOf(0) }
    var totalGanancias by remember { mutableStateOf(0.0) }
    var ventasPorProducto by remember { mutableStateOf<Map<String, Pair<Int, Int>>>(emptyMap()) }
    val productosList = remember { mutableStateListOf<Producto>() }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var mostrarListaProductos by remember { mutableStateOf(false) }
    var mostrarGrafica by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    // Refresca los datos cada vez que la pantalla de Balance se monta
    LaunchedEffect(Unit) {
        obtenerDatos(scope, db, productosList, ventaRepository, productoRepository) { ventas, productos ->
            totalVentas = ventas.size
            totalGanancias = ventas.sumOf { venta ->
                val producto = productos.find { it.id == venta.productoId }
                (producto?.precio ?: 0.0) * venta.cantidad
            }
            ventasPorProducto = ventas.groupBy { venta ->
                productos.find { it.id == venta.productoId }?.nombre ?: "Desconocido"
            }.mapValues { entry ->
                val cantidadVendida = entry.value.sumOf { it.cantidad }
                val stockActual = productos.find { it.nombre == entry.key }?.stock ?: 0
                Pair(cantidadVendida, stockActual)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = SnackbarHostState()) }) { padding ->
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
                    text = "Balance de Ventas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0047AB),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Resumen de ventas y ganancias
                Text(
                    "Total de ventas realizadas: $totalVentas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Text(
                    "Ganancias totales: $${"%.2f".format(totalGanancias)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón para mostrar/ocultar la lista de productos
                Button(
                    onClick = {
                        mostrarListaProductos = !mostrarListaProductos
                        if (!mostrarListaProductos) {
                            selectedProducto = null
                            mostrarGrafica = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(if (mostrarListaProductos) "Ocultar Productos" else "Mostrar Productos", color = Color.White)
                }

                // Lista de productos
                if (mostrarListaProductos) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        items(productosList) { producto ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .shadow(2.dp, RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                onClick = {
                                    selectedProducto = producto
                                    mostrarGrafica = true
                                    mostrarListaProductos = false
                                }
                            ) {
                                Text(
                                    text = producto.nombre,
                                    modifier = Modifier.padding(16.dp),
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar gráfica de ventas del producto seleccionado
                if (mostrarGrafica && selectedProducto != null) {
                    selectedProducto?.let { producto ->
                        Text(
                            "Ventas de: ${producto.nombre}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF000000),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        GraficarVentas(
                            ventasPorProducto = mapOf(
                                producto.nombre to Pair(
                                    ventasPorProducto[producto.nombre]?.first ?: 0,
                                    producto.stock
                                )
                            )
                        )

                        // Botón para ocultar la gráfica
                        Button(
                            onClick = {
                                mostrarGrafica = false
                                selectedProducto = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Ocultar Gráfica", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de volver a ventas
                Button(
                    onClick = {
                        navController.navigate("ventas") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0047AB))
                ) {
                    Text("Volver a Ventas", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GraficarVentas(ventasPorProducto: Map<String, Pair<Int, Int>>) {
    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFFFFC107),
        Color(0xFF2196F3),
        Color(0xFFF44336),
        Color(0xFF9C27B0)
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val barWidth = size.width / (ventasPorProducto.size * 3f)
        var xOffset = 0f
        val colorIterator = colors.iterator()

        ventasPorProducto.toList().forEachIndexed { index, (producto, data) ->
            val (cantidadVendida, stock) = data
            var color = if (colorIterator.hasNext()) colorIterator.next() else colors.random()

            val barHeight = (cantidadVendida * 10f).coerceAtMost(size.height)
            val stockHeight = (stock * 10f).coerceAtMost(size.height)

            // Dibuja la barra de ventas
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(xOffset, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )

            // Dibuja la barra de stock restante al lado derecho de la barra de ventas
            drawRect(
                color = Color.Gray,
                topLeft = androidx.compose.ui.geometry.Offset(xOffset + barWidth + 10f, size.height - stockHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, stockHeight)
            )

            // Añadir etiquetas de producto y cantidad vendida
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = Color(0xFFE3D6E5) // Cambié el color a blanco para mejor visibilidad
                    textSize = 40f // Aumenté el tamaño de la fuente para que sea más visible
                    isFakeBoldText = true
                }
                drawText(
                    producto,
                    xOffset + barWidth / 4,
                    size.height + 30f,
                    textPaint
                )
                drawText(
                    "Vendidos: $cantidadVendida",
                    xOffset,
                    size.height - barHeight - 15f,
                    textPaint
                )
                drawText(
                    "Stock: $stock",
                    xOffset + barWidth + 10f,
                    size.height - stockHeight - 15f,
                    textPaint
                )
            }

            xOffset += barWidth * 3
        }
    }
}

suspend fun obtenerDatos(
    scope: CoroutineScope,
    db: FirebaseFirestore,
    productosList: MutableList<Producto>,
    ventaRepository: VentaRepository,
    productoRepository: ProductoRepository,
    callback: (List<Venta>, List<Producto>) -> Unit
) {
    scope.launch {
        withContext(Dispatchers.IO) {
            try {
                val productosSnapshot = db.collection("Productos").get().await()
                val ventasSnapshot = db.collection("Ventas").get().await()

                val productos = productosSnapshot.documents.mapNotNull { document ->
                    document.toObject(Producto::class.java)?.apply {
                        id = document.id
                    }
                }

                productosList.clear()
                productosList.addAll(productos)

                val ventas = ventasSnapshot.documents.mapNotNull { document ->
                    document.toObject(Venta::class.java)?.apply {
                        id = document.id
                    }
                }

                callback(ventas, productos)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
