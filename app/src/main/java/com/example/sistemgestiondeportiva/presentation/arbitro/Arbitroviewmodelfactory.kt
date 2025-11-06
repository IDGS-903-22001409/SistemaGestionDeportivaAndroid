package com.example.sistemgestiondeportiva.presentation.arbitro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ArbitroViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArbitroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArbitroViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}