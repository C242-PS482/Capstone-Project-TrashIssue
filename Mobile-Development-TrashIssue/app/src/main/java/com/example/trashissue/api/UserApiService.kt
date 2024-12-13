package com.example.trashissue.api

import com.example.trashissue.models.EventsResponse
import com.example.trashissue.models.LoginRequest
import com.example.trashissue.models.LoginResponse
import com.example.trashissue.models.PredictResponse
import com.example.trashissue.models.RegisterRequest
import com.example.trashissue.models.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApiService {
    @POST("register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    // Menambahkan @GET untuk mengambil daftar acara
    @GET("events") // Ganti dengan endpoint yang sesuai di API Anda
    fun getEvents(): Call<EventsResponse>

        @Multipart
        @POST("predict") // Tetap 'predict' karena BASE_URL sudah termasuk '/api/'
        suspend fun predictTrash(
            @Part image: MultipartBody.Part
        ): Response<PredictResponse>
}