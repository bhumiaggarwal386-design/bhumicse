package com.example.bhumicse

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bhumicse.data.OutfitViewModel
import com.example.bhumicse.data.OutfitWithItems

@Composable
fun OutfitCard(
    outfitWithItems: OutfitWithItems,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onPreview: () -> Unit,
    onRatingChange: (Int) -> Unit
) {
    val outfit = outfitWithItems.outfit

    // ✅ Key on rating so stars always reflect
    // the current value from Room
    key(outfit.id, outfit.rating) {

        Card(
            modifier  = Modifier.padding(8.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
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
                    val firstItem = outfitWithItems.items.firstOrNull()
                    if (firstItem != null) {
                        Image(
                            painter            = rememberAsyncImagePainter(
                                model = Uri.parse(firstItem.imageUri)
                            ),
                            contentDescription = outfit.name,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(outfit.name, fontWeight = FontWeight.SemiBold)

                // ✅ Stars use outfit.rating directly
                // no remember needed — key() handles recomposition
                Row {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= outfit.rating)
                                Icons.Filled.Star
                            else
                                Icons.Outlined.Star,
                            contentDescription = null,
                            tint     = if (i <= outfit.rating)
                                Color(0xFFFFC107)
                            else
                                Color.LightGray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
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
}
@Composable
fun OutfitScreen(
    onAddClick: () -> Unit,
    onEditClick: (OutfitWithItems) -> Unit
) {
    val context = LocalContext.current
    val app     = context.applicationContext as android.app.Application

    val viewModel = viewModel<OutfitViewModel>(
        factory = OutfitViewModel.factory(app)
    )

    val outfits by viewModel.allOutfits.collectAsStateWithLifecycle()
    var previewOutfit by remember { mutableStateOf<OutfitWithItems?>(null) }

    Scaffold(
        topBar = { AppHeader("My Outfits") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Outfit")
            }
        }
    ) { padding ->

        if (outfits.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No outfits yet 👀")
            }
        } else {
            LazyVerticalGrid(
                columns  = GridCells.Fixed(2),
                modifier = Modifier.padding(padding).padding(8.dp)
            ) {
                itemsIndexed(
                    outfits,
                    key = { _, outfitWithItems -> outfitWithItems.outfit.id }
                ) { _, outfitWithItems ->

                    OutfitCard(
                        outfitWithItems = outfitWithItems,
                        onDelete = {
                            viewModel.deleteOutfit(outfitWithItems.outfit)
                        },
                        onEdit = {
                            onEditClick(outfitWithItems)
                        },
                        onPreview = {
                            previewOutfit = outfitWithItems
                        },
                        onRatingChange = { newRating ->
                            viewModel.updateRating(
                                outfitId = outfitWithItems.outfit.id,
                                rating   = newRating
                            )
                        }
                    )
                }
            }
        }
    }

    previewOutfit?.let { outfitWithItems ->

        val refs by viewModel.outfitItemRefs.collectAsStateWithLifecycle()

        LaunchedEffect(outfitWithItems.outfit.id) {
            viewModel.loadRefsForOutfit(outfitWithItems.outfit.id)
        }

        Dialog(
            onDismissRequest = { previewOutfit = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F2))
            ) {
                refs.forEach { ref ->
                    val item = outfitWithItems.items.firstOrNull {
                        it.id == ref.clothingItemId
                    }
                    if (item != null) {
                        Box(
                            modifier = Modifier.offset {
                                IntOffset(
                                    ref.offsetX.toInt(),
                                    ref.offsetY.toInt()
                                )
                            }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = Uri.parse(item.imageUri)
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(120.dp)
                                    .graphicsLayer(
                                        scaleX    = ref.scale,
                                        scaleY    = ref.scale,
                                        rotationZ = ref.rotation
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
                        outfitWithItems.outfit.name,
                        color      = Color.White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
