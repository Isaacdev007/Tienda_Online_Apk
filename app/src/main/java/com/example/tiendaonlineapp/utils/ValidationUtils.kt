package com.example.tiendaonlineapp.utils

import android.util.Patterns

object ValidationUtils {

    /**
     * Valida que el campo no esté vacío
     */
    fun isNotEmpty(text: String): Boolean {
        return text.trim().isNotEmpty()
    }

    /**
     * Valida formato de email
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida longitud mínima de contraseña
     */
    fun isValidPassword(password: String, minLength: Int = 6): Boolean {
        return password.length >= minLength
    }

    /**
     * Valida que las contraseñas coincidan
     */
    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    /**
     * Valida nombre (mínimo 3 caracteres)
     */
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 3
    }
}