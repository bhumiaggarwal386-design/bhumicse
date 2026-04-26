package com.example.bhumicse

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

// ─────────────────────────────────────────────
// 🔥 PERSISTENCE
// ─────────────────────────────────────────────

private const val OUTFIT_FILE = "outfits.json"

private fun saveOutfits(context: Context, outfits: List<Outfit>) {
    val jsonArray = JSONArray()

    outfits.forEach { outfit ->
        val obj = JSONObject().apply {
            put("id", outfit.id)
            put("name", outfit.name)
            put("rating", outfit.rating)

            val itemsArray = JSONArray()
            outfit.items.forEach { item ->
                val itemObj = JSONObject().apply {
                    put("itemId", item.itemId)
                    put("offsetX", item.offsetX)
                    put("offsetY", item.offsetY)
                    put("scale", item.scale)
                    put("rotation", item.rotation)
                }
                itemsArray.put(itemObj)
            }

            put("items", itemsArray)
        }

        jsonArray.put(obj)
    }

    // safer overwrite (prevents partial write corruption)
    File(context.filesDir, OUTFIT_FILE).writeText(jsonArray.toString())
}

private fun loadOutfits(context: Context): List<Outfit> {
    val file = File(context.filesDir, OUTFIT_FILE)

    if (!file.exists()) return emptyList()

    return try {
        val jsonArray = JSONArray(file.readText())

        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)

            val itemsArray = obj.getJSONArray("items")
            val items = mutableListOf<SavedCanvasItem>()

            for (j in 0 until itemsArray.length()) {
                val itemObj = itemsArray.getJSONObject(j)

                items.add(
                    SavedCanvasItem(
                        itemId = itemObj.getInt("itemId"),
                        offsetX = itemObj.getDouble("offsetX").toFloat(),
                        offsetY = itemObj.getDouble("offsetY").toFloat(),
                        scale = itemObj.getDouble("scale").toFloat(),
                        rotation = itemObj.getDouble("rotation").toFloat()
                    )
                )
            }

            Outfit(
                id = obj.getInt("id"),
                name = obj.getString("name"),
                rating = obj.getInt("rating"),
                items = items
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// ─────────────────────────────────────────────
// 👇 OUTFIT CARD (UNCHANGED)
// ─────────────────────────────────────────────

@Composable
fun OutfitCard(
    outfit: Outfit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onPreview: () -> Unit,
    onRatingChange: (Int) -> Unit
) {
    var currentRating by remember(outfit.id) { mutableStateOf(outfit.rating) }

    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2))
                    .clickable { onPreview() }
            ) {
                val firstImage = outfit.items.firstOrNull()
                    ?.let { saved ->
                        SampleData.items.firstOrNull { it.id == saved.itemId }
                    }

                if (firstImage != null) {
                    Image(
                        painter = painterResource(id = firstImage.imageRes),
                        contentDescription = outfit.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(outfit.name, fontWeight = FontWeight.SemiBold)

            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= currentRating)
                            Icons.Filled.Star
                        else
                            Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                currentRating = i
                                onRatingChange(i)
                            }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// 📌 MAIN SCREEN
// ─────────────────────────────────────────────

@Composable
fun OutfitScreen(
    onAddClick: () -> Unit,
    onEditClick: (Outfit) -> Unit
) {
    val context = LocalContext.current

    val outfits = remember { mutableStateListOf<Outfit>() }

    LaunchedEffect(Unit) {
        outfits.clear()
        outfits.addAll(loadOutfits(context))
    }

    var previewOutfit by remember { mutableStateOf<Outfit?>(null) }

    Scaffold(
        topBar = { AppHeader("My Wardrobe") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Outfit")
            }
        }
    ) { padding ->

        if (outfits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No outfits yet 👀")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding).padding(8.dp)
            ) {
                itemsIndexed(outfits, key = { _, outfit -> outfit.id }) { index, outfit ->

                    OutfitCard(
                        outfit = outfit,

                        onDelete = {
                            outfits.removeAt(index)
                            saveOutfits(context, outfits)
                        },

                        onEdit = { onEditClick(outfit) },

                        onPreview = { previewOutfit = outfit },

                        onRatingChange = { newRating: Int ->
                            outfits[index] = outfits[index].copy(rating = newRating)
                            saveOutfits(context, outfits)
                        }
                    )
                }
            }
        }
    }

    // ── PREVIEW ─────────────────────────────
    previewOutfit?.let { outfit ->
        Dialog(
            onDismissRequest = { previewOutfit = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F2))
            ) {

                outfit.items.forEach { saved ->
                    val item = SampleData.items.firstOrNull { it.id == saved.itemId }

                    if (item != null) {
                        Box(
                            modifier = Modifier.offset {
                                IntOffset(saved.offsetX.toInt(), saved.offsetY.toInt())
                            }
                        ) {
                            Image(
                                painter = painterResource(id = item.imageRes),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(120.dp)
                                    .graphicsLayer(
                                        scaleX = saved.scale,
                                        scaleY = saved.scale,
                                        rotationZ = saved.rotation
                                    )
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(20.dp)
                ) {
                    Text(
                        outfit.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
