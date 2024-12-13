package com.example.trashissue.models

data class RegisterResponse(
    val error: Boolean,
    val message: String,
    val userId: String
)
