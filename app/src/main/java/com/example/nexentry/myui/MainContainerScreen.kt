package com.example.nexentry.myui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexentry.R
import com.example.nexentry.ui.theme.NexEntryTheme

// NavItem model
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

// Using FamApp Style Palette
val MainFamDarkBg = Color(0xFF0A0A0A)
val MainFamGold = Color(0xFFE5C185)

// Requested Blue Vertical Gradient for Dark Background or Light context
val MainBlueGradientEffect = Brush.verticalGradient(
    listOf(
        Color(0xFF00A8FF), // Top
        Color(0xFF003366)  // Bottom
    )
)

@Composable
fun MainContainerScreen(isDarkMode: Boolean, onThemeChange: (Boolean) -> Unit) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val regularFont = FontFamily(Font(R.font.regular))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent, 
        bottomBar = {
            ModernFloatingBottomBar(
                selectedIndex = selectedItemIndex,
                onItemSelected = { selectedItemIndex = it },
                isDarkMode = isDarkMode,
                font = regularFont
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDarkMode) {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A1612), MainFamDarkBg),
                            startY = 0f,
                            endY = 1000f
                        )
                    } else {
                        // Light theme background from screenshot
                        Color(0xFFF5F7FF)
                    }
                )
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (selectedItemIndex) {
                0 -> GuardDashboardScreen(isDarkMode = isDarkMode)
                1 -> MyProfileScreen(isDarkMode = isDarkMode, onThemeChange = onThemeChange)
            }
        }
    }
}

@Composable
fun ModernFloatingBottomBar(selectedIndex: Int, onItemSelected: (Int) -> Unit, isDarkMode: Boolean, font: FontFamily) {
    val items = listOf(
        NavItem("HOME", Icons.Filled.Home, Icons.Filled.Home),
        NavItem("PROFILE", Icons.Default.Person, Icons.Default.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() 
            .padding(horizontal = 48.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = if (isDarkMode) 0.dp else 12.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color(0xFF00A8FF).copy(alpha = 0.2f)
                ),
            color = if (isDarkMode) Color.Black else Color.White,
            shape = RoundedCornerShape(32.dp),
            border = if (isDarkMode) BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) else null
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedItemIndex == index

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.9f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "scale"
                    )

                    val tint by animateColorAsState(
                        if (isDarkMode) {
                            if (isSelected) MainFamGold else Color(0xFF666666)
                        } else {
                            if (isSelected) Color(0xFF00A8FF) else Color(0xFF94A3B8)
                        },
                        label = "tint"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onItemSelected(index) }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isSelected && !isDarkMode) {
                                // Background pill for light mode selected icon
                                Box(
                                    modifier = Modifier
                                        .size(width = 48.dp, height = 32.dp)
                                        .background(Color(0xFF00A8FF).copy(alpha = 0.1f), CircleShape)
                                )
                            }
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = tint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = font,
                            color = tint,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun MultiMainDevicePreviewLight() {
    NexEntryTheme {
        MainContainerScreen(isDarkMode = false, onThemeChange = {})
    }
}
