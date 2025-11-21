package com.example.tiendaonlineapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tiendaonlineapp.database.AppDatabase
import com.example.tiendaonlineapp.utils.SessionManager
import com.example.tiendaonlineapp.utils.ValidationUtils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegistro: TextView

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar base de datos y sesión
        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        // Verificar si ya hay sesión activa
        if (sessionManager.estaLogueado()) {
            irAProductos()
            return
        }

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
                iniciarSesion(correo, contrasena)
            }
        }

        // Texto "¿No tienes cuenta? Regístrate" → ir a RegistroActivity
        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Inicia sesión verificando credenciales en la base de datos
     */
    private fun iniciarSesion(correo: String, contrasena: String) {
        // Deshabilitar botón mientras se procesa
        btnLogin.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Buscar usuario en la base de datos
                val usuario = database.usuarioDao().buscarPorCorreoYContrasena(correo, contrasena)

                withContext(Dispatchers.Main) {
                    if (usuario != null) {
                        // Credenciales correctas
                        sessionManager.guardarSesion(usuario.id, usuario.nombre)

                        Toast.makeText(
                            this@LoginActivity,
                            "¡Bienvenido ${usuario.nombre}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        irAProductos()
                    } else {
                        // Credenciales incorrectas
                        Toast.makeText(
                            this@LoginActivity,
                            "Correo o contraseña incorrectos",
                            Toast.LENGTH_LONG
                        ).show()
                        btnLogin.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error al iniciar sesión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    btnLogin.isEnabled = true
                }
            }
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

        return true
    }

    /**
     * Navega a la pantalla de productos
     */
    private fun irAProductos() {
        val intent = Intent(this, ProductosActivity::class.java)
        startActivity(intent)
        finish()
    }
}