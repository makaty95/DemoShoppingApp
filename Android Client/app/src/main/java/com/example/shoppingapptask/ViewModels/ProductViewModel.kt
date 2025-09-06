package com.example.shoppingapptask.ViewModels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapptask.Models.Login.LoginRequest
import com.example.shoppingapptask.Models.Login.LoginResponse
import com.example.shoppingapptask.Models.Product
import com.example.shoppingapptask.Models.Register.RegisterRequest
import com.example.shoppingapptask.Models.Register.RegisterResponse
import com.example.shoppingapptask.Models.UserProfile
import com.example.shoppingapptask.ProductAPI
import com.example.shoppingapptask.ProductRepository
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ProductViewModel : ViewModel() {

    // the API URL
    private val BASE_URL = "https://dummyjson.com/"

    var products by mutableStateOf<List<Product>>(emptyList())
        private set

    private val api: ProductAPI
    private val repository: ProductRepository

    var isLoading by mutableStateOf(true)
        private set


    init {

        // setting up the connection with the API
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ProductAPI::class.java)
        repository = ProductRepository(api)
    }

    fun loadProducts(forceRefresh: Boolean = false) {
        // If we already have the products and not forcing refresh, don't fetch again
        // just as the same reloading strategy of the profile
        if (products.isNotEmpty() && !forceRefresh) return

        viewModelScope.launch {
            try {
                products = getAllProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getAllProducts(): List<Product> {
        try {
            isLoading = true
            val fetchedProducts = repository.getProducts().products
            return fetchedProducts
        } catch (e: Exception) {
            Log.i("info","Error fetching products: ${e.message}")
        } finally {
            isLoading = false
            Log.i("info", "Products loaded")
        }
        return listOf<Product>()
    }


}

