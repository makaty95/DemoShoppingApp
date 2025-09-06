package com.example.shoppingapptask.Models.Register

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val avatar: String
)