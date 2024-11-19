package com.example.bdroomguiaejemplo.Repository

import com.example.bdroomguiaejemplo.Model.Venta
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VentaRepository(private val db: FirebaseFirestore) {
    private val ventasCollection = db.collection("Ventas")

    suspend fun obtenerVentas(): List<Venta> {
        val ventas = mutableListOf<Venta>()
        try {
            val result = ventasCollection.get().await()
            for (document in result.documents) {
                val venta = document.toObject(Venta::class.java)
                if (venta != null) {
                    venta.id = document.id // Asigna el ID del documento
                    ventas.add(venta)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ventas
    }

    suspend fun insertarVenta(venta: Venta) {
        try {
            ventasCollection.add(venta).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun actualizarVenta(venta: Venta) {
        try {
            ventasCollection.document(venta.id).set(venta).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun eliminarVenta(ventaId: String) {
        try {
            ventasCollection.document(ventaId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
