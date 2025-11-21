package com.example.tiendaonlineapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Usuario::class, Producto::class, CarritoItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tienda_online_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback para pre-cargar datos iniciales
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    precargarProductos(database.productoDao())
                }
            }
        }

        suspend fun precargarProductos(productoDao: ProductoDao) {
            // Productos iniciales
            val productos = listOf(
                Producto(
                    id = 1,
                    nombre = "Camiseta Deportiva",
                    descripcion = "Camiseta deportiva de alta calidad",
                    precio = 45000.0
                ),
                Producto(
                    id = 2,
                    nombre = "Audífonos Bluetooth",
                    descripcion = "Sonido premium inalámbrico",
                    precio = 80000.0
                ),
                Producto(
                    id = 3,
                    nombre = "Reloj Inteligente",
                    descripcion = "Monitorea tu salud y actividad",
                    precio = 250000.0
                )
            )

            productos.forEach { productoDao.insertar(it) }
        }
    }
}