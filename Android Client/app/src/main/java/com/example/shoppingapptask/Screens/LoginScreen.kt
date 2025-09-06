package com.example.shoppingapptask.Screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LoginScreen(
    onLoginClick: suspend (String, String) -> Int,
    onSignUpClick: () -> Unit
) {
    var userName by remember { mutableStateOf("henryh") }
    var password by remember { mutableStateOf("henryhpass") }
    var login_code by remember { mutableStateOf(1) }
    var passwordVisible by remember { mutableStateOf(false) }

    // for button animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val login_btn_scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, // shrink when the button is pressed
        label = "buttonScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {


        // form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Login here",
                color = Color.Red,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome back you've been missed!",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(64.dp))

            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Gray,
                    errorTextColor= Color.Red
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                    val iconTint = if (passwordVisible) Color(0xFF2ECC71) else Color.Gray


                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = "Toggle password visibility",
                            tint = iconTint
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Gray,
                    errorTextColor= Color.Red
                ),
                modifier = Modifier.fillMaxWidth()
            )



            if(login_code == 2) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Wrong username or password", color = Color.Red)
            } else if(login_code == 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("No internet connection", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(24.dp))

            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    scope.launch {
                        Log.i("info", "you clicked the login button")
                        login_code = onLoginClick(userName, password)

                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .scale(login_btn_scale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPressed) Color(0xFFB71C1C) else Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.Gray
                ),
                interactionSource = interactionSource
            ) {
                Text("Login", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // register link
            TextButton(onClick = onSignUpClick) {
                Text("Don't have an account? Sign up")
            }
        }
    }
}

@Composable
@Preview(name = "Login screen", showBackground = true, showSystemUi = true)
fun LoginScreenPreview() {

    Surface {
        LoginScreen({email, password -> 1}, {})
    }

}