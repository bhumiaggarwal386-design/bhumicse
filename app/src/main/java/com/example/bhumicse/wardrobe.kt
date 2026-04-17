@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bhumicse


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview


data class ClothingItem(
    val id: Int,
    val name: String,
    val imageRes: Int
)
@Composable
fun WardScreen() {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { WardrobeTopBar() },
        bottomBar = { BottomNavBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Added successfully into your wardrobe")
                    }
                },
                containerColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {

            item {
                CategorySection(
                    title = "Top Wear",
                    items = sampleItems()
                )
            }

            item {
                CategorySection(
                    title = "Bottom Wear",
                    items = sampleItems()
                )
            }

            item {
                CategorySection(
                    title = "Accessories",
                    items = sampleItems()
                )
            }
        }
    }
}

@Composable
fun WardrobeTopBar() {
    TopAppBar(
        title = {
            Text("My Wardrobe", color = Color.White)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}
@Composable
fun CategorySection(title: String, items: List<ClothingItem>) {

    Column(modifier = Modifier.padding(12.dp)) {

        Text(
            text = title,
            fontWeight =Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        CategoryChips()

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(220.dp)
        ) {

            item {
                NewItemCard()
            }

            items(items.size) {
                ClothingCard(items[it])
            }
        }
    }
}
@Composable
fun CategoryChips() {

    val chips = listOf("All", "Shirt", "Jacket", "Hoodie", "Denim")

    LazyRow {
        items(chips) { chip ->
            AssistChip(
                onClick = { },
                label = { Text(chip) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}
@Composable
fun ClothingCard(item: ClothingItem) {

    Card(
        modifier = Modifier
            .padding(6.dp)
            .size(100.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        Box(contentAlignment = Alignment.BottomEnd) {

            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    }
}
@Composable
fun NewItemCard() {

    Card(
        modifier = Modifier
            .padding(6.dp)
            .size(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Add, contentDescription = "")
                Text("New Item", fontSize = 12.sp)
            }
        }
    }
}
@Composable
fun BottomNavBar() {

    NavigationBar {

        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = "") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Checkroom, contentDescription = "") },
            label = { Text("Wardrobe") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Person, contentDescription = "") },
            label = { Text("Profile") }
        )
    }
}fun sampleItems(): List<ClothingItem> {
    return listOf(
        ClothingItem(1, "Shirt", R.drawable.shirt),
        ClothingItem(2, "T-Shirt", R.drawable.tshirt),
        ClothingItem(3, "Jeans", R.drawable.jeans)
    )
}
@Preview(showBackground = true)
@Composable
fun WardrobePreview() {
    WardScreen()
}