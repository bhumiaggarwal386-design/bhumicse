package com.example.bhumicse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.bhumicse.ui.theme.BhumicseTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- BACKEND INITIALIZATION START ---
        val database by lazy { WardrobeDatabase.getDatabase(this) }
        val repository by lazy { ClothingRepository(database.clothingDao()) }
        val viewModelFactory = ClothingViewModelFactory(repository)
        val viewModel: ClothingViewModel = ViewModelProvider(this, viewModelFactory).get(ClothingViewModel::class.java)
        // --- BACKEND INITIALIZATION END ---

        setContent {
            BhumicseTheme {
                WardrobeScreen(viewModel = viewModel)
            }
        }

        // Example of how to use the database correctly

        // 3. TEST: READING (Separate launch blocks for multiple collections)
        lifecycleScope.launch {
            viewModel.allClothes.collect { list ->
                Log.d("WARDROBE_TEST", "Success! Total items in wardrobe: ${list.size}")
                list.forEach { item ->
                    Log.d("WARDROBE_TEST", "Item in DB: ${item.name} (${item.category})")
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getItemsByCategory("Shirts").collect { shirtList ->
                Log.d("WARDROBE_TEST", "FILTER TEST: Found ${shirtList.size} shirts.")
            }
        }
    }
}

@Composable
fun WardrobeScreen(viewModel: ClothingViewModel) {
    val clothesList by viewModel.allClothes.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("My Digital Wardrobe", style = MaterialTheme.typography.headlineMedium)
            LazyColumn {
                items(clothesList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        // We use a Row to put the Text on the left and Icon on the right
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, style = MaterialTheme.typography.titleLarge)
                                Text(text = "Category: ${item.category}", color = Color.Gray)
                                Text(text = "Color: ${item.color}", color = Color.Gray)
                            }

                            // THIS IS THE DELETE BUTTON
                            IconButton(onClick = { viewModel.delete(item) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddItemDialog(
            onDismiss = { showDialog = false },
            onAdd = { name, cat, color ->
                viewModel.insert(ClothingItem(name = name, category = cat, color = color))
                showDialog = false
            }
        )
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onAdd(name, category, color) }) { Text("Add") }
        },
        title = { Text("Add Clothing") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                TextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                TextField(value = color, onValueChange = { color = it }, label = { Text("Color") })
            }
        }
    )
}
