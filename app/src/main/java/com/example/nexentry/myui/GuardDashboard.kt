package com.example.nexentry.myui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexentry.R
import com.example.nexentry.ui.theme.NexEntryTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// FamApp Style Palette
val GuardFamDarkBg = Color(0xFF0A0A0A)
val GuardFamSurface = Color(0xFF161616)
val GuardFamGold = Color(0xFFE5C185)
val GuardFamGreen = Color(0xFF00DC82)
val GuardFamDivider = Color(0xFF262626)

val BlueVerticalGradient = Brush.verticalGradient(
    listOf(
        Color(0xFF00A8FF), // Top
        Color(0xFF003366)  // Bottom
    )
)

enum class DashboardPage {
    HOME, CHECK_IN, PENDING_DETAIL
}

@Composable
fun GuardDashboardScreen(isDarkMode: Boolean) {
    var currentPage by remember { mutableStateOf(DashboardPage.HOME) }
    var selectedVisitorName by remember { mutableStateOf("") }
    var selectedFlat by remember { mutableStateOf("") }

    AnimatedContent(targetState = currentPage, label = "pageTransition") { targetPage ->
        when (targetPage) {
            DashboardPage.HOME -> DashboardHome(
                isDarkMode = isDarkMode,
                onCheckInClick = { currentPage = DashboardPage.CHECK_IN },
                onPendingClick = { name, flat ->
                    selectedVisitorName = name
                    selectedFlat = flat
                    currentPage = DashboardPage.PENDING_DETAIL
                }
            )
            DashboardPage.CHECK_IN -> VisitorCheckInScreen(isDarkMode = isDarkMode, onBack = { currentPage = DashboardPage.HOME })
            DashboardPage.PENDING_DETAIL -> ActivityDetailScreen(
                isDarkMode = isDarkMode,
                name = selectedVisitorName,
                flat = selectedFlat,
                onBack = { currentPage = DashboardPage.HOME }
            )
        }
    }
}

@Composable
fun DashboardHome(isDarkMode: Boolean, onCheckInClick: () -> Unit, onPendingClick: (String, String) -> Unit) {
    var isOnline by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.verticalGradient(colors = listOf(Color(0xFF1A1612), GuardFamDarkBg))
                } else {
                    BlueVerticalGradient
                }
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Name Centered at top
            Spacer(modifier = Modifier.statusBarsPadding().height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "NexEntry",
                    color = if (isDarkMode) GuardFamGold else Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    HeaderSectionFam(isOnline = isOnline, onToggle = { isOnline = it }, isDarkMode = isDarkMode)
                }

                item {
                    BentoGridFam(onCheckInClick = onCheckInClick, isDarkMode = isDarkMode)
                }

                item {
                    Text(
                        text = "Pending Activity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDarkMode) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                    )
                }

                items(listOf(
                    "Ayan Hashmi" to "B-401",
                    "Sammer Khan" to "B-402",
                    "Arsalan Khan" to "B-403"
                )) { (name, flat) ->
                    PendingRequestItemFam(
                        name = name,
                        flat = flat,
                        time = "10:25 AM",
                        isDarkMode = isDarkMode,
                        onClick = { onPendingClick(name, flat) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VisitorCheckInScreen(isDarkMode: Boolean, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var flat by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        capturedImage = bitmap
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.verticalGradient(colors = listOf(Color(0xFF1A1612), GuardFamDarkBg))
                } else {
                    BlueVerticalGradient
                }
            )
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.background(if (isDarkMode) GuardFamSurface else Color(0xFFF1F5F9), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = if (isDarkMode) Color.White else Color.Black)
                }
                Spacer(Modifier.width(16.dp))
                Text("New Check-in", fontSize = 24.sp, fontWeight = FontWeight.Black, color = if (isDarkMode) Color.White else Color.Black)
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                color = if (isDarkMode) GuardFamSurface else Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Gray.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Visitor Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) GuardFamGold else Color(0xFF1E293B))
                        Box(
                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(20.dp)).background(if (isDarkMode) GuardFamDarkBg else Color(0xFFF1F5F9)).border(1.dp, if (isDarkMode) GuardFamGold.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                .clickable {
                                    if (cameraPermissionState.status.isGranted) cameraLauncher.launch()
                                    else cameraPermissionState.launchPermissionRequest()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (capturedImage != null) Image(bitmap = capturedImage!!.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            else Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo", tint = if (isDarkMode) GuardFamGold else Color.Black)
                        }
                    }

                    FamInputField(value = name, onValueChange = { name = it }, label = "Name", icon = Icons.Default.Person, isDarkMode = isDarkMode)
                    FamInputField(value = phone, onValueChange = { phone = it }, label = "Phone Number", icon = Icons.Default.Phone, isDarkMode = isDarkMode)
                    FamInputField(value = flat, onValueChange = { flat = it }, label = "Flat Number", icon = Icons.Default.Home, isDarkMode = isDarkMode)
                    FamInputField(value = purpose, onValueChange = { purpose = it }, label = "Purpose", icon = Icons.Default.Info, isDarkMode = isDarkMode)

                    Spacer(Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF1C1C1C) else Color(0xFF00a8ff))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Register & Notify",
                            fontWeight = FontWeight.Bold,
                            color = Color.White 
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FamInputField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, isDarkMode: Boolean) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = { Icon(icon, null, tint = if (isDarkMode) GuardFamGold.copy(alpha = 0.7f) else Color.Black) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isDarkMode) GuardFamGold else Color(0xFF00a8ff),
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
            unfocusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.03f) else Color.Black.copy(alpha = 0.01f),
            focusedTextColor = if (isDarkMode) Color.White else Color.Black,
            unfocusedTextColor = if (isDarkMode) Color.White else Color.Black
        )
    )
}

