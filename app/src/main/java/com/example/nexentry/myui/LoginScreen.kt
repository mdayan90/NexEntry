package com.example.nexentry.myui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexentry.R
import com.example.nexentry.ui.theme.NexEntryTheme

// Palette using the app's primary blue
val AppPrimaryBlue = Color(0xFF003366)
val LoginBgStart = Color(0xFF003366)
val LoginBgEnd = Color(0xFF00a8ff)
val LoginWhite = Color(0xFFFFFFFF)
val LoginGrayText = Color(0xFF64748B)

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val normalFont = FontFamily.SansSerif

    // Hero Floating Animation
    val infiniteTransition = rememberInfiniteTransition(label = "heroFloating")
    val heroOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LoginBgStart, LoginBgEnd)))
    ) {
        // --- Abstract Decorative Blobs ---
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-50).dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                .blur(40.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(), // Only padding for status bar at top
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- TOP SECTION (App Name + Hero Image) ---
            // Shifting content slightly more centered
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NexEntry",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(R.drawable.guard_image_4),
                    contentDescription = "Guard Hero",
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth(0.5f)
                        .graphicsLayer { translationY = heroOffset }
                )
            }

            // --- BOTTOM SECTION (Login Card attached to bottom) ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp),
                color = LoginWhite,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        // This ensures content is above the bottom navigation bar while white bg goes all the way down
                        .navigationBarsPadding()
                        .padding(horizontal = 32.dp, vertical = 40.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = LoginBgStart,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "Secure access to society gate management system.",
                        fontSize = 14.sp,
                        color = LoginGrayText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Custom Inputs
                    LoginInputFieldModern(
                        value = username,
                        onValueChange = { username = it },
                        label = "User ID",
                        placeholder = "Staff ID / Username",
                        icon = Icons.Default.PersonOutline
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LoginInputFieldModern(
                        value = password,
                        onValueChange = { password = it },
                        label = "Security PIN",
                        placeholder = "Enter security PIN",
                        icon = Icons.Default.LockOpen,
                        isPassword = true,
                        passwordVisible = visibility,
                        onVisibilityToggle = { visibility = !visibility }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = true,
                                onCheckedChange = {},
                                colors = CheckboxDefaults.colors(checkedColor = AppPrimaryBlue)
                            )
                            Text("Remember me", fontSize = 12.sp, color = LoginGrayText)
                        }
                        Text(
                            "Forgot PIN?",
                            fontSize = 12.sp,
                            color = AppPrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Large Primary Button
                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                onLoginSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryBlue)
                    ) {
                        Text("Sign in", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        "NEXENTRY TERMINAL ACCESS v2.0",
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LoginInputFieldModern(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = LoginGrayText,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            leadingIcon = { Icon(icon, null, tint = Color.LightGray) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { onVisibilityToggle?.invoke() }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppPrimaryBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Preview(name = "Small Phone", device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Large Phone", device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(name = "Foldable", device = "spec:width=673dp,height=841dp,dpi=420")
@Composable
fun MultiLoginDevicePreview() {
    NexEntryTheme {
        LoginScreen(onLoginSuccess = {})
    }
}