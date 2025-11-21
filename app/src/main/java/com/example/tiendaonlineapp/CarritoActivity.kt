package com.example.tiendaonlineapp

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.tiendaonlineapp.database.AppDatabase
import com.example.tiendaonlineapp.database.CarritoItem
import com.example.tiendaonlineapp.database.Producto
import com.example.tiendaonlineapp.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarritoActivity : AppCompatActivity() {

    private lateinit var btnFinalizar: Button
    private lateinit var tvTotal: TextView
    private lateinit var containerProductos: LinearLayout

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    private var itemsCarrito = listOf<CarritoItem>()
    private var totalGeneral = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // Inicializar
        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        // Referencias
        btnFinalizar = findViewById(R.id.btnFinalizar)
        tvTotal = findViewById(R.id.tvTotal)
        containerProductos = findViewById(R.id.containerProductos)

        // Cargar productos del carrito
        cargarCarrito()

        // Botón finalizar compra
        btnFinalizar.setOnClickListener {
            if (itemsCarrito.isNotEmpty()) {
                mostrarDialogoConfirmacion()
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
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
                itemsCarrito = database.carritoDao().obtenerPorUsuario(usuarioId)

                withContext(Dispatchers.Main) {
                    if (itemsCarrito.isEmpty()) {
                        mostrarCarritoVacio()
                    } else {
                        mostrarProductosCarrito()
                    }
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
     * Muestra los productos del carrito con controles
     */
    private fun mostrarProductosCarrito() {
        containerProductos.removeAllViews()
        totalGeneral = 0.0

        CoroutineScope(Dispatchers.IO).launch {
            for (item in itemsCarrito) {
                val producto = database.productoDao().obtenerPorId(item.producto_id)

                if (producto != null) {
                    val subtotal = producto.precio * item.cantidad
                    totalGeneral += subtotal

                    withContext(Dispatchers.Main) {
                        val cardView = crearCardProducto(item, producto, subtotal)
                        containerProductos.addView(cardView)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                actualizarTotal()
            }
        }
    }

    /**
     * Crea una card visual para cada producto del carrito
     */
    private fun crearCardProducto(item: CarritoItem, producto: Producto, subtotal: Double): CardView {
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            radius = 16f
            cardElevation = 4f
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorCardBackground))
        }

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // Nombre del producto
        val tvNombre = TextView(this).apply {
            text = producto.nombre
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary))
        }
        mainLayout.addView(tvNombre)

        // Precio unitario
        val tvPrecio = TextView(this).apply {
            text = "Precio: ${producto.precioFormateado()}"
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
            setPadding(0, 4, 0, 0)
        }
        mainLayout.addView(tvPrecio)

        // Controles de cantidad
        val cantidadLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 12, 0, 12)
        }

        // Botón disminuir (-)
        val btnDisminuir = Button(this).apply {
            text = "-"
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(80, 80).apply {
                marginEnd = 8
            }
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary))
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
        }

        // TextView cantidad
        val tvCantidad = TextView(this).apply {
            text = item.cantidad.toString()
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                marginEnd = 8
            }
        }

        // Botón aumentar (+)
        val btnAumentar = Button(this).apply {
            text = "+"
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(80, 80)
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
        }

        cantidadLayout.addView(btnDisminuir)
        cantidadLayout.addView(tvCantidad)
        cantidadLayout.addView(btnAumentar)
        mainLayout.addView(cantidadLayout)

        // Subtotal
        val tvSubtotal = TextView(this).apply {
            text = "Subtotal: $${"%,.0f".format(subtotal)}"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            setPadding(0, 8, 0, 8)
        }
        mainLayout.addView(tvSubtotal)

        // Botón eliminar
        val btnEliminar = Button(this).apply {
            text = "Eliminar del carrito"
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorError))
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
        }
        mainLayout.addView(btnEliminar)

        // Eventos de botones
        btnAumentar.setOnClickListener {
            aumentarCantidad(item.id)
        }

        btnDisminuir.setOnClickListener {
            disminuirCantidad(item.id)
        }

        btnEliminar.setOnClickListener {
            eliminarProducto(item.id, producto.nombre)
        }

        cardView.addView(mainLayout)
        return cardView
    }

    /**
     * Aumenta la cantidad de un producto
     */
    private fun aumentarCantidad(itemId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.carritoDao().aumentarCantidad(itemId)

                withContext(Dispatchers.Main) {
                    cargarCarrito() // Recargar vista
                    Toast.makeText(
                        this@CarritoActivity,
                        "Cantidad actualizada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CarritoActivity,
                        "Error al actualizar cantidad",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Disminuye la cantidad de un producto
     */
    private fun disminuirCantidad(itemId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cantidadActual = database.carritoDao().obtenerCantidad(itemId)

                if (cantidadActual != null && cantidadActual > 1) {
                    database.carritoDao().disminuirCantidad(itemId)

                    withContext(Dispatchers.Main) {
                        cargarCarrito()
                        Toast.makeText(
                            this@CarritoActivity,
                            "Cantidad actualizada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CarritoActivity,
                            "Usa el botón 'Eliminar' para quitar el producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CarritoActivity,
                        "Error al actualizar cantidad",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Elimina un producto del carrito
     */
    private fun eliminarProducto(itemId: Int, nombreProducto: String) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Deseas eliminar '$nombreProducto' del carrito?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.carritoDao().eliminarItem(itemId)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@CarritoActivity,
                                "Producto eliminado del carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                            cargarCarrito()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@CarritoActivity,
                                "Error al eliminar: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Actualiza el total en la UI
     */
    private fun actualizarTotal() {
        tvTotal.text = "$${"%,.0f".format(totalGeneral)}"

        if (totalGeneral > 0) {
            btnFinalizar.isEnabled = true
            btnFinalizar.alpha = 1.0f
        } else {
            btnFinalizar.isEnabled = false
            btnFinalizar.alpha = 0.5f
        }
    }

    /**
     * Muestra mensaje de carrito vacío
     */
    private fun mostrarCarritoVacio() {
        containerProductos.removeAllViews()

        val tvVacio = TextView(this).apply {
            text = "Tu carrito está vacío"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(32, 64, 32, 64)
            setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
        }

        containerProductos.addView(tvVacio)

        tvTotal.text = "$0"
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
            .setMessage("¿Deseas finalizar la compra?\n\nTotal: $${"%,.0f".format(totalGeneral)}")
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