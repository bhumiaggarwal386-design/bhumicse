package com.example.bhumicse

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bhumicse.data.OutfitWithItems
import com.example.bhumicse.data.PlannerViewModel
import java.util.*

@Composable
fun PlannerScreen1() {

    val context = LocalContext.current
    val app     = context.applicationContext as android.app.Application

    val viewModel = viewModel<PlannerViewModel>(
        factory = PlannerViewModel.factory(app)
    )

    val allOutfits by viewModel.allOutfits.collectAsStateWithLifecycle()
    val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()
    val refs       by viewModel.outfitItemRefs.collectAsStateWithLifecycle()

    var showDialog     by remember { mutableStateOf(false) }
    var selectedOutfit by remember { mutableStateOf<OutfitWithItems?>(null) }
    var selectedDate   by remember { mutableStateOf("") }
    var showFullImage  by remember { mutableStateOf(false) }
    var previewOutfit  by remember { mutableStateOf<OutfitWithItems?>(null) }

    LaunchedEffect(previewOutfit?.outfit?.id) {
        previewOutfit?.outfit?.id?.let {
            viewModel.loadRefsForOutfit(it)
        }
    }

    if (showFullImage) {
        BackHandler {
            showFullImage = false
            previewOutfit = null
        }
    }

    // ── Full outfit preview screen ───────────────────────────────
    val currentPreview = previewOutfit
    if (showFullImage && currentPreview != null) {
        Scaffold(
            topBar = { AppHeader("Planner") }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF2F2F2))
            ) {
                // Canvas area pushed down so items
                // don't hide behind the back button
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 72.dp)
                ) {
                    refs.forEach { ref ->
                        val item = currentPreview.items.firstOrNull {
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
                                    contentScale       = ContentScale.Fit,
                                    modifier           = Modifier
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
                }

                // Back button floats on top
                Button(
                    onClick = {
                        showFullImage = false
                        previewOutfit = null
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Text("← Back")
                }

                // Outfit name bar at bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(20.dp)
                ) {
                    Text(
                        currentPreview.outfit.name,
                        color      = Color.White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    // ── Main screen ──────────────────────────────────────────────
    Scaffold(
        topBar = { AppHeader("Planner") }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Button(
                onClick  = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Outfit to Planner")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (allEntries.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text     = "No outfits planned yet 📅",
                        fontSize = 15.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        allEntries,
                        key = { it.id }
                    ) { entry ->

                        val matchedOutfit = allOutfits.firstOrNull {
                            it.outfit.id == entry.outfitId
                        }

                        Card(
                            modifier  = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            shape   = MaterialTheme.shapes.large,
                            onClick = {
                                if (matchedOutfit != null) {
                                    previewOutfit = matchedOutfit
                                    showFullImage = true
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    val imageUri = matchedOutfit
                                        ?.items
                                        ?.firstOrNull()
                                        ?.imageUri ?: ""

                                    if (imageUri.isNotEmpty()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = Uri.parse(imageUri)
                                            ),
                                            contentDescription = null,
                                            contentScale       = ContentScale.Crop,
                                            modifier           = Modifier.size(64.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier         = Modifier.size(64.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("👗")
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column {
                                        Text(
                                            text       = matchedOutfit?.outfit?.name
                                                ?: "Unknown Outfit",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize   = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text     = "📅 ${entry.date}",
                                            fontSize = 12.sp,
                                            color    = MaterialTheme.colorScheme
                                                .onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteEntry(entry)
                                    }
                                ) {
                                    Icon(
                                        imageVector        = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint               = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Add dialog ───────────────────────────────────────────────
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("Plan an Outfit", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Select Outfit",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (allOutfits.isEmpty()) {
                        Text(
                            text     = "No outfits saved yet.\nCreate one in the Create tab first!",
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    } else {
                        allOutfits.forEach { outfitWithItems ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedOutfit?.outfit?.id ==
                                            outfitWithItems.outfit.id,
                                    onClick  = {
                                        selectedOutfit = outfitWithItems
                                    }
                                )

                                val thumbUri = outfitWithItems.items
                                    .firstOrNull()?.imageUri ?: ""

                                if (thumbUri.isNotEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = Uri.parse(thumbUri)
                                        ),
                                        contentDescription = null,
                                        modifier           = Modifier.size(40.dp)
                                    )
                                } else {
                                    Box(
                                        modifier         = Modifier.size(40.dp),
                                        contentAlignment = Alignment.Center
                                    ) { Text("👗") }
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Text(outfitWithItems.outfit.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick  = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    selectedDate = "$d/${m + 1}/$y"
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (selectedDate.isEmpty())
                                "📅 Pick a Date"
                            else
                                "📅 $selectedDate"
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedDate.isNotEmpty() &&
                            selectedOutfit != null) {
                            viewModel.addEntry(
                                outfitId = selectedOutfit!!.outfit.id,
                                date     = selectedDate
                            )
                        }
                        showDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
