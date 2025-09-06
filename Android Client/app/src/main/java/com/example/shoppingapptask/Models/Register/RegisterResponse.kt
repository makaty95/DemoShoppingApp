package com.example.shoppingapptask.Models.Register

data class RegisterResponse(
    val email: String,
    val password: Int,
    val name: String,
    val avatar: String,
    val role: String,
    val id: Int
)