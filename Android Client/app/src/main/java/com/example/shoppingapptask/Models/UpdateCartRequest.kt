package com.example.shoppingapptask.Models

data class UpdateCartRequest(
    val merge: Boolean,
    val products: List<ProductRecord>,
)