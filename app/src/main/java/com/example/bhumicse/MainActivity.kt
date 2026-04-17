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

/** * BACKEND HANDOVER NOTES:
 * 1. To save an item: Call 'viewModel.insert(item)'
 * 2. To get the list of clothes: Use 'viewModel.allClothes'
 * 3. Image Handling: Save the photo URI as a String in 'imageUri'.
 */


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

        // Example of how to use the database (removing the broken placeholder code)
        lifecycleScope.launch {
            viewModel.insert(
                ClothingItem(
                    name = "Shirt",
                    category = "Casual",
                    color = "Blue"
                )
            )
            Log.d("DB_TEST", "Item inserted successfully")
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