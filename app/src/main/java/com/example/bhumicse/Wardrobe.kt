package com.example.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bhumicse.AppHeader
import com.example.bhumicse.data.ClothingItemEntity
import com.example.bhumicse.data.WardrobeViewModel
import kotlinx.coroutines.launch
import java.io.File

// ── Main screen ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen() {

    val context   = LocalContext.current
    val app       = context.applicationContext as android.app.Application
    val viewModel = viewModel<WardrobeViewModel>(
        factory = WardrobeViewModel.factory(app)
    )

    // Migrate existing JSON data to Room on first launch
    LaunchedEffect(Unit) {
        viewModel.migrateIfNeeded()
    }

    // Collect the live list from Room
    val allItems by viewModel.allItems.collectAsStateWithLifecycle()

    val categories = listOf("Topwear", "Bottomwear", "Accessories")
    val scope      = rememberCoroutineScope()

    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var showAddDialog   by remember { mutableStateOf(false) }
    var capturedUri     by remember { mutableStateOf<Uri?>(null) }
    val sheetState      = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSourceSheet by remember { mutableStateOf(false) }

    // Preview + delete state
    var previewItem  by remember { mutableStateOf<ClothingItemEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<ClothingItemEntity?>(null) }

    // Form state
    var itemName         by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Topwear") }
    var itemColor        by remember { mutableStateOf("") }

    fun resetForm() {
        itemName         = ""
        selectedCategory = "Topwear"
        itemColor        = ""
    }

    fun createTempUri(ctx: android.content.Context): Uri {
        val file = File(ctx.filesDir, "wardrobe_${System.currentTimeMillis()}.jpg")
        file.createNewFile()
        return FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingImageUri != null) {
            capturedUri = pendingImageUri
            resetForm()
            showAddDialog = true
        } else {
            pendingImageUri = null
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val copiedFile = copyImageToAppStorage(context, uri)
            if (copiedFile != null) {
                capturedUri = Uri.fromFile(copiedFile)
                resetForm()
                showAddDialog = true
            }
        }
    }
    Scaffold(
        topBar = { AppHeader("My Wardrobe") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSourceSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            categories.forEach { category ->
                item {
                    // Filter the Room list by category
                    CategorySection(
                        category = category,
                        items    = allItems.filter { it.category == category },
                        onPreview = { previewItem = it },
                        onDelete  = { itemToDelete = it }
                    )
                }
            }
        }
    }

    // ── Source picker bottom sheet ───────────────────────────────────────────
    if (showSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSourceSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Photo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 4.dp)
                )
                OutlinedButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showSourceSheet = false
                            val uri = createTempUri(context)
                            pendingImageUri = uri
                            cameraLauncher.launch(uri)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp))
                    Text("Take a Photo", fontSize = 15.sp)
                }
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showSourceSheet = false
                            galleryLauncher.launch(arrayOf("image/*"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp))
                    Text("Choose from Gallery", fontSize = 15.sp)
                }
                TextButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showSourceSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    // ── Add item dialog ──────────────────────────────────────────────────────
    if (showAddDialog && capturedUri != null) {
        AddItemDialog(
            imageUri         = capturedUri!!,
            itemName         = itemName,
            onItemNameChange = { itemName = it },
            selectedCategory = selectedCategory,
            onCategoryChange = { selectedCategory = it },
            itemColor        = itemColor,
            onColorChange    = { itemColor = it },
            categories       = categories,
            onSave           = {
                if (itemName.isNotBlank() && itemColor.isNotBlank()) {
                    // ✅ Save to Room instead of JSON
                    viewModel.addItem(
                        name     = itemName.trim(),
                        category = selectedCategory,
                        color    = itemColor.trim(),
                        imageUri = capturedUri!!
                    )
                    showAddDialog   = false
                    capturedUri     = null
                    pendingImageUri = null
                }
            },
            onDismiss = {
                showAddDialog   = false
                capturedUri     = null
                pendingImageUri = null
            }
        )
    }

    // ── Full-screen preview dialog ───────────────────────────────────────────
    previewItem?.let { item ->
        Dialog(
            onDismissRequest = { previewItem = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    painter            = rememberAsyncImagePainter(model = Uri.parse(item.imageUri)),
                    contentDescription = item.name,
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier.fillMaxSize().align(Alignment.Center)
                )
                IconButton(
                    onClick  = { previewItem = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(item.name, color = Color.White,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${item.category}  ·  ${item.color}",
                        color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
                }
            }
        }
    }

    // ── Delete confirmation dialog ───────────────────────────────────────────
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            shape = RoundedCornerShape(16.dp),
            icon  = {
                Icon(Icons.Default.Delete, contentDescription = null,
                    tint = MaterialTheme.colorScheme.error)
            },
            title = { Text("Delete Item", fontWeight = FontWeight.Bold) },
            text  = {
                Text(
                    text      = "Remove \"${item.name}\" from your wardrobe?",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // ✅ Delete from Room instead of JSON
                        viewModel.deleteItem(item)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ── Category section ─────────────────────────────────────────────────────────
// ⛔ UNCHANGED — exact same as before
@Composable
fun CategorySection(
    category: String,
    items: List<ClothingItemEntity>,        // ← type updated to ClothingItemEntity
    onPreview: (ClothingItemEntity) -> Unit,
    onDelete: (ClothingItemEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text     = category,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color    = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = "No items yet. Tap + to add.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            val rows = items.chunked(3)
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        WardrobeItemCard(
                            item      = item,
                            modifier  = Modifier.weight(1f),
                            onPreview = { onPreview(item) },
                            onDelete  = { onDelete(item) }
                        )
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color    = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

// ── Item card ────────────────────────────────────────────────────────────────
// ⛔ UNCHANGED — only type updated
@Composable
fun WardrobeItemCard(
    item: ClothingItemEntity,               // ← type updated
    modifier: Modifier = Modifier,
    onPreview: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Image(
                    // ✅ Parse URI string back to Uri for Coil
                    painter            = rememberAsyncImagePainter(
                        model = Uri.parse(item.imageUri)
                    ),
                    contentDescription = item.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .clickable { onPreview() }
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .clickable { onPreview() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Preview",
                            tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete",
                            tint = Color(0xFFFF6B6B), modifier = Modifier.size(16.dp))
                    }
                }
            }
            Text(
                text     = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            )
            Text(
                text     = item.color,
                fontSize = 10.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}

// ── Add item dialog ──────────────────────────────────────────────────────────
// ⛔ COMPLETELY UNCHANGED
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    imageUri: Uri,
    itemName: String,
    onItemNameChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    itemColor: String,
    onColorChange: (String) -> Unit,
    categories: List<String>,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape    = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add Item", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally))
                Image(
                    painter      = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "Selected photo",
                    contentScale = ContentScale.Crop,
                    modifier     = Modifier.fillMaxWidth().height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                OutlinedTextField(value = itemName, onValueChange = onItemNameChange,
                    label = { Text("Item Name") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory, onValueChange = {},
                        readOnly = true, label = { Text("Category") },
                        trailingIcon = {
                            IconButton(onClick = { dropdownExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text    = { Text(cat) },
                                onClick = { onCategoryChange(cat); dropdownExpanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(value = itemColor, onValueChange = onColorChange,
                    label = { Text("Color") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss,
                        modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(onClick = onSave, modifier = Modifier.weight(1f),
                        enabled = itemName.isNotBlank() && itemColor.isNotBlank()
                    ) { Text("Save") }
                }
            }
        }
    }
}
private fun copyImageToAppStorage(
    context: android.content.Context,
    sourceUri: Uri
): File? {
    return try {
        val fileName = "wardrobe_${System.currentTimeMillis()}.jpg"
        val destFile = File(context.filesDir, fileName)

        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // ✅ Return the File directly — no URI
        // conversion needed. Coil loads File paths
        // perfectly without any permission issues.
        destFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
