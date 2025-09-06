package com.example.shoppingapptask


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoppingapptask.Screens.CartScreen
import com.example.shoppingapptask.Screens.LoginScreen
import com.example.shoppingapptask.Screens.ProductDetailScreen
import com.example.shoppingapptask.Screens.ProductListScreen
import com.example.shoppingapptask.Screens.ProfileScreen
import com.example.shoppingapptask.Screens.RegisterScreen
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.shoppingapptask.Models.CartProduct
import com.example.shoppingapptask.ViewModels.PaymentStatus
import com.example.shoppingapptask.ViewModels.PaymentViewModel
import com.example.shoppingapptask.Screens.BillingScreen
import com.example.shoppingapptask.Screens.PaymentFailScreen
import com.example.shoppingapptask.Screens.PaymentScreen
import com.example.shoppingapptask.Screens.PaymentSuccessScreen
import com.example.shoppingapptask.ViewModels.ProductViewModel
import com.example.shoppingapptask.ViewModels.UserViewModel

@Composable
fun AppNavigation(
    paymentViewModel: PaymentViewModel = viewModel()
) {


    val navController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current

    Log.i("info", "we are working")


    // Observe payment status & navigate
    val paymentStatus = paymentViewModel.paymentStatus.value
    LaunchedEffect(paymentStatus) {
        when (paymentStatus) {
            PaymentStatus.SUCCESS -> {


                navController.navigate("payment_success") {
                    popUpTo("payment_screen") { inclusive = true }
                    launchSingleTop = true
                }
                paymentViewModel.resetStatus()

                // remove the cart
                userViewModel.deleteUserCart()

            }

            PaymentStatus.CANCELED -> {
                navController.navigate("payment_failed")
                paymentViewModel.resetStatus()
            }

            PaymentStatus.FAILED -> {
                navController.navigate("payment_failed")
                paymentViewModel.resetStatus()
            }

            else -> Unit
        }
    }


    Scaffold(
        bottomBar = {
            // Hide bottom bar on login/register screens
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute in listOf("products", "cart", "profile")) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->

        // definition of each screen as a separate navigation destination
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {


            // login screen - 1st screen to be seen
            composable(route = "login") {
                LoginScreen(
                    onLoginClick = { username, password -> Int

                        Log.i("Login", "logging in..")

                        if (!context.isNetworkAvailable()) {
                            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                            return@LoginScreen 0
                        }

                        val response = userViewModel.loginUser(username, password)
                        if(response != null) {
                            // navigate to the products list
                            navController.navigate("products") {
                                // remove all the previous navigation so the user can't go back once he is authenticated
                                popUpTo("login") { inclusive = true }
                            }
                            userViewModel.loadProfile()
                            return@LoginScreen 1;
                        }

                        Log.i("Login", "Login lambda done")
                        return@LoginScreen 2
                    },

                    onSignUpClick = {
                        navController.navigate("register")
                    }
                )
            }


            // register screen
            composable(route = "register") {
                //TODO: modify and complete this


                val scope = rememberCoroutineScope()
                RegisterScreen(
                    onRegisterClick = { userName, email, password, avatar ->
                        scope.launch {
                            try {
                                val response = userViewModel.registerUser(userName, email, password, avatar)
                                Log.i("Register", "Reg. id: ${response?.id}")

                            } catch (e: Exception) {
                                Log.e("Register", "Failed: ${e.message}")
                            }

                            // just go to the products screen (for now)
                            navController.navigate("login")

                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }


            // main product list screen
            composable("products") {

                // load the products first
                productViewModel.loadProducts()

                ProductListScreen(
                    productViewModel = productViewModel,
                    onProductClick = { product ->
                        navController.navigate("product_detail/${product.id}")
                    }
                )
            }


            composable("cart") {
               CartScreen(
                   userViewModel,
                   navController,
                   onCheckOut = { navController, amount ->
                       navController.navigate("billing/${amount}")
                   }
               )
            }

            // user profile screen
            composable("profile") {
                ProfileScreen(userViewModel = userViewModel)
            }

            // the screen for each product (when clicked)
            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->

                Log.i("info", "inside lambda function of product_detail/{productId}")

                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                Log.i("info", "product id is $productId")

                val product = productViewModel.products.find { it.id == productId }
                Log.i("info", "product processed, going to detailed screen..")
                product?.let {
                    ProductDetailScreen(
                        product = it,
                        onAddToCart = { product, quantity ->
                            if(quantity > 0 && quantity <= product.stock) { // if valid quantity
                                val cartProduct = CartProduct(
                                    product.id,
                                    product.title,
                                    product.price,
                                    quantity,
                                    quantity * product.price,
                                    product.discountPercentage,
                                    product.discountPercentage * product.price * quantity,
                                    product.thumbnail
                                )

                                Log.i("Add to cart", "Product added to cart")

                                userViewModel.addToInAppProductsCart(cartProduct)
                                return@ProductDetailScreen true
                            }

                            Log.i("Add to cart", "Test Failed when adding to cart")

                            return@ProductDetailScreen false
                        }
                    )
                }
            }





            composable(
                route = "billing/{amount}",
                arguments = listOf(navArgument("amount") {type = NavType.LongType})
            ) { backStackEntry ->

                val totalAmount = backStackEntry.arguments?.getLong("amount") ?: 0

                BillingScreen(
                    userViewModel = userViewModel,
                    onContinuePayment = {
                        navController.navigate("payment/${totalAmount}")
                    },
                    navController

                )
            }


            composable(
                route = "payment/{amount}",
                arguments = listOf(navArgument("amount") {type = NavType.LongType})
            ) { backStackEntry ->
                val totalAmount = backStackEntry.arguments?.getLong("amount") ?: 0
                PaymentScreen(
                    amount = totalAmount,
                    //paymentLauncher = paymentLauncher,
                    paymentViewModel = paymentViewModel,
                    userViewModel = userViewModel,
                    navController
                )
            }


            composable(
                route = "payment_success"
            ) { backStackEntry ->
                PaymentSuccessScreen(navController)
            }

            composable(
                route = "payment_failed"
            ) { backStackEntry ->
                PaymentFailScreen(navController)
            }

        }

    }

}


fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}