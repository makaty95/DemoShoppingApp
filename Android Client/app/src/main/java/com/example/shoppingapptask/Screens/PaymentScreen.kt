package com.example.shoppingapptask.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.shoppingapptask.ViewModels.PaymentViewModel
import com.example.shoppingapptask.ViewModels.UserViewModel
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.view.CardMultilineWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun PaymentScreen(
    amount: Long,
    paymentViewModel: PaymentViewModel,
    userViewModel: UserViewModel,
    navController: NavController
) {

    val context = LocalContext.current

    // remember the card params
    var cardParams by remember { mutableStateOf<PaymentMethodCreateParams?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // for button animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, // shrink when the button is pressed
        label = "buttonScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            // go back button
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Gray, shape = RoundedCornerShape(12.dp))


            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "return back",
                    tint = Color.Red,
                    modifier = Modifier.scale(1.0F)
                )
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You are paying $${amount/100}",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))


            CardInputView { params ->
                cardParams = params
                Log.i("Update", "params updated")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                    if (cardParams == null) {
                        Toast.makeText(context, "Enter valid card details", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    CoroutineScope(Dispatchers.IO).launch{
                        delay(2000)

                        // Fetch clientSecret from backend using Retrofit
                        userViewModel.fetchClientSecret(amount) { clientSecret ->
                            if (clientSecret != null) {
                                val confirmParams = ConfirmPaymentIntentParams
                                    .createWithPaymentMethodCreateParams(cardParams!!, clientSecret)

                                Log.i("Pay", "confirming the payment params..")
                                paymentViewModel.confirmPayment(confirmParams)
                                Log.i("Pay", "finished confirming the payment params")
                                isLoading = false

                            } else {
                                navController.navigate("payment_failed") {
                                    popUpTo("payment_screen") { inclusive = true }
                                    launchSingleTop = true
                                }
                                paymentViewModel.resetStatus()
                                //Toast.makeText(context, "Failed to fetch payment info", Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }


                        }



                    }

                },
                enabled = !isLoading && cardParams != null,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp,
                    disabledElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLoading) Color.Gray else Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth().scale(scale),
                interactionSource = interactionSource
            ) {

                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Pay Now")
                }

            }
        }


    }



}


@Composable
fun CardInputView(
    onParamsChanged: (PaymentMethodCreateParams?) -> Unit
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        factory = { context ->
            CardMultilineWidget(context).apply {
                setCardValidCallback { isValid, invalidFields ->
                    if(isValid) {
                        onParamsChanged(paymentMethodCreateParams)
                    } else {
                        onParamsChanged(null)
                    }
                }
            }
        }
    )
}