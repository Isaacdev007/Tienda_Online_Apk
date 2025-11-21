package com.example.tiendaonlineapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CarritoDao {

    @Insert
    suspend fun insertar(item: CarritoItem)

    @Query("SELECT * FROM carrito WHERE usuario_id = :usuarioId AND producto_id = :productoId LIMIT 1")
    suspend fun obtenerItem(usuarioId: Int, productoId: Int): CarritoItem?

    @Query("UPDATE carrito SET cantidad = :cantidad WHERE id = :itemId")
    suspend fun actualizarCantidad(itemId: Int, cantidad: Int)

    @Query("""
        SELECT c.id, c.usuario_id, c.producto_id, c.cantidad 
        FROM carrito c 
        WHERE c.usuario_id = :usuarioId
    """)
    suspend fun obtenerPorUsuario(usuarioId: Int): List<CarritoItem>

    @Query("DELETE FROM carrito WHERE usuario_id = :usuarioId")
    suspend fun eliminarPorUsuario(usuarioId: Int)

    @Query("SELECT COUNT(*) FROM carrito WHERE usuario_id = :usuarioId")
    suspend fun contarItems(usuarioId: Int): Int

    // ⭐ NUEVAS FUNCIONES

    /**
     * Elimina un item específico del carrito
     */
    @Query("DELETE FROM carrito WHERE id = :itemId")
    suspend fun eliminarItem(itemId: Int)

    /**
     * Aumenta la cantidad de un item en 1
     */
    @Query("UPDATE carrito SET cantidad = cantidad + 1 WHERE id = :itemId")
    suspend fun aumentarCantidad(itemId: Int)

    /**
     * Disminuye la cantidad de un item en 1
     * Si la cantidad llega a 0, el item se elimina automáticamente
     */
    @Query("UPDATE carrito SET cantidad = cantidad - 1 WHERE id = :itemId AND cantidad > 1")
    suspend fun disminuirCantidad(itemId: Int): Int

    /**
     * Obtiene la cantidad actual de un item
     */
    @Query("SELECT cantidad FROM carrito WHERE id = :itemId")
    suspend fun obtenerCantidad(itemId: Int): Int?
}