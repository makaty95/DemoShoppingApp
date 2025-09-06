package com.example.shoppingapptask.Screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.shoppingapptask.Models.Product
import com.example.shoppingapptask.ProductImageSlider
import kotlin.math.max
import kotlin.math.min

@SuppressLint("DefaultLocale")
@Composable
fun ProductDetailScreen(product: Product, onAddToCart: (Product, Int) -> Boolean) {

    var itemCount by remember { mutableStateOf(0) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()


    val priceAfterDiscount = String.format("%.2f", product.price*(1 - (product.discountPercentage/100)))

    val context = LocalContext.current

    // for button animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, // shrink when the button is pressed
        label = "buttonScale"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        // title
        Text(
            product.title,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // product image
        ProductImageSlider(product.images)

        Spacer(modifier = Modifier.height(8.dp))

        // category
        Text(product.category, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(4.dp))

        // description
        Text(product.description, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(8.dp))

        // availability
        Text(
            text = product.availabilityStatus,
            color = Color(120, 120, 120)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            // price
            Text("$${priceAfterDiscount}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.width(8.dp))

            //discount
            Row(verticalAlignment = Alignment.CenterVertically
                , horizontalArrangement = Arrangement.End) {
                Icon(
                    imageVector = Icons.Default.Discount,
                    contentDescription = "Discount",
                    tint = Color.Red
                )
                Text("${product.discountPercentage}%", style = MaterialTheme.typography.titleMedium)
            }




            Spacer(modifier = Modifier.weight(1f)) // Push rating to the end

            //TODO: try add a random rating :)
            // rating
            Row(verticalAlignment = Alignment.CenterVertically
                , horizontalArrangement = Arrangement.End) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = Color(0xFFFFD700).copy(alpha = 0.7f)
                )
                Text("${product.rating} ", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                //Text("(${product.rating.count}) ", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            }
        }

        // the original price
        if(product.discountPercentage > 0) {
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.bodySmall
                    .copy(textDecoration = TextDecoration.LineThrough)
            )
        }



        Spacer(modifier = Modifier.height(8.dp))


        Row (verticalAlignment = Alignment.CenterVertically) {


            IconButton(
                onClick = {
                    //TODO: decrease item count logic
                    itemCount = max(0, --itemCount)
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.RemoveCircle,
                    contentDescription = "decrease item count",
                    tint = Color.LightGray,
                    modifier = Modifier.scale(1.5F)

                )
            }

            Spacer(modifier = Modifier.width(4.dp))
            Text("${itemCount}")
            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = {
                    //TODO: increase item count logic
                    itemCount = min(++itemCount, product.stock);
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "increase item count",
                    tint = Color.LightGray,
                    modifier = Modifier.scale(1.5F)
                )
            }






        }

        // Tags
        FlowRow (

            ) {
            product.tags.forEach { it ->
                Card(
                    modifier = Modifier.wrapContentSize().padding(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6))

                ) {
                    Text(
                        it,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )

                }

            }
        }





        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                //TODO
                val success = onAddToCart(product, itemCount)

                if(success) {
                    Toast.makeText(context, "Item added to cart", Toast.LENGTH_LONG).show()
                }

            },
            modifier = Modifier.fillMaxWidth()
                .scale(scale),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) Color(0xFFB71C1C) else Color.Red,
                contentColor = Color.White,
                disabledContainerColor = Color.DarkGray,
                disabledContentColor = Color.Gray
            ),
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Shopping cart",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add to cart")
        }


    }
}


@Composable
@Preview(name = "Full Product", showBackground = true, showSystemUi = true)
fun ProductDetailScreenPreview() {
    Surface {
        ProductDetailScreen(
            Product(
                1,
                "Silicon power 256GB SSD 3D NAND A55 ALC Cache Performance Boost SATA III 2.5",
                "this is the description of the item",
                "Hammers",
                190.22,
                1.5,
                4.6,
                stock = 21,
                listOf("accessories", "electronics", "electronics", "electronics", "electronics", "elegant", "hello"),
                "In Stock",
                listOf(""),
                ""

            ),
            onAddToCart = {p, q -> false}
        )
    }

}