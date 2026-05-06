package com.example.nexentry.myui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import com.example.nexentry.ui.theme.DarkBackground
import com.example.nexentry.ui.theme.NexEntryTheme
import com.example.nexentry.ui.theme.PrimaryBlue
import com.example.nexentry.ui.theme.SecondaryBlue

// NavItem model
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

// Using FamApp Style Palette
val MainFamDarkBg = DarkBackground
val MainFamGold = Color(0xFFE5C185)

@Composable
fun MainContainerScreen(isDarkMode: Boolean, onThemeChange: (Boolean) -> Unit) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val regularFont = FontFamily(Font(R.font.regular))

    // Define the background brush based on the theme
    val containerBackgroundBrush = if (isDarkMode) {
        Brush.linearGradient(
            colors = listOf(Color(0xFF0A0A0A), Color(0xFF0A0A0A))
        )
    } else {
        // Light theme background matches Dashboard
        Brush.verticalGradient(
            colors = listOf(PrimaryBlue.copy(alpha = 0.1f), Color.White),
            startY = 0f,
            endY = 1000f
        )
    }

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
                .background(containerBackgroundBrush)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (selectedItemIndex) {
                0 -> GuardDashboardScreen(isDarkMode = isDarkMode)
                1 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Logs Screen Content", color = if (isDarkMode) Color.White else Color.Black)
                }
                2 -> MyProfileScreen(isDarkMode = isDarkMode, onThemeChange = onThemeChange)
            }
        }
    }
}

@Composable
fun ModernFloatingBottomBar(selectedIndex: Int, onItemSelected: (Int) -> Unit, isDarkMode: Boolean, font: FontFamily) {
    val items = listOf(
        NavItem("HOME", Icons.Filled.Home, Icons.Filled.Home),
        NavItem("LOGS", Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Filled.Assignment),
        NavItem("PROFILE", Icons.Default.Person, Icons.Default.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() 
            .padding(horizontal = 32.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            // Bottom bar: BLACK in dark mode, BLUE GRADIENT in light mode
            color = Color.Transparent,
            shape = RoundedCornerShape(35.dp),
            shadowElevation = if (isDarkMode) 0.dp else 15.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isDarkMode) Brush.linearGradient(listOf(Color.Black, Color.Black)) 
                        else Brush.verticalGradient(listOf(SecondaryBlue, PrimaryBlue))
                    )
                    .border(
                        width = 1.dp, 
                        color = if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.2f), 
                        shape = RoundedCornerShape(35.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = selectedIndex == index

                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.85f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "scale"
                        )

                        val tint by animateColorAsState(
                            if (isDarkMode) {
                                if (isSelected) MainFamGold else Color(0xFF666666)
                            } else {
                                // Light Mode: Selected White, Unselected Black
                                if (isSelected) Color.White else Color.Black.copy(alpha = 0.6f)
                            },
                            label = "tint"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .scale(scale)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onItemSelected(index) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(35.dp)
                                        .background(
                                            if(isDarkMode) MainFamGold.copy(alpha = 0.1f) 
                                            else Color.White.copy(alpha = 0.15f), 
                                            CircleShape
                                        )
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = tint,
                                    modifier = Modifier.size(26.dp)
                                )
                                if (isSelected) {
                                    Text(
                                        text = item.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = font,
                                        color = tint
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun MultiMainDevicePreview() {
    NexEntryTheme {
        MainContainerScreen(isDarkMode = true, onThemeChange = {})
    }
}
