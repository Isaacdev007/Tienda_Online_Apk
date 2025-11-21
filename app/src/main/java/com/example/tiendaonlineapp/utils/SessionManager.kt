package com.example.tiendaonlineapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "TiendaOnlineSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_SESSION_START_TIME = "session_start_time"

        // Tiempo de expiración: 30 minutos en milisegundos
        // Cambia este valor según necesites
        private const val SESSION_TIMEOUT = 30 * 60 * 1000L
    }

    /**
     * Guarda la sesión del usuario con timestamp actual
     */
    fun guardarSesion(userId: Int, userName: String) {
        val currentTime = System.currentTimeMillis()
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_SESSION_START_TIME, currentTime)
            apply()
        }
    }

    /**
     * Actualiza el timestamp de la sesión (renueva el tiempo)
     */
    fun renovarSesion() {
        if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            prefs.edit().apply {
                putLong(KEY_SESSION_START_TIME, System.currentTimeMillis())
                apply()
            }
        }
    }

    /**
     * Obtiene el ID del usuario logueado
     */
    fun obtenerUsuarioId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    /**
     * Obtiene el nombre del usuario logueado
     */
    fun obtenerNombreUsuario(): String {
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    /**
     * Verifica si hay un usuario logueado Y si la sesión NO ha expirado
     */
    fun estaLogueado(): Boolean {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

        if (!isLoggedIn) {
            return false
        }

        // Verificar si la sesión ha expirado
        if (sesionExpirada()) {
            cerrarSesion()
            return false
        }

        // Si la sesión está activa, renovar el tiempo
        renovarSesion()
        return true
    }

    /**
     * Verifica si la sesión ha expirado
     */
    private fun sesionExpirada(): Boolean {
        val sessionStartTime = prefs.getLong(KEY_SESSION_START_TIME, 0)
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - sessionStartTime

        return elapsedTime > SESSION_TIMEOUT
    }

    /**
     * Obtiene el tiempo restante de sesión en minutos
     */
    fun tiempoRestanteSesion(): Long {
        val sessionStartTime = prefs.getLong(KEY_SESSION_START_TIME, 0)
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - sessionStartTime
        val remainingTime = SESSION_TIMEOUT - elapsedTime

        return if (remainingTime > 0) {
            remainingTime / (60 * 1000) // convertir a minutos
        } else {
            0
        }
    }

    /**
     * Cierra la sesión
     */
    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}