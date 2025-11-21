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
        }, 2000)
    }

    /**
     * Verifica si hay sesión activa y válida
     * estaLogueado() ahora valida automáticamente el tiempo
     */
    private fun verificarSesionYRedirigir() {
        val intent = if (sessionManager.estaLogueado()) {
            // Sesión válida y dentro del tiempo permitido
            Intent(this, ProductosActivity::class.java)
        } else {
            // Sin sesión o sesión expirada
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}