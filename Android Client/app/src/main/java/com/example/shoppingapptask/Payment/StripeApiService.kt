package com.example.shoppingapptask.Payment

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface StripeApiService {
    @POST("create-payment-intent")
    fun createPaymentIntent(@Body request: PaymentIntentRequest): Call<PaymentIntentResponse>

}