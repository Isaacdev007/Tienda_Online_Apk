package com.example.tiendaonlineapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductoDao {

    @Insert
    suspend fun insertar(producto: Producto)

    @Query("SELECT * FROM productos")
    suspend fun obtenerTodos(): List<Producto>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Producto?
}