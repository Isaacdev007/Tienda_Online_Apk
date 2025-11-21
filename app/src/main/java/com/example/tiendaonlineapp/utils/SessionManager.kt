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
    }

    /**
     * Guarda la sesión del usuario
     */
    fun guardarSesion(userId: Int, userName: String) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
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
     * Verifica si hay un usuario logueado
     */
    fun estaLogueado(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Cierra la sesión
     */
    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}