package com.example.shoppingapptask.Models

data class Cart (
    val id: Int,
    var products: MutableList<CartProduct>,
    var total: Double,
    var discountedTotal: Double,
    val userId: Int,
    var totalProducts: Int,
    var totalQuantity: Int
)