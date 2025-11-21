package com.example.tiendaonlineapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tiendaonlineapp.database.AppDatabase
import com.example.tiendaonlineapp.database.Usuario
import com.example.tiendaonlineapp.utils.ValidationUtils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etConfirmar: TextInputEditText
    private lateinit var btnRegistrarse: Button
    private lateinit var tvVolverLogin: TextView

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar base de datos
        database = AppDatabase.getDatabase(this)

        // Referencias a elementos
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        etConfirmar = findViewById(R.id.etConfirmar)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        tvVolverLogin = findViewById(R.id.tvVolverLogin)

        // Cuando el usuario toca "Registrarse"
        btnRegistrarse.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString()
            val confirmar = etConfirmar.text.toString()

            if (validarCampos(nombre, correo, contrasena, confirmar)) {
                registrarUsuario(nombre, correo, contrasena)
            }
        }

        // Cuando toca el texto "¿Ya tienes cuenta?" → volver al Login
        tvVolverLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Registra un nuevo usuario en la base de datos
     */
    private fun registrarUsuario(nombre: String, correo: String, contrasena: String) {
        // Deshabilitar botón mientras se procesa
        btnRegistrarse.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Verificar si el correo ya existe
                val usuarioExistente = database.usuarioDao().buscarPorCorreo(correo)

                withContext(Dispatchers.Main) {
                    if (usuarioExistente != null) {
                        // El correo ya está registrado
                        Toast.makeText(
                            this@RegistroActivity,
                            "Este correo ya está registrado",
                            Toast.LENGTH_LONG
                        ).show()
                        btnRegistrarse.isEnabled = true
                    } else {
                        // Crear nuevo usuario
                        val nuevoUsuario = Usuario(
                            nombre = nombre,
                            correo = correo,
                            contrasena = contrasena
                        )

                        // Insertar en la base de datos
                        database.usuarioDao().insertar(nuevoUsuario)

                        // Mostrar mensaje de éxito
                        Toast.makeText(
                            this@RegistroActivity,
                            "¡Registro exitoso! Ahora puedes iniciar sesión",
                            Toast.LENGTH_LONG
                        ).show()

                        // Ir al Login
                        val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegistroActivity,
                        "Error al registrar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    btnRegistrarse.isEnabled = true
                }
            }
        }
    }

    /**
     * Valida todos los campos del formulario de registro
     */
    private fun validarCampos(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmar: String
    ): Boolean {

        // Limpiar errores previos
        etNombre.error = null
        etCorreo.error = null
        etContrasena.error = null
        etConfirmar.error = null

        // Validar nombre vacío
        if (!ValidationUtils.isNotEmpty(nombre)) {
            etNombre.error = "El nombre no puede estar vacío"
            etNombre.requestFocus()
            return false
        }

        // Validar longitud del nombre
        if (!ValidationUtils.isValidName(nombre)) {
            etNombre.error = "El nombre debe tener al menos 3 caracteres"
            etNombre.requestFocus()
            return false
        }

        // Validar correo vacío
        if (!ValidationUtils.isNotEmpty(correo)) {
            etCorreo.error = "El correo no puede estar vacío"
            etCorreo.requestFocus()
            return false
        }

        // Validar formato de correo
        if (!ValidationUtils.isValidEmail(correo)) {
            etCorreo.error = "Ingresa un correo válido (ejemplo@correo.com)"
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

        // Validar confirmación vacía
        if (!ValidationUtils.isNotEmpty(confirmar)) {
            etConfirmar.error = "Debes confirmar la contraseña"
            etConfirmar.requestFocus()
            return false
        }

        // Validar coincidencia de contraseñas
        if (!ValidationUtils.passwordsMatch(contrasena, confirmar)) {
            etConfirmar.error = "Las contraseñas no coinciden"
            etConfirmar.requestFocus()
            return false
        }

        return true
    }
}