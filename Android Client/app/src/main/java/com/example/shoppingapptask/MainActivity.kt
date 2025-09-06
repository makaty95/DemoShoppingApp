package com.example.shoppingapptask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.shoppingapptask.ViewModels.PaymentViewModel
import com.example.shoppingapptask.ui.theme.ShoppingAppTaskTheme

class MainActivity : ComponentActivity() {
    private val paymentViewModel: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentViewModel.initPaymentLauncher(
            this,
            "pk_test_51S3fhF4RRCLmQ7Xx8Csd8qd9uLnxue9mact1bcqT4B8nYDrK7SMEa9Hqp0X1rdgGZQExqSxmDVGk7HwxgNdbkfND00TLHgtfhU"
        )

        setContent {
            ShoppingAppTaskTheme {
                AppNavigation(paymentViewModel = paymentViewModel)
            }
        }
    }
}
