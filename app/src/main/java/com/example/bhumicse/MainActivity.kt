//package com.example.bhumicse
//
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.bhumicse.ui.theme.BhumicseTheme
//import kotlinx.coroutines.launch
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        // --- BACKEND INITIALIZATION START ---
//        val database by lazy { WardrobeDatabase.getDatabase(this) }
//        val repository by lazy { ClothingRepository(database.clothingDao()) }
//        val viewModelFactory = ClothingViewModelFactory(repository)
//        val viewModel: ClothingViewModel = ViewModelProvider(this, viewModelFactory).get(ClothingViewModel::class.java)
//        // --- BACKEND INITIALIZATION END ---
//
//        setContent {
//            BhumicseTheme {
////                WardrobeScreen(viewModel = viewModel)
//                AppNavigation(viewModel)
//            }
//        }
//
//        // Example of how to use the database correctly
//
//        // 3. TEST: READING (Separate launch blocks for multiple collections)
//        lifecycleScope.launch {
//            viewModel.allClothes.collect { list ->
//                Log.d("WARDROBE_TEST", "Success! Total items in wardrobe: ${list.size}")
//                list.forEach { item ->
//                    Log.d("WARDROBE_TEST", "Item in DB: ${item.name} (${item.category})")
//                }
//            }
//        }
//
//        lifecycleScope.launch {
//            viewModel.getItemsByCategory("Shirts").collect { shirtList ->
//                Log.d("WARDROBE_TEST", "FILTER TEST: Found ${shirtList.size} shirts.")
//            }
//        }
//    }
//}
//
//@Composable
//fun WardrobeScreen(viewModel: ClothingViewModel) {
//    val clothesList by viewModel.allClothes.collectAsState(initial = emptyList())
//    var showDialog by remember { mutableStateOf(false) }
//
//
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(onClick = { showDialog = true }) {
//                Icon(Icons.Default.Add, contentDescription = "Add")
//            }
//        }
//    ) { padding ->
//        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
//            Text("My Digital Wardrobe", style = MaterialTheme.typography.headlineMedium)
//            LazyColumn {
//                items(clothesList) { item ->
//                    Card(
//                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                    ) {
//                        // We use a Row to put the Text on the left and Icon on the right
//                        Row(
//                            modifier = Modifier.padding(16.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Column(modifier = Modifier.weight(1f)) {
//                                Text(text = item.name, style = MaterialTheme.typography.titleLarge)
//                                Text(text = "Category: ${item.category}", color = Color.Gray)
//                                Text(text = "Color: ${item.color}", color = Color.Gray)
//                            }
//
//                            // THIS IS THE DELETE BUTTON
//                            IconButton(onClick = { viewModel.delete(item) }) {
//                                Icon(
//                                    imageVector = Icons.Default.Delete,
//                                    contentDescription = "Delete",
//                                    tint = Color.Red
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    if (showDialog) {
//        AddItemDialog(
//            onDismiss = { showDialog = false },
//            onAdd = { name, cat, color ->
//                viewModel.insert(ClothingItem(name = name, category = cat, color = color))
//                showDialog = false
//            }
//        )
//    }
//}
//
//@Composable
//fun AddItemDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
//    var name by remember { mutableStateOf("") }
//    var category by remember { mutableStateOf("") }
//    var color by remember { mutableStateOf("") }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        confirmButton = {
//            Button(onClick = { onAdd(name, category, color) }) { Text("Add") }
//        },
//        title = { Text("Add Clothing") },
//        text = {
//            Column {
//                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
//                TextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
//                TextField(value = color, onValueChange = { color = it }, label = { Text("Color") })
//            }
//        }
//    )
//}
//@Composable
//fun AppNavigation(viewModel: ClothingViewModel) {
//
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "front"
//    ) {
//
//        composable("front") {
//            Frontscreen(navController)
//        }
//
//        composable("second") {
//            SecondScreen()
//        }
//
//        composable("wardrobe") {
//            WardrobeScreen(viewModel)
//        }
//    }
//}
@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bhumicse

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bhumicse.ui.theme.BhumicseTheme
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
//    @Composable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database by lazy { WardrobeDatabase.getDatabase(this) }
        val repository by lazy { ClothingRepository(database.clothingDao()) }
        val viewModelFactory = ClothingViewModelFactory(repository)
        val viewModel: ClothingViewModel = ViewModelProvider(this, viewModelFactory)[ClothingViewModel::class.java]

        setContent {
            BhumicseTheme {
                AppNavigation(viewModel)
            }
        }


//        if (currentScreen == "Main") {
//            SecondScreenUI(onWardrobeClick = { currentScreen = "Wardrobe" })
//        } else {
//            WardrobeScreen(viewModel = viewModel)
//        }
//    }
}
}


