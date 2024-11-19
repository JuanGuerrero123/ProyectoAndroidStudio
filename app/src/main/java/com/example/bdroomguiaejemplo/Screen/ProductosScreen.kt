package com.example.bdroomguiaejemplo.Screen

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.bdroomguiaejemplo.Model.Producto
import com.example.bdroomguiaejemplo.R
import com.example.bdroomguiaejemplo.Repository.ProductoRepository
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavHostController,
    productoRepository: ProductoRepository
) {
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<Uri?>(null) }
    var mostrarLista by remember { mutableStateOf(false) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val storage = FirebaseStorage.getInstance().reference
    val context = LocalContext.current

    // Launcher para seleccionar imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUrl = uri
        }
    )

    LaunchedEffect(Unit) {
        scope.launch {
            productos = productoRepository.obtenerProductos()
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
                painter = painterResource(id = R.drawable.fondo5),
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
                    text = "Gestión de Productos",
                    fontSize = 28.sp,
                    color = Color(0xFF0047AB),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Campos de entrada
                InputField("Nombre", nombre, onValueChange = { nombre = it }, labelColor = Color.White)
                InputField("Precio", precio, onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) precio = it }, labelColor = Color.White)
                InputField("Stock", stock, onValueChange = { if (it.all { char -> char.isDigit() }) stock = it }, labelColor = Color.White)

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para seleccionar una imagen
                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0047AB))
                ) {
                    Text("Seleccionar Imagen", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar la imagen seleccionada si existe
                imageUrl?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Imagen del Producto",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para agregar productos
                ActionButton("Agregar Producto", Color(0xFF0047AB)) {
                    if (nombre.isBlank() || precio.isBlank() || stock.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Todos los campos son obligatorios.") }
                    } else {
                        imageUrl?.let { uri ->
                            scope.launch(Dispatchers.IO) {
                                try {
                                    // Verificar si la URI no es nula
                                    if (uri == null || uri.path.isNullOrEmpty()) {
                                        throw Exception("Error: La URI de la imagen es nula o inválida.")
                                    }

                                    // Copiar la imagen a un archivo temporal antes de comprimirla
                                    val tempFile = createTempFileFromUri(context, uri)

                                    // Comprobar si el archivo temporal existe
                                    if (!tempFile.exists()) {
                                        throw Exception("El archivo temporal no existe.")
                                    }

                                    // Comprimir la imagen
                                    val compressedFile = Compressor.compress(context, tempFile) {
                                        default()
                                    }

                                    // Crear una URI a partir del archivo comprimido
                                    val compressedUri = Uri.fromFile(compressedFile)

                                    // Verificar que el archivo comprimido exista antes de subirlo
                                    if (compressedFile.exists()) {
                                        val imageRef = storage.child("productos/${System.currentTimeMillis()}_${compressedUri.lastPathSegment}")
                                        val uploadTask = imageRef.putFile(compressedUri)

                                        uploadTask.addOnSuccessListener {
                                            // Obtener URL de descarga de la imagen
                                            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                                val producto = Producto(
                                                    nombre = nombre,
                                                    precio = precio.toDoubleOrNull() ?: 0.0,
                                                    stock = stock.toIntOrNull() ?: 0,
                                                    imageUrl = downloadUri.toString() // Guardar la URL de la imagen
                                                )
                                                scope.launch(Dispatchers.Main) {
                                                    productoRepository.insertarProducto(producto)
                                                    productos = productoRepository.obtenerProductos()
                                                    snackbarHostState.showSnackbar("Producto agregado con éxito.")
                                                    nombre = ""
                                                    precio = ""
                                                    stock = ""
                                                    imageUrl = null
                                                }
                                            }.addOnFailureListener {
                                                scope.launch(Dispatchers.Main) {
                                                    snackbarHostState.showSnackbar("Error al obtener URL de descarga: ${it.message}")
                                                }
                                            }
                                        }.addOnFailureListener {
                                            scope.launch(Dispatchers.Main) {
                                                snackbarHostState.showSnackbar("Error al subir la imagen: ${it.message}")
                                            }
                                        }
                                    } else {
                                        scope.launch(Dispatchers.Main) {
                                            snackbarHostState.showSnackbar("El archivo comprimido no existe.")
                                        }
                                    }
                                } catch (e: Exception) {
                                    scope.launch(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar("Error al comprimir o subir la imagen: ${e.message}")
                                    }
                                }
                            }
                        } ?: run {
                            scope.launch {
                                snackbarHostState.showSnackbar("Debe seleccionar una imagen.")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para mostrar/ocultar la lista de productos
                ActionButton(if (mostrarLista) "Ocultar Lista" else "Listar Productos", Color(0xFF4CAF50)) {
                    mostrarLista = !mostrarLista
                    if (mostrarLista) {
                        scope.launch {
                            productos = productoRepository.obtenerProductos()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de productos
                if (mostrarLista) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(productos) { producto ->
                            ProductoCard(
                                producto = producto,
                                productoRepository = productoRepository,
                                onProductoSeleccionado = { productoSeleccionado = it },
                                onProductoActualizado = {
                                    scope.launch {
                                        productos = productoRepository.obtenerProductos()
                                    }
                                },
                                scope = scope,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }

                productoSeleccionado?.let {
                    ProductoInfoDialog(
                        producto = it,
                        productoRepository = productoRepository,
                        onDismiss = { productoSeleccionado = null },
                        onProductoActualizado = {
                            scope.launch {
                                productos = productoRepository.obtenerProductos()
                            }
                        }
                    )
                }
            }
        }
    }
}

// Función para crear archivo temporal desde URI
fun createTempFileFromUri(context: Context, uri: Uri): File {
    return try {
        val contentResolver = context.contentResolver
        val fileName = getFileName(contentResolver, uri) ?: "temp_image"
        val tempFile = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        tempFile
    } catch (e: Exception) {
        throw Exception("Error al crear el archivo temporal: ${e.message}")
    }
}

@SuppressLint("Range")
fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
    var name: String? = null
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }
    return name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoInfoDialog(
    producto: Producto,
    productoRepository: ProductoRepository,
    onDismiss: () -> Unit,
    onProductoActualizado: () -> Unit
) {
    var nombre by remember { mutableStateOf(producto.nombre) }
    var precio by remember { mutableStateOf(producto.precio.toString()) }
    var stock by remember { mutableStateOf(producto.stock.toString()) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Editar Producto",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0047AB)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InputField("Nombre", nombre, onValueChange = { nombre = it }, labelColor = Color(0xFF0047AB))
                InputField("Precio", precio, onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) precio = it }, labelColor = Color(0xFF0047AB))
                InputField("Stock", stock, onValueChange = { if (it.all { char -> char.isDigit() }) stock = it }, labelColor = Color(0xFF0047AB))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val productoActualizado = producto.copy(
                        nombre = nombre,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0
                    )
                    scope.launch {
                        productoRepository.actualizarProducto(productoActualizado)
                        onProductoActualizado()
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
fun ProductoCard(
    producto: Producto,
    productoRepository: ProductoRepository,
    onProductoSeleccionado: (Producto) -> Unit,
    onProductoActualizado: () -> Unit,
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
            producto.imageUrl?.let { url ->
                Image(
                    painter = rememberImagePainter(url),
                    contentDescription = "Imagen del Producto",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = producto.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Precio: ${producto.precio}",
                    color = Color.White
                )
                Text(
                    text = "Stock: ${producto.stock}",
                    color = Color.White
                )
            }

            Row {
                IconButtonWithLabel(
                    icon = Icons.Default.Info,
                    label = "Info",
                    onClick = { onProductoSeleccionado(producto) },
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButtonWithLabel(
                    icon = Icons.Default.Delete,
                    label = "Eliminar",
                    onClick = {
                        scope.launch {
                            productoRepository.eliminarProducto(producto.id)
                            snackbarHostState.showSnackbar("Producto eliminado con éxito.")
                            onProductoActualizado()
                        }
                    },
                    tint = Color.White
                )
            }
        }
    }
}
