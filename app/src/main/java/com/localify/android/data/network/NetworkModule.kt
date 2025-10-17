package com.localify.android.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    private const val BASE_URL = "https://staging.localify.org/"
    
    private val loggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        Log.d("HTTP", "Request: ${request.method} ${request.url}")
        Log.d("HTTP", "Headers: ${request.headers}")
        
        try {
            val response = chain.proceed(request)
            Log.d("HTTP", "Response: ${response.code} ${response.message}")
            Log.d("HTTP", "Response Headers: ${response.headers}")
            response
        } catch (e: Exception) {
            Log.e("HTTP", "Network error: ${e.javaClass.simpleName}: ${e.message}")
            Log.e("HTTP", "Full stack trace:", e)
            throw e
        }
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
