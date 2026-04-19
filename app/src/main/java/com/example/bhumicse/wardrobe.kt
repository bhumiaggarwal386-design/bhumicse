@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bhumicse

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory


// Renamed from ClothingItem to WardrobeItem to avoid conflict with the Room entity in ClothingItem.kt
data class WardrobeItem(
    val id: Int,
    val name: String,
    val imageRes: Int
)

@Composable
fun WardScreen() {
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
     val topWearChips = listOf("All", "Shirt", "Jacket","Hoodie","Denim")
    val bottomWearChips = listOf("All","Jeans","Trouser","Skirt","Shorts")
    val accessoriesChips = listOf("All","Bangles","Earrings","Necklace","Bracelet")


    Scaffold(
        containerColor = Color.Transparent,
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
                    items = topWearItems(),
                    chips = topWearChips
                )
            }

            item {
                CategorySection(
                    title = "Bottom Wear",
                    items = bottomWearItems(),
                    chips = bottomWearChips
                )
            }

            item {
                CategorySection(
                    title = "Accessories",
                    items = topWearItems(),
                    chips = accessoriesChips
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
fun CategorySection(title: String,
                    items: List<WardrobeItem>,
                    chips: List<String>) {

    Column(modifier = Modifier.padding(12.dp)) {

        Text(
            text = title,
            fontWeight = Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        CategoryChips(chips = chips)

        Spacer(modifier = Modifier.height(10.dp))

        // Replaced LazyVerticalGrid with a manual grid using Column and Row.
        // Nested Lazy layouts (like LazyVerticalGrid inside LazyColumn) frequently cause
        // NullPointerException in CompositionDataTree during Preview rendering in Android Studio.
        val allCards = mutableListOf<@Composable () -> Unit>()
        allCards.add { NewItemCard() }
        items.forEach { item ->
            allCards.add { ClothingCard(item) }
        }

        allCards.chunked(3).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { card ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        card()
                    }
                }
                // Fill remaining space if the row is not complete
                if (rowItems.size < 3) {
                    Spacer(modifier = Modifier.weight((3 - rowItems.size).toFloat()))
                }
            }
        }
    }
}
//@Composable
//fun WardScreenRoute() {
//    val model: ClothingViewModel = viewModel(
//        factory = ClothingViewModelFactory(
//            repository = ClothingRepository(
//                clothingDao = /* real DAO here */
//            )
//        )
//    )
//    WardScreen(model)
//}
@Composable
fun CategoryChips(chips: List<String>) {

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
fun ClothingCard(item: WardrobeItem) {
    val context = LocalContext.current
    
    // Manually load the bitmap to handle potential nulls safely, 
    // especially in the Android Studio Preview where decoding might fail.
    // This avoids the NullPointerException in BitmapPainter when the internal bitmap is null.
    val customPainter = remember(item.imageRes) {
        try {
            val bitmap = BitmapFactory.decodeResource(context.resources, item.imageRes)
            if (bitmap != null) {
                BitmapPainter(bitmap.asImageBitmap())
            } else {
                null
            }
        } catch (e: Throwable) {
            null
        }
    }

    Card(
        modifier = Modifier
            .padding(6.dp)
            .size(100.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        Box(contentAlignment = Alignment.BottomEnd) {
            
            Image(
                painter = customPainter ?: painterResource(id = R.drawable.ic_launcher_foreground),
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
            label = { Text("Create") }
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
            label = { Text("Planner") }
        )
    }
}

fun topWearItems(): List<WardrobeItem> {
    return listOf(
        WardrobeItem(1, "Shirt", R.drawable.shirt),
        WardrobeItem(2, "T-Shirt", R.drawable.tshirt)
    )
}
fun bottomWearItems(): List<WardrobeItem> {
    return listOf(
        WardrobeItem(1, "Shirt", R.drawable.jeans)
    )
}

@Preview(showBackground = true)
@Composable
fun WardrobePreview() {
    WardScreen()
}