package com.example.tiendaonlineapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "carrito",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["producto_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CarritoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuario_id: Int,
    val producto_id: Int,
    val cantidad: Int
)