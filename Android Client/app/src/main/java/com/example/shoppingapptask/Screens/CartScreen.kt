package com.example.shoppingapptask.Screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.shoppingapptask.Models.CartProduct
import com.example.shoppingapptask.ViewModels.UserViewModel
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.shoppingapptask.R
import kotlinx.coroutines.launch


@SuppressLint("DefaultLocale")
@Composable
fun CartScreen(
    userViewModel: UserViewModel,
    navController: NavController,
    onCheckOut: (NavController, Long) -> Unit
) {


    LaunchedEffect(userViewModel.userCart) {
        userViewModel.loadCart()
    }


    // if still no cart created, it will be empty list
    val userCart by userViewModel.userCart.collectAsState()
    val cartProducts = userCart?.products ?: emptyList()
    val totalAmount = userCart?.total ?: 0.0
    val totalProducts = userCart?.totalProducts ?: 0
    val totalAfterDiscount = userCart?.discountedTotal ?: 0.0

    val coroutineScope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // for button animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, // shrink when the button is pressed
        label = "buttonScale"
    )

    if(cartProducts.isNotEmpty()) {

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

                Spacer(modifier = Modifier.weight(1f))

                // delete all button
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            userViewModel.deleteUserCart()
                        }

                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "return back",
                        tint = Color.Red,
                        modifier = Modifier.scale(1.0F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // products column
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp) // fixed height container
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                    // add scroll for many items.
                ) {
                    cartProducts.forEach {
                            product -> CartItem(product, userViewModel)

                    }
                }
            }




            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

                Column {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format("%.2f", totalAfterDiscount),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("-$%.2f", (totalAmount - totalAfterDiscount)),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }



                // checkout button
                Button(
                    onClick = {

                        //TODO: checkout implementation
                        val totalAmount = (totalAfterDiscount * 100.0).toLong()
                        onCheckOut(navController, totalAmount)
                    },
                    modifier = Modifier.size(180.dp, 60.dp).scale(scale),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp,
                        disabledElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        disabledContainerColor = Color.DarkGray,
                        disabledContentColor = Color.Gray
                    ),
                    interactionSource = interactionSource
                ) {

                    Text("Check out")

                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.size(30.dp, 30.dp),


                        ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "${totalProducts}",
                                color = Color.Red
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowRight,
                        contentDescription = "Continue checkout",
                        tint = Color.White
                    )
                }

            }
            Spacer(modifier = Modifier.height(16.dp))

        }

    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.empty_cart),
                    contentDescription = "Empty Cart Logo",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                // Text under the logo
                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }





}


@SuppressLint("DefaultLocale")
@Composable
fun CartItem(product: CartProduct, userViewModel: UserViewModel) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Product Image
            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Product title
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Prices and discount
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Original Price (strikethrough)
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Discounted Price
                    val discountedPrice = (product.price * (1 - product.discountPercentage / 100))
                    Text(
                        text = "$${String.format("%.2f", discountedPrice)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF388E3C) // Green for discounted price
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Discount percentage
                    Text(
                        text = "-${product.discountPercentage.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            userViewModel.decreaseQuantity(product.id)
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFF1F1F1)),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Quantity",
                            tint = Color.Gray
                        )
                    }

                    Text(
                        text = "${product.quantity}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = {
                            userViewModel.increaseQuantity(product.id)
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFF1F1F1)),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Increase Quantity",
                            tint = Color.Gray
                        )
                    }
                }
            }

            // Remove Product Button
            IconButton(
                onClick = {
                    userViewModel.removeProductById(product.id)
                },
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Bottom),
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFFEAEA))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Product",
                    tint = Color.Red,

                )
            }
        }
    }



}


//@Composable
//@Preview(name = "Cart", showBackground = true, showSystemUi = true)
//fun Cart() {
//    Surface {
//        CartScreen(listOf(
//            CartProduct(1, "sledge hammer", 122.29, 1, 122.29, 12.0, 12.0, ""),
//            CartProduct(2, "backPack", 19.99, 2, 2 * 19.99, 1.5, 261.0, ""),
//            CartProduct(3, "umbrella", 12.31, 3, 3 * 12.31, 22.55, 29.1, ""),
//        ))
//    }
//}