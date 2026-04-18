package com.example.bhumicse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults.containerColor
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun SecondScreen() {
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
    )

        var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
            containerColor = Color.White,
                bottomBar = {
                    NavigationBar(containerColor = Color(0xFFFFB6C1)) {

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
                                    Icon(
                                        painter = painterResource(id = R.drawable.create),
                                        contentDescription = "Create",
//                                        Modifier = Modifier.size(30.dp),
                                        tint = if (selectedItem == 0) Color.White else Color.Gray
                                    )
                                }
                            },
                            label = { Text("Create")}

                        )

                        NavigationBarItem(
                            selected = selectedItem == 1,
                            onClick = { selectedItem = 1 },
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
                                    Icon(
                                        painter = painterResource(id = R.drawable.wardrobe2),
                                        contentDescription = "Wardrobe",
                                        tint = if (selectedItem == 0) Color.White else Color.Gray
                                    )
                                }
                            },
                            label = { Text("Wrardrobe")}
                        )

                        NavigationBarItem(
                            selected = selectedItem == 2,
                            onClick = { selectedItem = 2 },
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
                                    Icon(
                                        painter = painterResource(id = R.drawable.planner),
                                        contentDescription = "Planner",
                                        tint = if (selectedItem == 0) Color.White else Color.Gray
                                    )
                                }
                            },
                            label = { Text("Planner")}
                        )
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier.padding(paddingValues)
                ) {

                    when (selectedItem) {
                        0 -> CreateScreen()
                        1 -> WardrobeScreen()
                        2 -> PlannerScreen()
                    }
                }
            }
//            modifier = TODO(),
//            topBar = TODO(),
//            bottomBar = TODO(),
//            snackbarHost = TODO(),
//            floatingActionButton = TODO(),
//            floatingActionButtonPosition = TODO(),
//            contentColor = TODO(),
//            contentWindowInsets = TODO(),
//            content = TODO()
    }

@Composable
fun CreateScreen() {

      var userName by remember { mutableStateOf("User") }

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            Text(
                text = "Hi, $userName 👋",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "What should I wear today?",
                fontSize = 16.sp,
                color = Color.Gray
            )
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text("Today's Outfit", fontWeight = FontWeight.Bold)
//                    Text("Tap to generate ideas")
//                }
//            }
        }
    }


@Composable
fun WardrobeScreen() {
    Text("Wardrobe Screen")
}

@Composable
fun PlannerScreen() {
    Text("Your Planner")
}
@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {
    SecondScreen()
}