@Composable
fun WardrobeScreen(viewModel: ClothingViewModel) {
    val context = LocalContext.current
    val clothesList by viewModel.allClothes.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    fun createNewImageUri(): Uri? {
        return try {
            val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "wardrobe")
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, "item_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            Log.e("CAMERA_DEBUG", "Failed to create URI", e)
            null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            Toast.makeText(context, "Photo captured!", Toast.LENGTH_SHORT).show()
        } else {
            tempImageUri = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val uri = createNewImageUri()
            if (uri != null) {
                tempImageUri = uri
                cameraLauncher.launch(uri)
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Shirt", "Traditional", "Dress", "Pant")
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredList = clothesList.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) || item.color.contains(searchQuery, ignoreCase = true)
        val matchesCategory = if (selectedCategory == "All") true else item.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("My Digital Wardrobe", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Search clothes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = (selectedCategory == category),
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                if (filteredList.isEmpty()) {
                    item {
                        if (clothesList.isEmpty()) EmptyWardrobeView()
                        else Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No items match your search", color = Color.Gray)
                        }
                    }
                } else {
                    items(filteredList, key = { it.id }) { item ->
                        ClothingItemRow(item, onDelete = { viewModel.delete(item) })
                    }
                }
            }
        }
    }

//    if (showDialog) {
//        AddItemDialog(
//            onDismiss = {
//                showDialog = false
//                tempImageUri = null
//            },
//            onTakePhoto = {
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                    val uri = createNewImageUri()
//                    if (uri != null) {
//                        tempImageUri = uri
//                        cameraLauncher.launch(uri)
//                    }
//                } else {
//                    permissionLauncher.launch(Manifest.permission.CAMERA)
//                }
//            },
//            capturedImageUri = tempImageUri,
//            onAdd = { name, category, color, imagePath ->
//                viewModel.insert(ClothingItem(name = name, category = category, color = color, imageUri = imagePath))
//                showDialog = false
//                tempImageUri = null
//            }
//        )
//    }
}

@Composable
fun EmptyWardrobeView() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 100.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "👗", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Your wardrobe is empty!", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Text(text = "Tap + to add your first outfit.", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
    }
}

@Composable
fun ClothingItemRow(item: ClothingItem, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Color.Red.copy(alpha = 0.8f) else Color.Transparent
            Box(modifier = Modifier.fillMaxSize().padding(vertical = 4.dp).background(color, shape = MaterialTheme.shapes.medium), contentAlignment = Alignment.CenterEnd) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.padding(end = 16.dp))
            }
        }
    ) {
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, style = MaterialTheme.typography.titleLarge)
                    Text(text = "Category: ${item.category}", color = Color.Gray)
                    Text(text = "Color: ${item.color}", color = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String?) -> Unit,
    onTakePhoto: () -> Unit,
    capturedImageUri: Uri?
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Clothing") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedButton(
                    onClick = onTakePhoto,
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, if (capturedImageUri != null) Color(0xFF4CAF50) else Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (capturedImageUri != null) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (capturedImageUri != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Photo Ready", color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodyLarge)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Gray)
                                Text("Tap to Take Photo", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onAdd(name, category, color, capturedImageUri?.toString()) }) {
                Text("Save Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
//<<<<<<< HEAD
////new main activity
//=======
@Composable
fun AppNavigation(viewModel: ClothingViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "front"
    ) {

        composable("front") {
            Frontscreen(navController)
        }

        composable("second") {
            SecondScreen()
        }

        composable("wardrobe") {
            WardrobeScreen(viewModel)
        }
    }
}
