package com.example.trashissue.api

import android.util.Log
import com.example.trashissue.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    val instance: UserApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("API_CALL", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder().apply {
                    Log.d("API_CALL", "URL: ${original.url}")
                    Log.d("API_CALL", "Method: ${original.method}")
                    Log.d("API_CALL", "Headers: ${original.headers}")
                    Log.d("API_CALL", "Body: ${original.body}")
                }.build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val baseUrl = BuildConfig.BASE_URL
        Log.d("API_CALL", "Base URL from BuildConfig: $baseUrl")

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofit.create(UserApiService::class.java)
    }
}