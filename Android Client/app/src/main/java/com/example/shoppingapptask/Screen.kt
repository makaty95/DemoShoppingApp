package com.example.shoppingapptask

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {

    object Cart : Screen("cart", "Cart", Icons.Default.ShoppingCart)
    object Products : Screen("products", "Products", Icons.Default.Home)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}