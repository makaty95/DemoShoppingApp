package com.example.shoppingapptask.Models


data class Product(
    // these are the fields we want for now
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val tags: List<String>,
    val availabilityStatus: String,
    val images: List<String>,
    val thumbnail: String
)