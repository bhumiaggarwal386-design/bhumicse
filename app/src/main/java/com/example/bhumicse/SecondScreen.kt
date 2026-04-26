package com.example.bhumicse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.ui.screens.WardrobeScreen

@Composable
fun SecondScreen() {

    // Bottom tab state
    var selectedItem by remember { mutableStateOf(0) }

    // Outfit ↔ Canvas navigation state
    var currentScreen by remember { mutableStateOf("outfit") }
    var selectedOutfit by remember { mutableStateOf<Outfit?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0F5),
                        Color.White
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(containerColor = Color(0xFFFFE4E1)) {

                    // TAB 1 → CREATE / OUTFIT
                    NavigationBarItem(
                        selected = selectedItem == 0,
                        onClick = { selectedItem = 0 },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .background(
                                        if (selectedItem == 0) Color(0xFFFF69B4) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Create Outfit")
                            }
                        },
                        label = { Text("Create") }
                    )

                    // TAB 2 → WARDROBE
                    NavigationBarItem(
                        selected = selectedItem == 1,
                        onClick = { selectedItem = 1 },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .background(
                                        if (selectedItem == 1) Color(0xFFFF69B4) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.wardrobe2),
                                    contentDescription = "Wardrobe",
                                    tint = if (selectedItem == 1) Color.White else Color.Gray
                                )
                            }
                        },
                        label = { Text("Wardrobe") }
                    )

                    // TAB 3 → PLANNER
                    NavigationBarItem(
                        selected = selectedItem == 2,
                        onClick = { selectedItem = 2 },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .background(
                                        if (selectedItem == 2) Color(0xFFFF69B4) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Planner")
                            }
                        },
                        label = { Text("Planner") }
                    )
                }
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier.padding(paddingValues)
            ) {

                when (selectedItem) {

                    // TAB 1: Outfit + Canvas Flow
                    0 -> {
                        when (currentScreen) {

                            "outfit" -> {
                                OutfitScreen(
                                    onAddClick = {
                                        selectedOutfit = null
                                        currentScreen = "canvas"
                                    },
                                    onEditClick = { outfit ->
                                        selectedOutfit = outfit
                                        currentScreen = "canvas"
                                    }
                                )
                            }

                            "canvas" -> {
                                CanvasScreen(
                                    outfit = selectedOutfit,
                                    onSave = {
                                        currentScreen = "outfit"
                                    }
                                )
                            }
                        }
                    }

                    // TAB 2: WARDROBE (FIX APPLIED HERE)
                    1 -> Box(modifier = Modifier.fillMaxSize()) {
                        WardrobeScreen()
                    }

                    // TAB 3: PLANNER
                    2 -> PlannerScreen1()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {
    SecondScreen()
}
