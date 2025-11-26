package com.example.sistemgestiondeportiva.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.sistemgestiondeportiva.data.models.Usuario
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val ROL_ID_KEY = intPreferencesKey("rol_id")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_ID_KEY = intPreferencesKey("user_id")
    }

    private val gson = Gson()

    suspend fun saveAuthData(token: String, usuario: Usuario, rolID: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_DATA_KEY] = gson.toJson(usuario)
            preferences[ROL_ID_KEY] = rolID
            preferences[IS_LOGGED_IN_KEY] = true
            preferences[USER_ID_KEY] = usuario.usuaId  // ✅ CAMBIO AQUÍ: usuarioID -> usuaId
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }

    val rolID: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[ROL_ID_KEY]
    }

    val userData: Flow<Usuario?> = context.dataStore.data.map { preferences ->
        preferences[USER_DATA_KEY]?.let {
            try {
                gson.fromJson(it, Usuario::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    val userID: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    suspend fun getTokenSync(): String? {
        var token: String? = null
        context.dataStore.data.collect { preferences ->
            token = preferences[TOKEN_KEY]
        }
        return token
    }
}