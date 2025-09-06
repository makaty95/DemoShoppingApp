package com.example.shoppingapptask.Models

data class UserProfile(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val email: String,
    val image: String,
    val username: String,
    val password: String,
    val role: String,
    val phone:String,
    val address: UserAddress

)