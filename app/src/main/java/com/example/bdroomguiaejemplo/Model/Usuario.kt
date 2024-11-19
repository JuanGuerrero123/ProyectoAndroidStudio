package com.example.bdroomguiaejemplo.Model

data class Usuario(
    var id: String = "", // ID del documento en Firestore, que es el UID del usuario
    var nombre: String = "",
    var correo: String = "",
    var rol: String = "",
    var fechaRegistro: String = "" // Puede ser una cadena que represente la fecha
)
