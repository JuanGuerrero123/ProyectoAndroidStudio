package com.example.bdroomguiaejemplo.Repository

import com.example.bdroomguiaejemplo.Model.Cliente
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ClienteRepository(private val db: FirebaseFirestore) {
    private val clientesCollection = db.collection("Clientes")

    suspend fun obtenerClientes(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        try {
            val result = clientesCollection.get().await()
            for (document in result.documents) {
                val cliente = document.toObject(Cliente::class.java)
                if (cliente != null) {
                    cliente.id = document.id // Asigna el ID del documento
                    clientes.add(cliente)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return clientes
    }

    suspend fun insertarCliente(cliente: Cliente) {
        try {
            clientesCollection.add(cliente).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun actualizarCliente(cliente: Cliente) {
        try {
            clientesCollection.document(cliente.id).set(cliente).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun eliminarCliente(clienteId: String) {
        try {
            clientesCollection.document(clienteId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
