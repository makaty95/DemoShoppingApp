package com.example.shoppingapptask.Models

data class ProductsResponse (
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
)