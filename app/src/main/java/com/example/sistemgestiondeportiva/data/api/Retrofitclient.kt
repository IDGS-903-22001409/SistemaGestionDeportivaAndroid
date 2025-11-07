package com.example.sistemgestiondeportiva.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // IMPORTANTE: Cambia esta URL por la URL de tu backend
    // Si usas el emulador de Android: "http://10.0.2.2:5022/"
    // Si usas un dispositivo físico: "http://TU_IP_LOCAL:5022/" (ejemplo: "http://192.168.1.100:5022/")
    // Si tu backend está en HTTPS: "https://tudominio.com/"
    private const val BASE_URL = "http://192.168.137.1:5022/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// Clase para manejar respuestas de la API
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}