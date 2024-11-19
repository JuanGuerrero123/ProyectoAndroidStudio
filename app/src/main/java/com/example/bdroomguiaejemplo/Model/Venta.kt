package com.example.bdroomguiaejemplo.Model

import java.util.Date

data class Venta(
    var id: String = "", // Usar un String si el ID será automático en Firestore
    var productoId: String = "",
    var clienteId: String = "",
    var cantidad: Int = 0,
    var fecha: Date = Date()
)
