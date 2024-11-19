package com.example.bdroomguiaejemplo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bdroomguiaejemplo.Repository.ClienteRepository
import com.example.bdroomguiaejemplo.Repository.ProductoRepository
import com.example.bdroomguiaejemplo.Repository.VentaRepository
import com.example.bdroomguiaejemplo.Repository.UsuarioRepository
import com.example.bdroomguiaejemplo.Screen.MainApp
import com.example.bdroomguiaejemplo.ui.theme.BDRoomGuiaEjemploTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = FirebaseFirestore.getInstance()

        val productoRepository = ProductoRepository(db)
        val clienteRepository = ClienteRepository(db)
        val ventaRepository = VentaRepository(db)
        val usuarioRepository = UsuarioRepository(db)

        setContent {
            BDRoomGuiaEjemploTheme {
                MainApp(
                    productoRepository = productoRepository,
                    clienteRepository = clienteRepository,
                    ventaRepository = ventaRepository,
                    usuarioRepository = usuarioRepository
                )
            }
        }
    }
}
