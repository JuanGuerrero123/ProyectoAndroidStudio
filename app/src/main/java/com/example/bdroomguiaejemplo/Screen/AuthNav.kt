package com.example.bdroomguiaejemplo.Screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AuthNav(
    navController: NavHostController,
    onLoginSuccess: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userId ->
                    onLoginSuccess(userId)
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }
    }
}