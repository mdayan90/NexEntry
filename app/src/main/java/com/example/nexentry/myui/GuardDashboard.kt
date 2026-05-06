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
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexentry.R
import com.example.nexentry.ui.theme.NexEntryTheme
import com.example.nexentry.ui.theme.PrimaryBlue
import com.example.nexentry.ui.theme.SecondaryBlue
import com.example.nexentry.ui.theme.TertiaryBlue
import com.example.nexentry.ui.theme.DarkBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// Palette using the app's new blue theme
val GuardFamDarkBg = DarkBackground
val GuardFamSurface = TertiaryBlue
val GuardFamGold = Color(0xFFE5C185)
val GuardFamGreen = Color(0xFF00DC82)
val GuardFamDivider = Color(0xFF262626)
val Nexentry_color = PrimaryBlue

val BlueVerticalGradient = Brush.verticalGradient(
    listOf(
        PrimaryBlue.copy(alpha = 0.1f), // Top
        Color.White  // Bottom
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
                    Brush.linearGradient(listOf(Color(0xFF0A0A0A), Color(0xFF0A0A0A)))
                } else {
                    BlueVerticalGradient
                }
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Name Centered at top
            Spacer(modifier = Modifier.statusBarsPadding().height(16.dp))


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Text(
                        text = "NexEntry",
                        modifier = Modifier.fillMaxWidth(),
                        color = if (isDarkMode) GuardFamGold else Nexentry_color,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    HeaderSectionFam(isOnline = isOnline, onToggle = { isOnline = it }, isDarkMode = isDarkMode)
                }

                item {
                    BentoGridFam(onCheckInClick = onCheckInClick,  isDarkMode = isDarkMode)
                }

                item {
                    Text(
                        text = "Pending Activity",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isDarkMode) Color.White.copy(0.6f) else Color.Gray,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
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
                    Brush.linearGradient(listOf(Color(0xFF0A0A0A), Color(0xFF0A0A0A)))
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
                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(20.dp)).background(if (isDarkMode) GuardFamDarkBg else Color(0xFFF1F5F9)).border(1.dp, if (isDarkMode) GuardFamGold.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(20.dp))
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
                            .background(if (isDarkMode) Color(0xFF1C1C1C) else Nexentry_color)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Register & Notify",
                            fontWeight = FontWeight.Bold,
                            color = Color.White // Always white as per request
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
        label = { Text(label, color = if(isDarkMode) Color.Gray else Color.Black.copy(0.6f)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = { Icon(icon, null, tint = if (isDarkMode) GuardFamGold.copy(alpha = 0.7f) else Color.Black) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isDarkMode) GuardFamGold else Color(0xFF00a8ff),
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9),
            unfocusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.03f) else Color(0xFFF1F5F9),
            focusedTextColor = if (isDarkMode) Color.White else Color.Black,
            unfocusedTextColor = if (isDarkMode) Color.White else Color.Black
        )
    )
}

@Composable
fun ActivityDetailScreen(isDarkMode: Boolean, name: String, flat: String, onBack: () -> Unit) {
    val lightGreenAction = Color(0xFFDCFCE7)
    val lightGreenText = Color(0xFF16A34A)
    val lightRedAction = Color(0xFFFD0101)
    val lightRedText = Color(0xFFFFFFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.linearGradient(listOf(Color(0xFF0A0A0A), Color(0xFF0A0A0A)))
                } else {
                    BlueVerticalGradient
                }
            )
            .statusBarsPadding()
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.background(if (isDarkMode) GuardFamSurface else Color.White, CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = if (isDarkMode) Color.White else Color.Black)
            }
            Spacer(Modifier.width(16.dp))
            Text("Details", fontSize = 24.sp, fontWeight = FontWeight.Black, color = if (isDarkMode) Color.White else Color.Black)
        }

        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = if (isDarkMode) GuardFamSurface else Color.White,
            shadowElevation = if (isDarkMode) 0.dp else 4.dp,
            border = if (isDarkMode) BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else null
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
                            .border(1.dp, if (isDarkMode) GuardFamGold else Color.Transparent, CircleShape)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)) else Brush.linearGradient(listOf(lightGreenAction, lightGreenAction)))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Call Owner", color = if (isDarkMode) GuardFamGold else lightGreenText, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onBack, 
                        modifier = Modifier.weight(1f).height(50.dp), 
                        shape = CircleShape, 
                        colors = ButtonDefaults.buttonColors(containerColor = lightRedAction, contentColor = lightRedText)
                    ) {
                        Text("Reject", fontWeight = FontWeight.Bold)
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
        shadowElevation = if (isDarkMode) 0.dp else 4.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Surface(color = if(isDarkMode) GuardFamGold.copy(0.1f) else Color(0xFFE0E7FF), shape = CircleShape) {
                        Text("DASHBOARD", color = if (isDarkMode) GuardFamGold else Color(0xFF4F46E5), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Hi, Hashmi", color = if (isDarkMode) Color.White else Color.Black, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                }
                Switch(checked = isOnline, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GuardFamGreen))
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip("Gate Open", Color(0xFFDCFCE7), Color(0xFF16A34A))
                StatusChip("3 Pending", Color(0xFFFEF3C7), Color(0xFFD97706))
                StatusChip("Flat B-401", Color(0xFFF1F5F9), Color.Gray)
            }
        }
    }
}

