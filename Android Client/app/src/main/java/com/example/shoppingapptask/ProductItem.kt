package com.example.shoppingapptask

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.shoppingapptask.Models.Product


@Composable
fun ProductItem(product: Product, onClick: (Product) -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp) // fixed height to make all cards equal
            .padding(2.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { onClick(product) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxHeight() // make Column take full card height
        ) {

            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )


            // Top content
            Text(product.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))

            // Filler space to push bottom row down
            Spacer(modifier = Modifier.weight(1f))

            // Bottom row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //TODO: try to make a random rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFD700).copy(alpha = 0.7f)
                    )
                    Text("${product.rating} ", style = MaterialTheme.typography.titleMedium)
                    //Text("(${product.rating.count}) ", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }

                Text(
                    "$${product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(100, 100, 100)
                )
            }
        }
    }
}


@Composable
@Preview(name = "ProductItem", showBackground = true, showSystemUi = true)
fun ProductItemPreview() {
    Surface {
        ProductItem(
            product = Product(
                1,
                "Silicon power 256GB SSD 3D NAND A55 ALC Cache Performance Boost SATA III 2.5",
                "this is the description of the item",
                "Hammers",
                190.22,
                1.5,
                4.6,
                stock = 120,
                listOf(),
                "In Stock",
                listOf(""),
                ""

            ),
            onClick = {}
        )
    }
}