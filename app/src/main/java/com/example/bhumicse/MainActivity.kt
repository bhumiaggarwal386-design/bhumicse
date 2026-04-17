package com.example.bhumicse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BhumicseTheme {
        Greeting("Android")
    }
}