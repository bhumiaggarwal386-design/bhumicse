package com.example.bhumicse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        lifecycleScope.launch {
            // 1. Create a test item
            val testShirt = ClothingItem(
                name = "Blue Denim",
                category = "Shirts",
                color = "Blue"
            )

            // 2. TEST: ADDING (Insert)
            Log.d("WARDROBE_TEST", "Adding a shirt to the wardrobe...")
            viewModel.insert(testShirt)
        }

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
    // This connects the Database to the UI screen
    val clothesList by viewModel.allClothes.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(
                text = "My Digital Wardrobe",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(clothesList) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = item.name, style = MaterialTheme.typography.titleLarge)
                    Text(text = "Category: ${item.category}", color = Color.Gray)
                }
            }
        }
    }
}
