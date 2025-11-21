package com.example.tiendaonlineapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ConfirmacionCompraActivity : AppCompatActivity() {

    private lateinit var containerResumen: LinearLayout
    private lateinit var tvTotalPagado: TextView
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion_compra)

        // Referencias
        containerResumen = findViewById(R.id.containerResumen)
        tvTotalPagado = findViewById(R.id.tvTotalPagado)
        btnVolver = findViewById(R.id.btnVolver)

        // Obtener datos del intent
        val productosNombres = intent.getStringArrayListExtra("productos_nombres") ?: arrayListOf()
        val productosCantidades = intent.getIntegerArrayListExtra("productos_cantidades") ?: arrayListOf()
        val productosPrecios = intent.getStringArrayListExtra("productos_precios") ?: arrayListOf()
        val total = intent.getDoubleExtra("total", 0.0)

        // Mostrar resumen
        mostrarResumen(productosNombres, productosCantidades, productosPrecios, total)

        // Botón volver
        btnVolver.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    /**
     * Muestra el resumen de la compra
     */
    private fun mostrarResumen(
        nombres: ArrayList<String>,
        cantidades: ArrayList<Int>,
        precios: ArrayList<String>,
        total: Double
    ) {
        containerResumen.removeAllViews()

        for (i in nombres.indices) {
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Nombre y cantidad
            val tvNombreCantidad = TextView(this).apply {
                text = "${nombres[i]} x${cantidades[i]}"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            // Precio
            val tvPrecio = TextView(this).apply {
                text = precios[i]
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }

            itemLayout.addView(tvNombreCantidad)
            itemLayout.addView(tvPrecio)
            containerResumen.addView(itemLayout)
        }

        // Actualizar total
        tvTotalPagado.text = "$${"%,.0f".format(total)}"
    }

    override fun onBackPressed() {
        // Al presionar atrás, ir a productos
        val intent = Intent(this, ProductosActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}