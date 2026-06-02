package com.example.bhumicse

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bhumicse.data.CanvasViewModel
import com.example.bhumicse.data.OutfitWithItems
import com.example.bhumicse.data.toClothingData
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CanvasScreen(
    outfitWithItems: OutfitWithItems? = null,
    onSave: () -> Unit,
    onBack: () -> Unit = {}          // ← new parameter
) {
    val context = LocalContext.current
    val app     = context.applicationContext as android.app.Application
    val scope   = rememberCoroutineScope()

    val viewModel = viewModel<CanvasViewModel>(
        factory = CanvasViewModel.factory(app)
    )

    val wardrobeItems by viewModel.allItems.collectAsStateWithLifecycle()

    var selectedCategory    by remember { mutableStateOf(Category.TOPWEAR) }
    var showNameDialog      by remember { mutableStateOf(false) }
    var showBackDialog      by remember { mutableStateOf(false) } // ← new
    var outfitName          by remember {
        mutableStateOf(outfitWithItems?.outfit?.name ?: "")
    }

    val canvasItems = remember { mutableStateListOf<CanvasItem>() }

    LaunchedEffect(outfitWithItems) {
        if (outfitWithItems != null) {
            val loaded = viewModel.loadCanvasItems(outfitWithItems)
            canvasItems.clear()
            canvasItems.addAll(loaded)
        }
    }

    // REPLACE topBar
    Scaffold(
        topBar = {
            AppHeader(onBack = { showBackDialog = true })
        }
    ){ padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── CANVAS ─────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF2F2F2))
            ) {
                canvasItems.forEach { canvasItem ->
                    key(canvasItem.uid) {

                        var offsetX  by remember { mutableStateOf(canvasItem.offsetX) }
                        var offsetY  by remember { mutableStateOf(canvasItem.offsetY) }
                        var scale    by remember { mutableStateOf(canvasItem.scale) }
                        var rotation by remember { mutableStateOf(canvasItem.rotation) }

                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                                .pointerInput(canvasItem.uid) {
                                    // ⛔ GESTURE LOGIC UNCHANGED
                                    detectTransformGestures { _, pan, zoom, rotate ->
                                        offsetX   += pan.x
                                        offsetY   += pan.y
                                        scale     *= zoom
                                        rotation  += rotate

                                        canvasItem.offsetX  = offsetX
                                        canvasItem.offsetY  = offsetY
                                        canvasItem.scale    = scale
                                        canvasItem.rotation = rotation
                                    }
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = Uri.parse(canvasItem.item.imageUri)
                                ),
                                contentDescription = null,
                                contentScale       = ContentScale.Fit,
                                modifier           = Modifier
                                    .size(120.dp)
                                    .graphicsLayer(
                                        scaleX    = scale,
                                        scaleY    = scale,
                                        rotationZ = rotation
                                    )
                            )

                            IconButton(
                                onClick  = { canvasItems.remove(canvasItem) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }

            // ── CATEGORY BUTTONS ───────────────
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
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else Color.LightGray
                        )
                    ) {
                        Text(category.name)
                    }
                }
            }

            // ── ITEM PICKER ────────────────────
            val filteredItems = wardrobeItems.filter {
                val categoryMatch = when (selectedCategory) {
                    Category.TOPWEAR    -> "topwear"
                    Category.BOTTOMWEAR -> "bottomwear"
                    Category.ACCESSORY  -> "accessories"
                }
                it.category.lowercase() == categoryMatch
            }

            if (filteredItems.isEmpty()) {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "No items in wardrobe for this category.\nAdd some in the Wardrobe tab!",
                        color = Color.Gray
                    )
                }
            } else {
                LazyRow(modifier = Modifier.padding(8.dp)) {
                    items(filteredItems) { entity ->
                        Card(
                            modifier  = Modifier
                                .padding(6.dp)
                                .size(100.dp),
                            shape     = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier         = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        canvasItems.add(
                                            CanvasItem(
                                                item     = entity.toClothingData(),
                                                offsetX  = 300f,
                                                offsetY  = 500f,
                                                scale    = 1f,
                                                rotation = 0f
                                            )
                                        )
                                    }
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = Uri.parse(entity.imageUri)
                                    ),
                                    contentDescription = null,
                                    modifier           = Modifier.size(80.dp)
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick  = { showNameDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text("Save Outfit")
            }
        }
    }

    // ── BACK CONFIRMATION DIALOG ───────────────
    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            shape = RoundedCornerShape(20.dp),
            icon  = {
                Icon(
                    imageVector        = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text       = "Leave Canvas?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text  = "Your current canvas will be lost unless you save it first.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Save
                    Button(
                        onClick  = {
                            showBackDialog = false
                            showNameDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Outfit")
                    }
                    // Discard
                    OutlinedButton(
                        onClick  = {
                            showBackDialog = false
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Discard & Go Back")
                    }
                    // Cancel
                    TextButton(
                        onClick  = { showBackDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            },
            dismissButton = {}
        )
    }
    // ── SAVE DIALOG ────────────────────────────
    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Name your outfit") },
            text  = {
                OutlinedTextField(
                    value         = outfitName,
                    onValueChange = { outfitName = it }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val finalName = outfitName.ifBlank {
                        outfitWithItems?.outfit?.name ?: "Outfit"
                    }

                    if (outfitWithItems == null) {
                        viewModel.saveNewOutfit(
                            name        = finalName,
                            canvasItems = canvasItems.toList()
                        )
                    } else {
                        viewModel.updateOutfit(
                            outfitId    = outfitWithItems.outfit.id,
                            name        = finalName,
                            canvasItems = canvasItems.toList()
                        )
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
