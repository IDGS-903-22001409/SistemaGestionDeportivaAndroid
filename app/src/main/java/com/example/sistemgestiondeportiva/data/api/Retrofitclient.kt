package com.example.sistemgestiondeportiva.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ⬇️ CAMBIAR PUERTO A 5022 (HTTP)
    private const val BASE_URL = "http://10.86.168.120:5289/"

    // ⬇️ AGREGAR LOGGING COMPLETO
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Muestra todo: headers, body, etc.
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request()
            android.util.Log.d("RETROFIT", "=== REQUEST ===")
            android.util.Log.d("RETROFIT", "URL: ${request.url}")
            android.util.Log.d("RETROFIT", "Method: ${request.method}")
            android.util.Log.d("RETROFIT", "Headers: ${request.headers}")

            val response = chain.proceed(request)

            android.util.Log.d("RETROFIT", "=== RESPONSE ===")
            android.util.Log.d("RETROFIT", "Code: ${response.code}")
            android.util.Log.d("RETROFIT", "Message: ${response.message}")

            response
        }
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