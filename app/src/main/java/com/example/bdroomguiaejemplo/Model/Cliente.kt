package com.example.bdroomguiaejemplo.Model

data class Cliente(
    var id: String = "", // Usar un String si el ID será el UID de Firebase
    var nombre: String = "",
    var correo: String = ""
)
