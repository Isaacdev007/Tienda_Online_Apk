package com.example.tiendaonlineapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.tiendaonlineapp.utils.ValidationUtils
import com.google.android.material.textfield.TextInputEditText

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etConfirmar: TextInputEditText
    private lateinit var btnRegistrarse: Button
    private lateinit var tvVolverLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

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
                // Si las validaciones pasan, mostrar mensaje y volver al login
                Toast.makeText(this, "¡Registro exitoso! Ahora puedes iniciar sesión", Toast.LENGTH_LONG).show()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
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

        // Todas las validaciones pasaron
        return true
    }
}