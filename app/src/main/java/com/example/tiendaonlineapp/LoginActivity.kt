package com.example.tiendaonlineapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tiendaonlineapp.utils.ValidationUtils
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegistro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Referencias a los elementos del XML
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegistro = findViewById(R.id.tvRegistro)

        // Botón "Iniciar sesión" con validación
        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString()

            if (validarCampos(correo, contrasena)) {
                // Si las validaciones pasan, ir a ProductosActivity
                Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProductosActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Texto "¿No tienes cuenta? Regístrate" → ir a RegistroActivity
        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Valida los campos del formulario
     */
    private fun validarCampos(correo: String, contrasena: String): Boolean {
        // Limpiar errores previos
        etCorreo.error = null
        etContrasena.error = null

        // Validar correo vacío
        if (!ValidationUtils.isNotEmpty(correo)) {
            etCorreo.error = "El correo no puede estar vacío"
            etCorreo.requestFocus()
            return false
        }

        // Validar formato de correo
        if (!ValidationUtils.isValidEmail(correo)) {
            etCorreo.error = "Ingresa un correo válido"
            etCorreo.requestFocus()
            return false
        }

        // Validar contraseña vacía
        if (!ValidationUtils.isNotEmpty(contrasena)) {
            etContrasena.error = "La contraseña no puede estar vacía"
            etContrasena.requestFocus()
            return false
        }

        // Validar longitud mínima de contraseña
        if (!ValidationUtils.isValidPassword(contrasena, 6)) {
            etContrasena.error = "La contraseña debe tener al menos 6 caracteres"
            etContrasena.requestFocus()
            return false
        }

        // Todas las validaciones pasaron
        return true
    }
}