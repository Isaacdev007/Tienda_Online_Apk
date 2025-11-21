package com.example.tiendaonlineapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double
) {
    fun precioFormateado(): String = "$${"%,.0f".format(precio)}"
}