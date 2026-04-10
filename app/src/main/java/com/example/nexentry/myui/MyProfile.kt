package com.example.nexentry.myui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexentry.R
import com.example.nexentry.ui.theme.NexEntryTheme

// FamApp Style Palette
val FamDarkBg = Color(0xFF0A0A0A)
val FamSurface = Color(0xFF161616)
val FamGold = Color(0xFFE5C185)
val FamGreen = Color(0xFF00DC82)
val FamDivider = Color(0xFF262626)


@Composable
fun MyProfileScreen(isDarkMode: Boolean, onThemeChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.verticalGradient(colors = listOf(Color(0xFF1A1612), FamDarkBg))
                } else {
                    Brush.verticalGradient(colors = listOf(Color(0xFFE0F2FE), Color.White))
                }
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Icon(Icons.Default.Close, null, tint = if (isDarkMode) Color.White else Color.Black)
//                Text(
//                    text = "@hashmiayan90",
//                    color = if (isDarkMode) Color.White else Color.Black,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Icon(Icons.Outlined.Edit, null, tint = if (isDarkMode) Color.White else Color.Black)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Avatar
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .border(2.dp, FamGold.copy(alpha = 0.5f), CircleShape)
                            .padding(4.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.guard_image_4),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Name
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "MD AYAN HASHMI",
                        color = if (isDarkMode) FamGold else Color(0xFF1E293B),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // --- THEME SELECTION SECTION ---
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Theme Selection",
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ThemeCard(
                            title = "Dark",
                            icon = Icons.Default.DarkMode,
                            isSelected = isDarkMode,
                            onClick = { onThemeChange(true) },
                            modifier = Modifier.weight(1f),
                            isDarkMode = isDarkMode
                        )
                        ThemeCard(
                            title = "Light",
                            icon = Icons.Default.LightMode,
                            isSelected = !isDarkMode,
                            onClick = { onThemeChange(false) },
                            modifier = Modifier.weight(1f),
                            isDarkMode = isDarkMode
                        )
                    }
                }

                // Other list items...
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        FamListItem(Icons.Default.Security, "Security PIN", isDarkMode)
                        FamListItem(Icons.Default.Notifications, "Alert Settings", isDarkMode)
                        FamListItem(Icons.Default.SupportAgent, "Admin Support", isDarkMode)
                        FamListItem(Icons.Default.Info, "About NexEntry", isDarkMode)
                    }
                }

                // Logout Button
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(56.dp)
                            .clickable { },
                        shape = CircleShape,
                        color = if (isDarkMode) Color(0xFF1C1C1C) else Color(0xFFF1F5F9)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Log Out",
                                color = if (isDarkMode) FamGold else Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    isDarkMode: Boolean
) {
    Surface(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) FamGold.copy(alpha = 0.1f) else (if (isDarkMode) FamSurface else Color(0xFFF1F5F9)),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) FamGold else Color.Gray.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) FamGold else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                color = if (isSelected) FamGold else Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FamListItem(icon: ImageVector, title: String, isDarkMode: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = FamGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = if (isDarkMode) Color.White else Color.Black, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
    }
}

@Preview(name = "Small Phone", device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Large Phone", device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(name = "Foldable", device = "spec:width=673dp,height=841dp,dpi=420")
@Preview(name = "Ultra Small Phone", device = "spec:width=320dp,height=533dp,dpi=160")
@Composable
fun MultiMyprofilePreview() {
    NexEntryTheme {
        MyProfileScreen(isDarkMode = true, onThemeChange = {})
    }
}
