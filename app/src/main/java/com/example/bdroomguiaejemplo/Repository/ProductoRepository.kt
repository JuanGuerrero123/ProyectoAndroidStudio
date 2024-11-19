package com.example.bdroomguiaejemplo.Repository

import com.example.bdroomguiaejemplo.Model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoRepository(private val db: FirebaseFirestore) {
    private val productosCollection = db.collection("Productos")

    suspend fun obtenerProductos(): List<Producto> {
        val productos = mutableListOf<Producto>()
        try {
            val result = productosCollection.get().await()
            for (document in result.documents) {
                val producto = document.toObject(Producto::class.java)
                if (producto != null) {
                    producto.id = document.id // Asigna el ID del documento
                    productos.add(producto)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return productos
    }

    suspend fun insertarProducto(producto: Producto) {
        try {
            productosCollection.add(producto).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun actualizarProducto(producto: Producto) {
        try {
            productosCollection.document(producto.id).set(producto).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun eliminarProducto(productoId: String) {
        try {
            productosCollection.document(productoId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
