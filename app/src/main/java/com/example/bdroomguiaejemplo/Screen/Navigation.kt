package com.example.bdroomguiaejemplo.Screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bdroomguiaejemplo.Repository.ClienteRepository
import com.example.bdroomguiaejemplo.Repository.ProductoRepository
import com.example.bdroomguiaejemplo.Repository.VentaRepository
import com.example.bdroomguiaejemplo.Repository.UsuarioRepository

@Composable
fun Navigation(
    navController: NavHostController,
    productoRepository: ProductoRepository,
    clienteRepository: ClienteRepository,
    ventaRepository: VentaRepository,
    usuarioRepository: UsuarioRepository,
    modifier: Modifier = Modifier,
    userRole: String
) {
    NavHost(
        navController = navController,
        startDestination = if (userRole == "Dueño") "productos" else "clientes",
        modifier = modifier
    ) {
        composable("productos") {
            if (userRole == "Dueño") {
                ProductosScreen(navController, productoRepository)
            }
        }
        composable("clientes") {
            ClientesScreen(navController, clienteRepository)
        }
        composable("ventas") {
            VentasScreen(navController, ventaRepository, productoRepository, clienteRepository)
        }
        composable("balance") {
            if (userRole == "Dueño") {
                BalanceVentasScreen(
                    navController = navController,
                    ventaRepository = ventaRepository,
                    productoRepository = productoRepository
                )
            }
        }
    }
}