@Composable
fun StatusChip(text: String, bg: Color, fg: Color) {
    Surface(color = bg, shape = CircleShape) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).background(fg, CircleShape))
            Spacer(Modifier.width(6.dp))
            Text(text, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BentoGridFam(onCheckInClick: () -> Unit, isDarkMode: Boolean) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("QUICK ACTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
        
        // Check-in (Full Width)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clickable { onCheckInClick() },
            shape = RoundedCornerShape(28.dp),
            color = if (isDarkMode) Color(0xFF121212) else Color(0xFF2563EB),
            shadowElevation = if (isDarkMode) 0.dp else 8.dp
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.size(36.dp).background(if(isDarkMode) GuardFamGold.copy(0.1f) else Color.White.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) { 
                    Icon(Icons.Default.PersonAdd, null, tint = if(isDarkMode) GuardFamGold else Color.White, modifier = Modifier.size(20.dp)) 
                }
                Column {
                    Text("Check-in", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if(isDarkMode) GuardFamGold else Color.White)
                    Text("Add visitor", fontSize = 11.sp, color = if(isDarkMode) Color.Gray else Color.White.copy(alpha = 0.7f))
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            BentoTileFam("Society", "Rules & Info", Icons.Default.Domain, Modifier.weight(1f), isDarkMode)
            // SOS Tile
            Surface(
                modifier = Modifier.weight(1f).height(140.dp).clickable { },
                shape = RoundedCornerShape(28.dp),
                color = if (isDarkMode) GuardFamSurface else Color.White,
                border = if (isDarkMode) BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else null
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(36.dp).background(if(isDarkMode) Color.Red.copy(0.1f) else Color.White, CircleShape), contentAlignment = Alignment.Center) { 
                        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(20.dp)) 
                    }
                    Column {
                        Text("SOS", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if(isDarkMode) Color.Red else Color.Red)
                        Text("Panic alert", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun BentoTileFam(title: String, sub: String, icon: ImageVector, modifier: Modifier, isDarkMode: Boolean) {
    Surface(
        modifier = modifier.height(140.dp).clickable { },
        shape = RoundedCornerShape(28.dp),
        color = if (isDarkMode) GuardFamSurface else Color.White,
        shadowElevation = if (isDarkMode) 0.dp else 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(36.dp).background(if(isDarkMode) Color.White.copy(0.05f) else Color(0xFFF1F5F9), CircleShape), contentAlignment = Alignment.Center) { 
                Icon(icon, null, tint = if(isDarkMode) Color.White else Color.Gray, modifier = Modifier.size(20.dp)) 
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isDarkMode) Color.White else Color.Black)
                Text(sub, fontSize = 11.sp, color = Color.Gray)
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
        shadowElevation = if (isDarkMode) 0.dp else 4.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), color = if (isDarkMode) GuardFamDarkBg else Color(0xFFE0E7FF), shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) {
                    Text(name.take(2).uppercase(), color = if (isDarkMode) GuardFamGold else Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isDarkMode) Color.White else Color.Black)
                Text("$flat • $time • Visitor", fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, modifier = Modifier.size(14.dp), tint = Color.LightGray)
        }
    }
}

@Preview(name = "Small Phones", device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Large Phone", device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(name = "Foldable", device = "spec:width=673dp,height=841dp,dpi=420")
@Composable
fun MultiGuardDevicePreview() {
    NexEntryTheme {
        GuardDashboardScreen(isDarkMode = true)
    }
}
