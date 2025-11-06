package com.example.sistemgestiondeportiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sistemgestiondeportiva.inicioSesion.PantallaLogin
import com.example.sistemgestiondeportiva.escaneo.EscanerScreen
import com.example.sistemgestiondeportiva.jugador.MainJugadorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") { PantallaLogin(navController) }
                    composable("jugador") { MainJugadorScreen() }
                    composable("escaner") { EscanerScreen() }
                }
            }
        }
    }
}
