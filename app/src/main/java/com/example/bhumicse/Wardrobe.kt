package com.example.app.ui.screens

import android.content.Context
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
import com.example.bhumicse.AppHeader
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlinx.coroutines.launch
import android.content.Intent

data class WardrobeItem(
    val name: String,
    val category: String,
    val color: String,
    val imageUri: Uri
)

// ── JSON persistence helpers ─────────────────────────────────────────────────

private const val WARDROBE_FILE = "wardrobe_items.json"

private fun saveItems(context: Context, items: List<WardrobeItem>) {
    val jsonArray = JSONArray()
    items.forEach { item ->
        val obj = JSONObject().apply {
            put("name", item.name)
            put("category", item.category)
            put("color", item.color)
            put("imageUri", item.imageUri.toString())
        }
        jsonArray.put(obj)
    }
    File(context.filesDir, WARDROBE_FILE).writeText(jsonArray.toString())
}

private fun loadItems(context: Context): List<WardrobeItem> {
    val file = File(context.filesDir, WARDROBE_FILE)
    if (!file.exists()) return emptyList()
    return try {
        val jsonArray = JSONArray(file.readText())
        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            WardrobeItem(
                name = obj.getString("name"),
                category = obj.getString("category"),
                color = obj.getString("color"),
                imageUri = Uri.parse(obj.getString("imageUri"))
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// ── Main screen ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen() {
    val context = LocalContext.current
    val categories = listOf("Topwear", "Bottomwear", "Accessories")
    val scope = rememberCoroutineScope()

    // Load saved items on first composition
    val items = remember { mutableStateListOf<WardrobeItem>().also { it.addAll(loadItems(context)) } }

    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSourceSheet by remember { mutableStateOf(false) }

    // Preview state
    var previewItem by remember { mutableStateOf<WardrobeItem?>(null) }

    // Delete confirm state
    var itemToDelete by remember { mutableStateOf<WardrobeItem?>(null) }

    // Form state
    var itemName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Topwear") }
    var itemColor by remember { mutableStateOf("") }

    // Helper: save whenever items change
    fun persistItems() = saveItems(context, items)

    fun resetForm() {
        itemName = ""
        selectedCategory = "Topwear"
        itemColor = ""
    }

    fun createTempUri(ctx: Context): Uri {
        val file = File(
            ctx.filesDir,
            "wardrobe_${System.currentTimeMillis()}.jpg"
        )
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
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            capturedUri = uri
            resetForm()
            showAddDialog = true
        }
    }

    Scaffold(
        topBar = {
            AppHeader("My Wardrobe")
        },
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
                    CategorySection(
                        category = category,
                        items = items.filter { it.category == category },
                        onPreview = { previewItem = it },
                        onDelete = { itemToDelete = it }
                    )
                }
            }
        }
    }

    // ── Source picker bottom sheet ──────────────────────────────────────────
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
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
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
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Choose from Gallery", fontSize = 15.sp)
                }
                TextButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { showSourceSheet = false }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    // ── Add item dialog ─────────────────────────────────────────────────────
    if (showAddDialog && capturedUri != null) {
        AddItemDialog(
            imageUri = capturedUri!!,
            itemName = itemName,
            onItemNameChange = { itemName = it },
            selectedCategory = selectedCategory,
            onCategoryChange = { selectedCategory = it },
            itemColor = itemColor,
            onColorChange = { itemColor = it },
            categories = categories,
            onSave = {
                if (itemName.isNotBlank() && itemColor.isNotBlank()) {
                    items.add(
                        WardrobeItem(
                            name = itemName.trim(),
                            category = selectedCategory,
                            color = itemColor.trim(),
                            imageUri = capturedUri!!
                        )
                    )
                    persistItems() // ← save after adding
                    showAddDialog = false
                    capturedUri = null
                    pendingImageUri = null
                }
            },
            onDismiss = {
                showAddDialog = false
                capturedUri = null
                pendingImageUri = null
            }
        )
    }

    // ── Full-screen preview dialog ──────────────────────────────────────────
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
                    painter = rememberAsyncImagePainter(model = item.imageUri),
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
                IconButton(
                    onClick = { previewItem = null },
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
                    Text(
                        text = item.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.category}  ·  ${item.color}",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // ── Delete confirmation dialog ──────────────────────────────────────────
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            shape = RoundedCornerShape(16.dp),
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Item", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Remove \"${item.name}\" from your wardrobe?",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        items.remove(item)
                        persistItems() // ← save after deleting
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ── Category section ────────────────────────────────────────────────────────
@Composable
fun CategorySection(
    category: String,
    items: List<WardrobeItem>,
    onPreview: (WardrobeItem) -> Unit,
    onDelete: (WardrobeItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = category,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
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
                    text = "No items yet. Tap + to add.",
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
                            item = item,
                            modifier = Modifier.weight(1f),
                            onPreview = { onPreview(item) },
                            onDelete = { onDelete(item) }
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
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

// ── Item card ───────────────────────────────────────────────────────────────
@Composable
fun WardrobeItemCard(
    item: WardrobeItem,
    modifier: Modifier = Modifier,
    onPreview: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
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
                    painter = rememberAsyncImagePainter(model = item.imageUri),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
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
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Preview",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            )
            Text(
                text = item.color,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}

// ── Add item dialog ─────────────────────────────────────────────────────────
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
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Item",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "Selected photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                OutlinedTextField(
                    value = itemName,
                    onValueChange = onItemNameChange,
                    label = { Text("Item Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            IconButton(onClick = { dropdownExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    onCategoryChange(cat)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = itemColor,
                    onValueChange = onColorChange,
                    label = { Text("Color") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }
                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        enabled = itemName.isNotBlank() && itemColor.isNotBlank()
                    ) { Text("Save") }
                }
            }
        }
    }
}
