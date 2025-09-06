package com.example.shoppingapptask.Payment

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// this client uses retrofit to communicate with the backend over the specified path
object PaymentClient {

    // this is a spring backend on my PC - i just opened the port from the firewall so
    // i can connect to it :) (in real world this is supposed to be our production backend which have the secret key)
    private const val BASE_URL = "http://192.168.1.3:8080/"

    val instance: StripeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StripeApiService::class.java)
    }
}