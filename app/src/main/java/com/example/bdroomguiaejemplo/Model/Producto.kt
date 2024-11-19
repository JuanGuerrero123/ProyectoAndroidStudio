package com.example.bdroomguiaejemplo.Model

data class Producto(
    var id: String = "", // Usar un String si el ID será automático en Firestore
    var nombre: String = "",
    var precio: Double = 0.0,
    var stock: Int = 0,
    var imageUrl: String? = null
)
