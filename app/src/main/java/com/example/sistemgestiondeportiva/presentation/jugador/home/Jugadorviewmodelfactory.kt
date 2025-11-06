package com.example.sistemgestiondeportiva.presentation.jugador.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class JugadorViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JugadorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JugadorViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}