package com.example.bdroomguiaejemplo.Repository

import com.example.bdroomguiaejemplo.Model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepository(private val db: FirebaseFirestore) {
    private val usuariosCollection = db.collection("Usuarios")

    suspend fun obtenerUsuarios(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        try {
            val result = usuariosCollection.get().await()
            for (document in result.documents) {
                val usuario = document.toObject(Usuario::class.java)
                if (usuario != null) {
                    usuario.id = document.id // Asigna el ID del documento
                    usuarios.add(usuario)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return usuarios
    }

    suspend fun insertarUsuario(usuario: Usuario) {
        try {
            usuariosCollection.add(usuario).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun actualizarUsuario(usuario: Usuario) {
        try {
            usuariosCollection.document(usuario.id).set(usuario).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun eliminarUsuario(usuarioId: String) {
        try {
            usuariosCollection.document(usuarioId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
