package com.example.tiendaonlineapp

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendaonlineapp.database.AppDatabase
import com.example.tiendaonlineapp.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarritoActivity : AppCompatActivity() {

    private lateinit var btnFinalizar: Button

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // Inicializar
        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        btnFinalizar = findViewById(R.id.btnFinalizar)

        // Cargar productos del carrito
        cargarCarrito()

        // Botón finalizar compra
        btnFinalizar.setOnClickListener {
            mostrarDialogoConfirmacion()
        }
    }

    /**
     * Carga los productos del carrito desde la base de datos
     */
    private fun cargarCarrito() {
        val usuarioId = sessionManager.obtenerUsuarioId()

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val itemsCarrito = database.carritoDao().obtenerPorUsuario(usuarioId)

                if (itemsCarrito.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        mostrarCarritoVacio()
                    }
                    return@launch
                }

                // Obtener detalles de cada producto
                var total = 0.0
                val detalles = StringBuilder()

                for (item in itemsCarrito) {
                    val producto = database.productoDao().obtenerPorId(item.producto_id)
                    if (producto != null) {
                        val subtotal = producto.precio * item.cantidad
                        total += subtotal

                        detalles.append("${producto.nombre}\n")
                        detalles.append("Precio: ${producto.precioFormateado()}\n")
                        detalles.append("Cantidad: ${item.cantidad}\n")
                        detalles.append("Subtotal: $${"%,.0f".format(subtotal)}\n")
                        detalles.append("────────────────\n")
                    }
                }

                detalles.append("\nTOTAL: $${"%,.0f".format(total)}")

                withContext(Dispatchers.Main) {
                    mostrarResumenCarrito(detalles.toString(), total)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CarritoActivity,
                        "Error al cargar carrito: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Muestra el resumen del carrito
     */
    private fun mostrarResumenCarrito(detalles: String, total: Double) {
        // Buscar el ScrollView para mostrar el resumen
        try {
            val scrollView = findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollViewCarrito)
            val linearLayout = scrollView.getChildAt(0) as LinearLayout

            // Crear TextView para mostrar el resumen
            val tvResumen = TextView(this).apply {
                text = detalles
                textSize = 16f
                setPadding(16, 16, 16, 16)
                setTextColor(resources.getColor(android.R.color.black, null))
            }

            // Limpiar contenido anterior y agregar nuevo
            linearLayout.removeAllViews()
            linearLayout.addView(tvResumen)

        } catch (e: Exception) {
            // Si no se puede mostrar en el layout, usar Toast
            Toast.makeText(this, "Carrito cargado. Total: $${"%,.0f".format(total)}", Toast.LENGTH_LONG).show()
        }

        // Actualizar botón
        btnFinalizar.isEnabled = true
        btnFinalizar.text = "Finalizar Compra - $${"%,.0f".format(total)}"
    }

    /**
     * Muestra mensaje de carrito vacío
     */
    private fun mostrarCarritoVacio() {
        Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
        btnFinalizar.isEnabled = false
        btnFinalizar.alpha = 0.5f
    }

    /**
     * Muestra diálogo de confirmación de compra
     */
    private fun mostrarDialogoConfirmacion() {
        val usuarioId = sessionManager.obtenerUsuarioId()

        AlertDialog.Builder(this)
            .setTitle("Confirmar Compra")
            .setMessage("¿Deseas finalizar la compra?")
            .setPositiveButton("Confirmar") { dialog, _ ->
                finalizarCompra(usuarioId)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Finaliza la compra
     */
    private fun finalizarCompra(usuarioId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Vaciar el carrito del usuario
                database.carritoDao().eliminarPorUsuario(usuarioId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CarritoActivity,
                        "¡Compra realizada con éxito!",
                        Toast.LENGTH_LONG
                    ).show()

                    // Volver a la pantalla anterior
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CarritoActivity,
                        "Error al finalizar compra: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}