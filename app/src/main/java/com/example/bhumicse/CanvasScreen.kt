package com.example.bhumicse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CanvasScreen(
    outfit: Outfit? = null,
    onSave: () -> Unit
) {

    var selectedCategory by remember { mutableStateOf(Category.TOPWEAR) }

    var showNameDialog by remember { mutableStateOf(false) }
    var outfitName by remember {
        mutableStateOf(outfit?.name ?: "")
    }

    val canvasItems = remember {
        mutableStateListOf<CanvasItem>().apply {
            outfit?.items?.let {
                addAll(
                    it.map { saved ->
                        CanvasItem(
                            item = SampleData.items.first { it.id == saved.itemId },
                            offsetX = saved.offsetX,
                            offsetY = saved.offsetY,
                            scale = saved.scale,
                            rotation = saved.rotation
                        )
                    }
                )
            }
        }
    }

    Scaffold(

        // ✅ HEADER ADDED HERE
        topBar = {
            AppHeader()
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // 🖼 CANVAS
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF2F2F2))
            ) {

                canvasItems.forEach { canvasItem ->

                    key(canvasItem.item.id) {

                        var offsetX by remember { mutableStateOf(canvasItem.offsetX) }
                        var offsetY by remember { mutableStateOf(canvasItem.offsetY) }
                        var scale by remember { mutableStateOf(canvasItem.scale) }
                        var rotation by remember { mutableStateOf(canvasItem.rotation) }

                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        offsetX.toInt(),
                                        offsetY.toInt()
                                    )
                                }
                                .pointerInput(canvasItem.item.id) {

                                    detectTransformGestures { _, pan, zoom, rotate ->

                                        offsetX += pan.x
                                        offsetY += pan.y
                                        scale *= zoom
                                        rotation += rotate

                                        canvasItem.offsetX = offsetX
                                        canvasItem.offsetY = offsetY
                                        canvasItem.scale = scale
                                        canvasItem.rotation = rotation
                                    }
                                }
                        ) {

                            Image(
                                painter = painterResource(id = canvasItem.item.imageRes),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(120.dp)
                                    .graphicsLayer(
                                        scaleX = scale,
                                        scaleY = scale,
                                        rotationZ = rotation
                                    )
                            )

                            IconButton(
                                onClick = { canvasItems.remove(canvasItem) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }

            // 🎛 CATEGORY BUTTONS
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Category.values()) { category ->

                    val isSelected = selectedCategory == category

                    Button(
                        onClick = { selectedCategory = category },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else Color.LightGray
                        )
                    ) {
                        Text(category.name)
                    }
                }
            }

            val filteredItems = SampleData.items.filter {
                it.category == selectedCategory
            }

            LazyRow(
                modifier = Modifier.padding(8.dp)
            ) {
                items(filteredItems) { item ->

                    Card(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    canvasItems.add(
                                        CanvasItem(
                                            item = item,
                                            offsetX = 300f,
                                            offsetY = 500f,
                                            scale = 1f,
                                            rotation = 0f
                                        )
                                    )
                                }
                        ) {
                            Image(
                                painter = painterResource(id = item.imageRes),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { showNameDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text("Save Outfit")
            }
        }
    }

    if (showNameDialog) {

        AlertDialog(
            onDismissRequest = { showNameDialog = false },

            title = { Text("Name your outfit") },

            text = {
                OutlinedTextField(
                    value = outfitName,
                    onValueChange = { outfitName = it },
                    placeholder = { Text("e.g. Party Look") }
                )
            },

            confirmButton = {
                TextButton(onClick = {

                    val finalName =
                        if (outfitName.isBlank())
                            outfit?.name ?: "Outfit ${SampleData.outfits.size + 1}"
                        else outfitName

                    val snapshot = canvasItems.map {
                        SavedCanvasItem(
                            itemId = it.item.id,
                            offsetX = it.offsetX,
                            offsetY = it.offsetY,
                            scale = it.scale,
                            rotation = it.rotation
                        )
                    }.toMutableList()

                    if (outfit == null) {
                        SampleData.outfits.add(
                            Outfit(
                                id = SampleData.outfits.size + 1,
                                name = finalName,
                                items = snapshot
                            )
                        )
                    } else {
                        outfit.name = finalName
                        outfit.items.clear()
                        outfit.items.addAll(snapshot)
                    }

                    showNameDialog = false
                    onSave()

                }) {
                    Text("Save")
                }
            },

            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}