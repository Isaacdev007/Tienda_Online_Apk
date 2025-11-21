package com.example.tiendaonlineapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tiendaonlineapp.database.AppDatabase
import com.example.tiendaonlineapp.database.CarritoItem
import com.example.tiendaonlineapp.database.Producto
import com.example.tiendaonlineapp.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductosActivity : AppCompatActivity() {

    private lateinit var btnVerCarrito: Button
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnAgregarProducto1: Button
    private lateinit var btnAgregarProducto2: Button
    private lateinit var btnAgregarProducto3: Button

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    private var productos = listOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        // Inicializar
        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        // Referencias
        btnVerCarrito = findViewById(R.id.btnVerCarrito)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnAgregarProducto1 = findViewById(R.id.btnAgregarProducto1)
        btnAgregarProducto2 = findViewById(R.id.btnAgregarProducto2)
        btnAgregarProducto3 = findViewById(R.id.btnAgregarProducto3)

        // Cargar productos desde la base de datos
        cargarProductos()

        // Configurar botones de agregar
        btnAgregarProducto1.setOnClickListener { agregarAlCarrito(1) }
        btnAgregarProducto2.setOnClickListener { agregarAlCarrito(2) }
        btnAgregarProducto3.setOnClickListener { agregarAlCarrito(3) }

        // Botón ver carrito
        btnVerCarrito.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            startActivity(intent)
        }

        // Botón cerrar sesión
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    override fun onResume() {
        super.onResume()
        // Renovar sesión cada vez que la actividad esté visible
        sessionManager.renovarSesion()
        actualizarContadorCarrito()
    }

    /**
     * Muestra diálogo de confirmación para cerrar sesión
     */
    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?\n\nSe vaciará tu carrito de compras.")
            .setPositiveButton("Sí, cerrar") { dialog, _ ->
                cerrarSesion()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Cierra la sesión y vacía el carrito
     */
    private fun cerrarSesion() {
        val usuarioId = sessionManager.obtenerUsuarioId()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Vaciar carrito del usuario
                database.carritoDao().eliminarPorUsuario(usuarioId)

                withContext(Dispatchers.Main) {
                    // Cerrar sesión
                    sessionManager.cerrarSesion()

                    Toast.makeText(
                        this@ProductosActivity,
                        "Sesión cerrada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Ir al Login y limpiar el stack de actividades
                    val intent = Intent(this@ProductosActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProductosActivity,
                        "Error al cerrar sesión: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Carga los productos desde la base de datos
     */
    private fun cargarProductos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                productos = database.productoDao().obtenerTodos()

                withContext(Dispatchers.Main) {
                    if (productos.isEmpty()) {
                        Toast.makeText(
                            this@ProductosActivity,
                            "No hay productos disponibles",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProductosActivity,
                        "Error al cargar productos: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Agrega un producto al carrito
     */
    private fun agregarAlCarrito(productoId: Int) {
        val usuarioId = sessionManager.obtenerUsuarioId()

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Verificar si el producto ya está en el carrito
                val itemExistente = database.carritoDao().obtenerItem(usuarioId, productoId)

                if (itemExistente != null) {
                    // Aumentar cantidad
                    database.carritoDao().actualizarCantidad(
                        itemExistente.id,
                        itemExistente.cantidad + 1
                    )
                } else {
                    // Agregar nuevo item
                    val nuevoItem = CarritoItem(
                        usuario_id = usuarioId,
                        producto_id = productoId,
                        cantidad = 1
                    )
                    database.carritoDao().insertar(nuevoItem)
                }

                // Obtener nombre del producto
                val producto = database.productoDao().obtenerPorId(productoId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProductosActivity,
                        "${producto?.nombre ?: "Producto"} agregado al carrito",
                        Toast.LENGTH_SHORT
                    ).show()
                    actualizarContadorCarrito()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProductosActivity,
                        "Error al agregar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Actualiza el contador del botón del carrito
     */
    private fun actualizarContadorCarrito() {
        val usuarioId = sessionManager.obtenerUsuarioId()

        if (usuarioId == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cantidadItems = database.carritoDao().contarItems(usuarioId)

                withContext(Dispatchers.Main) {
                    if (cantidadItems > 0) {
                        btnVerCarrito.text = "Ver Carrito ($cantidadItems)"
                    } else {
                        btnVerCarrito.text = "Ver Carrito"
                    }
                }
            } catch (e: Exception) {
                // Silencioso, no es crítico
            }
        }
    }
}