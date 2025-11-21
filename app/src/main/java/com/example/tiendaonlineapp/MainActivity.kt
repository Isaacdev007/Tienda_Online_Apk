package com.example.tiendaonlineapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendaonlineapp.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Simular splash screen por 2 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            verificarSesionYRedirigir()
        }, 2000) // 2 segundos de espera
    }

    /**
     * Verifica si hay sesión activa y redirige apropiadamente
     */
    private fun verificarSesionYRedirigir() {
        val intent = if (sessionManager.estaLogueado()) {
            // Si hay sesión activa → ir directo a Productos
            Intent(this, ProductosActivity::class.java)
        } else {
            // Si no hay sesión → ir a Login
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish() // Cerrar MainActivity para que no se pueda volver con botón atrás
    }
}