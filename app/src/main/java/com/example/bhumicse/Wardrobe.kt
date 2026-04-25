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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


data class WardrobeItem(
    val id: Int,
    val name: String,
    val imageRes: Int
)

@Composable
fun WardrobeScreen4() {
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    fun createNewImageUri(): Uri? {
        val directory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "wardrobe"
        )
        if (!directory.exists()) directory.mkdirs()
        val file = File(directory, "item_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Toast.makeText(context, "Photo Captured!", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createNewImageUri()
            tempImageUri = uri
            uri?.let { cameraLauncher.launch(it) }
        }
    }

    fun launchCamera() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val uri = createNewImageUri()
            tempImageUri = uri
            uri?.let { cameraLauncher.launch(it) }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val topWearChips = listOf("All", "Shirt", "Jacket", "Hoodie", "Denim")
    val bottomWearChips = listOf("All", "Jeans", "Trouser", "Skirt", "Shorts")
    val accessoriesChips = listOf("All", "Bangles", "Earrings", "Necklace", "Bracelet")

    // ✅ No Scaffold or BottomNavBar here — just the content + FAB in a Box
    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar just for the wardrobe title
            TopAppBar(
                title = { Text("My Wardrobe", color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFE4E1)
                )
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    CategorySection(
                        title = "Top Wear",
                        items = topWearItems(),
                        chips = topWearChips,
                        onNewItemClick = { launchCamera() }
                    )
                }
                item {
                    CategorySection(
                        title = "Bottom Wear",
                        items = bottomWearItems(),
                        chips = bottomWearChips,
                        onNewItemClick = { launchCamera() }
                    )
                }
                item {
                    CategorySection(
                        title = "Accessories",
                        items = AcessoriesItems(),
                        chips = accessoriesChips,
                        onNewItemClick = { launchCamera() }
                    )
                }
            }
        }

        // ✅ FAB stays in the wardrobe screen, floated over content
        FloatingActionButton(
            onClick = { launchCamera() },
            containerColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}


@Composable
fun CategorySection(
    title: String,
    items: List<WardrobeItem>,
    chips: List<String>,
    onNewItemClick: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(12.dp)) {

        Text(text = title, fontWeight = Bold, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(8.dp))

        CategoryChips(chips = chips)

        Spacer(modifier = Modifier.height(10.dp))

        val allCards = mutableListOf<@Composable () -> Unit>()
        allCards.add { NewItemCard(onClick = onNewItemClick) }
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
                if (rowItems.size < 3) {
                    Spacer(modifier = Modifier.weight((3 - rowItems.size).toFloat()))
                }
            }
        }
    }
}

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

    val customPainter = remember(item.imageRes) {
        try {
            val bitmap = BitmapFactory.decodeResource(context.resources, item.imageRes)
            if (bitmap != null) BitmapPainter(bitmap.asImageBitmap()) else null
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
fun NewItemCard(onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .size(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        onClick = onClick
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

fun topWearItems(): List<WardrobeItem> {
    return listOf(
        WardrobeItem(1, "Shirt", R.drawable.shirt),
        WardrobeItem(2, "T-Shirt", R.drawable.tshirt)
    )
}

fun bottomWearItems(): List<WardrobeItem> {
    return listOf(
        WardrobeItem(1, "Jeans", R.drawable.jeans)
    )
}

fun AcessoriesItems(): List<WardrobeItem> {
    return listOf(
        WardrobeItem(1, "Earrings", R.drawable.earrings)
    )
}

@Preview(showBackground = true)
@Composable
fun WardrobePreview() {
    WardrobeScreen()
}