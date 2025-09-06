package com.example.shoppingapptask

import com.example.shoppingapptask.Models.Cart
import com.example.shoppingapptask.Models.CreateCartRequest
import com.example.shoppingapptask.Models.Login.LoginRequest
import com.example.shoppingapptask.Models.Login.LoginResponse
import com.example.shoppingapptask.Models.ProductsResponse
import com.example.shoppingapptask.Models.Register.RegisterRequest
import com.example.shoppingapptask.Models.Register.RegisterResponse
import com.example.shoppingapptask.Models.UpdateCartRequest
import com.example.shoppingapptask.Models.UserProfile
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductAPI {
    @GET("products?limit=20") // limit only to 20 product (for now)
    suspend fun getProducts(): ProductsResponse

    @POST("users")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    @GET("auth/me")
    suspend fun getUserProfile(@Header("Authorization") token: String): UserProfile

    @PUT("carts/{userId}")
    suspend fun updateUserCart(@Body request: UpdateCartRequest, @Path("userId") userId: Int ): Cart

    @POST("/carts/add")
    suspend fun createUserCart(@Body request: CreateCartRequest): Cart

    @DELETE("/carts/{cartId}")
    suspend fun deleteCart(@Path("cartId") cartId: Int)
}