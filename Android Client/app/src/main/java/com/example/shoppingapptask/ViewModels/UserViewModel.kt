package com.example.shoppingapptask.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapptask.Models.Cart
import com.example.shoppingapptask.Models.CartProduct
import com.example.shoppingapptask.Models.Login.LoginRequest
import com.example.shoppingapptask.Models.Login.LoginResponse
import com.example.shoppingapptask.Payment.PaymentIntentRequest
import com.example.shoppingapptask.Models.ProductRecord
import com.example.shoppingapptask.Models.Register.RegisterRequest
import com.example.shoppingapptask.Models.Register.RegisterResponse
import com.example.shoppingapptask.Models.UserProfile
import com.example.shoppingapptask.Payment.PaymentClient
import com.example.shoppingapptask.Payment.PaymentIntentResponse
import com.example.shoppingapptask.ProductAPI
import com.example.shoppingapptask.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class UserViewModel : ViewModel() {

    private val BASE_URL = "https://dummyjson.com/"
    private var ACCESS_TOKEN = ""

    private val api: ProductAPI
    private val repository: ProductRepository

    var isLoading by mutableStateOf(true)




    private val _userCart = MutableStateFlow<Cart?>(null)
    val userCart = _userCart.asStateFlow() // online cart
    var inAppProductsCart by mutableStateOf<List<CartProduct>>(emptyList())
    // : List<CartProduct> = mutableListOf() // local cart in memory

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile


    init {

        // setting up the connection with the API
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ProductAPI::class.java)
        repository = ProductRepository(api)
    }

    fun isHavingOnlineCart(): Boolean {
        return repository.isUserHavingOnlineCart(profile.value?.id ?: -1);
    }

    fun decreaseQuantity(productId: Int) {
        _userCart.update { cart ->
            cart?.let {
                val product = cart.products.find { it.id == productId }
                if(product != null) {
                    if(product.quantity <= 1) {
                        cart
                    } else {

                        val updatedProducts = it.products.map { p ->
                            if (p.id == productId) p.copy(quantity = p.quantity - 1) else p
                        }.toMutableList()

                        Log.i("Decrease quantity", "quantity decreased")
                        val discountedPrice = product.price * (1.0 - (product.discountPercentage/100.0))
                        cart.copy(
                            products = updatedProducts,
                            totalProducts = it.totalProducts,
                            total = it.total - product.price,
                            totalQuantity = it.totalQuantity - 1,
                            discountedTotal = (it.discountedTotal - discountedPrice )
                        )
                    }

                } else {
                    cart
                }
            }
        }
    }


    fun increaseQuantity(productId: Int) {
        _userCart.update { cart ->
            cart?.let {
                val product = cart.products.find { it.id == productId }
                if(product != null) {
                    if(product.quantity >= 5) {
                        cart
                    } else {

                        val updatedProducts = it.products.map { p ->
                            if (p.id == productId) p.copy(quantity = p.quantity + 1) else p
                        }.toMutableList()

                        Log.i("Increase quantity", "quantity increased")
                        val discountedPrice = product.price * (1.0 - (product.discountPercentage/100.0))
                        cart.copy(
                            products = updatedProducts,
                            totalProducts = it.totalProducts,
                            total = it.total + product.price,
                            totalQuantity = it.totalQuantity + 1,
                            discountedTotal = (it.discountedTotal + discountedPrice )
                        )
                    }

                } else {
                    cart
                }
            }
        }
    }

    fun removeProductById(productId: Int) {

        _userCart.update { cart ->
            cart?.let {
                val product = cart.products.find { it.id == productId }

                if(product != null) {
                    val discountedPrice = product.price * (1.0 - (product.discountPercentage/100.0))
                    cart.copy(

                        //TODO: recap on this
                        products = cart.products.filter { p -> p.id != productId }.toMutableList(),
                        totalProducts = it.totalProducts - 1,
                        total = it.total - (product.price * product.quantity),
                        totalQuantity = it.totalQuantity - product.quantity,
                        discountedTotal = it.discountedTotal - (discountedPrice * product.quantity)

                    )
                } else {
                    cart
                }

            }

        }

    }

    suspend fun addLocalProducts() {
        val newProducts = inAppProductsCart.map { ProductRecord(it.id, it.quantity) }

        // update the cart using the API
        val oldCart = _userCart.value

        val updatedCart = repository.updateUserCart(profile.value?.id ?: -1, newProducts, oldCart)

        _userCart.value = updatedCart

        // remove products in the local cart
        inAppProductsCart = mutableListOf()

    }

    suspend fun loadCart() {

        viewModelScope.launch {
            if(isHavingOnlineCart()) {
                if(inAppProductsCart.isNotEmpty()) {
                    addLocalProducts()
                }
            } else {
                if(inAppProductsCart.isNotEmpty()) {
                    createUserCart()
                }
            }
        }

    }



    fun addToInAppProductsCart(product: CartProduct) {

        inAppProductsCart.forEach { it ->
            if(it.id == product.id) {
                it.quantity += product.quantity;
                return;
            }
        }

        inAppProductsCart += product
    }

    suspend fun createUserCart() {
        val productsToAdd = inAppProductsCart.map { ProductRecord(it.id, it.quantity) }

        // clear cart
        inAppProductsCart = mutableListOf()

        val createdCart = repository.createUserCart(productsToAdd, profile.value?.id ?: -1)

        _userCart.value = createdCart

        inAppProductsCart = emptyList()

    }

    suspend fun deleteUserCart() {
        val currentCartId = _userCart.value?.id ?: -1
        Log.i("Deleting Cart", "Cart id is $currentCartId")

        repository.deleteCart(currentCartId)

        // the API does not really delete the cart we have to delete it manually
        _userCart.value = null
    }

    fun loadProfile(forceRefresh: Boolean = false) {
        // If we already have the profile and not forcing refresh, don't fetch again
        if (_profile.value != null && !forceRefresh) return

        viewModelScope.launch {
            try {
                val fetchedProfile = getUserProfile()
                _profile.value = fetchedProfile
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        _profile.value = null
    }

    suspend fun registerUser(userName: String, email: String, password: String, avatar: String): RegisterResponse? {
        try {
            isLoading = true
            val response = repository.registerUser(RegisterRequest(userName, email, password, avatar))
            Log.i("Register", "Register complete")
            return response
        } catch (e: Exception) {
            Log.i("Register", "Register Failed")
        } finally {
            isLoading = false;
        }
        return null;
    }

    suspend fun loginUser(userName: String, password: String): LoginResponse? {

        try {
            isLoading = true
            val response = repository.loginUser(LoginRequest(userName, password))
            ACCESS_TOKEN = response.accessToken
            Log.i("Login", "Login complete")
            return response
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("Login", "Login Failed")
        } finally {
            isLoading = false;
        }
        return null;
    }

    private suspend fun getUserProfile(): UserProfile? {
        return try {
            isLoading = true;
            repository.getUserProfile(ACCESS_TOKEN)
        } catch (e: Exception) {
            Log.i("info", "Getting user profile failed")
            e.printStackTrace()
            return null;
        } finally {
            isLoading = false;
        }

    }



    // fetch the client secret from the backend which is used to communicate with stripe
    fun fetchClientSecret(amount: Long, onResult: (String?) -> Unit) {

        val request = PaymentIntentRequest(amount = amount)

        PaymentClient.instance.createPaymentIntent(request).enqueue(object: Callback<PaymentIntentResponse>{
            override fun onResponse(
                call: Call<PaymentIntentResponse?>,
                response: Response<PaymentIntentResponse?>
            ) {
                if(response.isSuccessful) {
                    onResult(response.body()?.clientSecret)
                } else {
                    Log.i("Failure", "not successful")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<PaymentIntentResponse>, t: Throwable) {
                Log.e("Failure", "Network call failed", t)
                onResult(null)
            }
        })
    }

}