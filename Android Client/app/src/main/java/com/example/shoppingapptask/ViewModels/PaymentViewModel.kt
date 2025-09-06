package com.example.shoppingapptask.ViewModels

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult

class PaymentViewModel : ViewModel() {

    val paymentStatus = mutableStateOf<PaymentStatus?>(null)

    private var paymentLauncher: PaymentLauncher? = null

    fun initPaymentLauncher(
        activity: ComponentActivity,
        publishableKey: String
    ) {
        if (paymentLauncher != null) return

        PaymentConfiguration.init(activity.applicationContext, publishableKey)

        paymentLauncher = PaymentLauncher.create(
            activity,
            PaymentConfiguration.getInstance(activity).publishableKey,
            null
        ) { result ->
            paymentStatus.value = when (result) {
                is PaymentResult.Completed -> PaymentStatus.SUCCESS
                is PaymentResult.Canceled -> PaymentStatus.CANCELED
                is PaymentResult.Failed -> PaymentStatus.FAILED
            }
        }
    }

    fun confirmPayment(confirmParams: ConfirmPaymentIntentParams) {
        paymentLauncher?.confirm(confirmParams)
    }

    fun resetStatus() {
        paymentStatus.value = null
    }
}

enum class PaymentStatus {
    SUCCESS, FAILED, CANCELED
}