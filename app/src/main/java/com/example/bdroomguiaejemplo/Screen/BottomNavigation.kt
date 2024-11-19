package com.example.bdroomguiaejemplo.Screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Share // Importar el ícono de gráfico
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem("productos", Icons.Default.Home, "Productos"),
        BottomNavItem("clientes", Icons.Default.Person, "Clientes"),
        BottomNavItem("ventas", Icons.Default.ShoppingCart, "Ventas"),
        BottomNavItem("balance", Icons.Default.Share, "Balance")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Color(0xFF0047AB) else Color(
                            0xFF000000
                        ) // Color más intenso para el ícono seleccionado
                    )
                },
                label = {
                    Text(
                        item.label,
                        color = if (currentRoute == item.route) Color(0xFF0047AB) else Color(
                            0xFF0A0A0A
                        ) // Color más intenso para el texto del ítem seleccionado
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0047AB),  // Color del ícono cuando está seleccionado
                    unselectedIconColor = Color(0xFF050404),  // Color del ícono cuando no está seleccionado
                    selectedTextColor = Color(0xFF0047AB),  // Color del texto cuando está seleccionado
                    unselectedTextColor = Color(0xFF0C0A0A)  // Color del texto cuando no está seleccionado
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)
