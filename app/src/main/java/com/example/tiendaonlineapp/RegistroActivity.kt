package com.example.tiendaonlineapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val tvVolverLogin = findViewById<TextView>(R.id.tvVolverLogin)

        // Cuando el usuario toca "Registrarse" → volver al Login
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // cerrar esta pantalla
        }

        // Cuando toca el texto "¿Ya tienes cuenta?" → volver al Login
        tvVolverLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}