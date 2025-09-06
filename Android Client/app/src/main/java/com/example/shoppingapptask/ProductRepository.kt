package com.example.shoppingapptask

import android.util.Log
import com.example.shoppingapptask.Models.Cart
import com.example.shoppingapptask.Models.CreateCartRequest
import com.example.shoppingapptask.Models.Login.LoginRequest
import com.example.shoppingapptask.Models.Login.LoginResponse
import com.example.shoppingapptask.Models.ProductRecord
import com.example.shoppingapptask.Models.ProductsResponse
import com.example.shoppingapptask.Models.Register.RegisterRequest
import com.example.shoppingapptask.Models.Register.RegisterResponse
import com.example.shoppingapptask.Models.UpdateCartRequest
import com.example.shoppingapptask.Models.UserProfile

class ProductRepository(private val api: ProductAPI) {
    suspend fun getProducts(): ProductsResponse = api.getProducts()

    suspend fun registerUser(request: RegisterRequest): RegisterResponse {
        return api.registerUser(request)
    }

    suspend fun loginUser(request: LoginRequest): LoginResponse {
        return api.loginUser(request)
    }

    suspend fun getUserProfile(access_token: String): UserProfile {
        return api.getUserProfile("Bearer ${access_token}")
    }

    // the testing api is not storing the carts, so just make a local check
    var cartCreated: Boolean = false

    fun isUserHavingOnlineCart(userId: Int): Boolean {
        return cartCreated;
    }

    suspend fun updateUserCart(userId: Int, products: List<ProductRecord>, oldCart: Cart?): Cart {

        val newCart = api.updateUserCart(request = UpdateCartRequest(false, products), userId)

        // adding this because the test API does not save the online carts
        // in real world scenarios this won't happen

        newCart.totalQuantity += oldCart?.totalQuantity ?: 0
        newCart.total += oldCart?.total ?: 0.0
        //newCart.totalProducts += oldCart?.totalProducts ?: 0
        newCart.discountedTotal += oldCart?.discountedTotal ?: 0.0

        for (oldProduct in oldCart?.products ?: emptyList()) {

            var added = false

            // check if the product instance exist already (just add it's quantity)
            for(newProduct in newCart.products) {
                if(oldProduct.id == newProduct.id) {
                    newProduct.quantity++
                    added = true
                    break;
                }
            }

            // add new instance
            if(!added) {
                newCart.products += oldProduct
                newCart.totalProducts++;
            }
        }

        return newCart
    }

    suspend fun createUserCart(products: List<ProductRecord>, userId: Int): Cart {
        cartCreated = true
        return api.createUserCart(CreateCartRequest(userId, products))

    }

    suspend fun deleteCart(cartId: Int) {
        //api.deleteCart(cartId)
        // the API won't recognize the cart id
        // because there is no cart actually stored
        // we will just delete the cart locally
    }
}