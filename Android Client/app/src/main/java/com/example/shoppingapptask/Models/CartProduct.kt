package com.example.shoppingapptask.Models

data class CartProduct (
    val id: Int,
    val title: String,
    val price: Double,
    var quantity: Int,
    val total: Double,
    val discountPercentage: Double,
    val discountTotal: Double,
    val thumbnail: String
)