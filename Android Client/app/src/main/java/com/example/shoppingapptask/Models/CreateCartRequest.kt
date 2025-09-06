package com.example.shoppingapptask.Models

data class CreateCartRequest(
    val userId: Int,
    val products: List<ProductRecord>
)