@Composable
fun ActivityDetailScreen(isDarkMode: Boolean, name: String, flat: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.verticalGradient(colors = listOf(Color(0xFF1A1612), GuardFamDarkBg))
                } else {
                    BlueVerticalGradient
                }
            )
            .statusBarsPadding()
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.background(if (isDarkMode) GuardFamSurface else Color(0xFFF1F5F9), CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = if (isDarkMode) Color.White else Color.Black)
            }
            Spacer(Modifier.width(16.dp))
            Text("Details", fontSize = 24.sp, fontWeight = FontWeight.Black, color = if (isDarkMode) Color.White else Color.Black)
        }

        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = if (isDarkMode) GuardFamSurface else Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Gray.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp).background(if (isDarkMode) GuardFamDarkBg else Color(0xFFF1F5F9), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(32.dp), tint = if (isDarkMode) GuardFamGold else Color.Black)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color.Black)
                        Text("Status: Waiting", color = if (isDarkMode) GuardFamGold else Color.Red, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = if (isDarkMode) GuardFamDivider else Color.Gray.copy(alpha = 0.1f))
                DetailRowFam("Flat", flat, isDarkMode)
                DetailRowFam("Time", "10:25 AM", isDarkMode)
                DetailRowFam("Purpose", "Delivery", isDarkMode)

                Spacer(Modifier.height(40.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(1.dp, if (isDarkMode) GuardFamGold else Color(0xFF22C55E), CircleShape)
                            .clip(CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Call Owner", color = if (isDarkMode) GuardFamGold else Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = onBack, modifier = Modifier.weight(1f).height(50.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.1f), contentColor = Color(0xFFEF4444))) {
                        Text("Reject")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRowFam(label: String, value: String, isDarkMode: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray); Text(value, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color.Black)
    }
}

@Composable
fun HeaderSectionFam(isOnline: Boolean, onToggle: (Boolean) -> Unit, isDarkMode: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(24.dp).padding(top = 16.dp),
        shape = RoundedCornerShape(32.dp),
        color = if (isDarkMode) GuardFamSurface else Color.White,
        border = BorderStroke(1.dp, if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Gray.copy(alpha = 0.1f)),
        shadowElevation = if (isDarkMode) 0.dp else 4.dp
    ) {
        Row(modifier = Modifier.padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("DASHBOARD", color = if (isDarkMode) GuardFamGold else Color(0xFF00a8ff), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Hi, Hashmi", color = if (isDarkMode) Color.White else Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(10.dp).background(if(isOnline) GuardFamGreen else Color.Red, CircleShape))
                Switch(checked = isOnline, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GuardFamGreen))
            }
        }
    }
}

@Composable
fun BentoGridFam(onCheckInClick: () -> Unit, isDarkMode: Boolean) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .weight(1.4f)
                    .height(140.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(if (isDarkMode) Brush.linearGradient(listOf(Color(0xFF1C1814), Color(0xFF1C1814))) else BlueVerticalGradient)
                    .clickable { onCheckInClick() }
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(36.dp).background(if(isDarkMode) GuardFamGold.copy(0.1f) else Color.White.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, null, tint = if(isDarkMode) GuardFamGold else Color.White, modifier = Modifier.size(20.dp)) }
                    Column {
                        Text("Check-in", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if(isDarkMode) GuardFamGold else Color.White)
                        Text("Add Visitor", fontSize = 11.sp, color = if(isDarkMode) Color.Gray else Color.White.copy(alpha = 0.7f))
                    }
                }
            }
            BentoTileFam("Logs", "History", Icons.Default.History, Modifier.weight(1f), if (isDarkMode) GuardFamSurface else Color.White, if (isDarkMode) Color.White else Color.Black, {}, isDarkMode)
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            BentoTileFam("SOS", "Panic", Icons.Default.Warning, Modifier.weight(1f), if (isDarkMode) GuardFamSurface else Color.White, Color(0xFFEF4444), {}, isDarkMode)
            BentoTileFam("Society", "Rules", Icons.Default.Info, Modifier.weight(1.2f), if (isDarkMode) GuardFamSurface else Color.White, if (isDarkMode) Color.White else Color.Black, {}, isDarkMode)
        }
    }
}

@Composable
fun BentoTileFam(title: String, sub: String, icon: ImageVector, modifier: Modifier, bg: Color, fg: Color, onClick: () -> Unit, isDarkMode: Boolean) {
    Surface(
        modifier = modifier.height(140.dp).clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        color = bg,
        border = BorderStroke(1.dp, if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Gray.copy(alpha = 0.1f)),
        shadowElevation = if (isDarkMode) 0.dp else 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(36.dp).background(fg.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, null, tint = fg, modifier = Modifier.size(20.dp)) }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = fg)
                Text(sub, fontSize = 11.sp, color = if (isDarkMode) Color.Gray else Color.Gray.copy(0.8f))
            }
        }
    }
}

@Composable
fun PendingRequestItemFam(name: String, flat: String, time: String, isDarkMode: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isDarkMode) GuardFamSurface else Color.White,
        border = BorderStroke(1.dp, if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Gray.copy(alpha = 0.1f)),
        shadowElevation = if (isDarkMode) 0.dp else 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), color = if (isDarkMode) GuardFamDarkBg else Color(0xFFF1F5F9), shape = CircleShape) {
                Icon(Icons.Default.Person, null, tint = if (isDarkMode) Color.Gray else Color.Black, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isDarkMode) Color.White else Color.Black)
                Text("Flat $flat • $time", fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GuardFamPreview() {
    NexEntryTheme { GuardDashboardScreen(isDarkMode = true) }
}
