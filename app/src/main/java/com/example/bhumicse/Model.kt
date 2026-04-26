package com.example.bhumicse

import androidx.compose.ui.graphics.graphicsLayer

// 📦 Item Categories
enum class Category {
    TOPWEAR,
    BOTTOMWEAR,
    ACCESSORY
}

// 👕 Base Item (from your drawable resources)
data class ClothingData(
    val id: Int,
    val name: String,
    val category: Category,
    val imageRes: Int
)

// 🧱 Item placed on canvas (with transformations)
data class CanvasItem(
    val item: ClothingData,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var scale: Float = 1f,
    var rotation: Float = 0f,
    val uid: String = java.util.UUID.randomUUID().toString()
)

data class SavedCanvasItem(
    val itemId: Int,
    val offsetX: Float,
    val offsetY: Float,
    val scale: Float,
    val rotation: Float
)

// 👗 Final Outfit
data class Outfit(
    val id: Int,
    var name: String,
    var items: MutableList<SavedCanvasItem> = mutableListOf(),
    var rating: Int = 0
)

// 🧪 Sample Data (your items)
object SampleData {

    val items = listOf(
        ClothingData(
            id = 1,
            name = "Top 1",
            category = Category.TOPWEAR,
            imageRes = R.drawable.top1
        ),
        ClothingData(
            id = 2,
            name = "Bottom 1",
            category = Category.BOTTOMWEAR,
            imageRes = R.drawable.bottom1
        ),
        ClothingData(
            id = 3,
            name = "Accessory 1",
            category = Category.ACCESSORY,
            imageRes = R.drawable.accessory1
        ),
        ClothingData(
            id = 4,
            name = "Accessory 2",
            category = Category.ACCESSORY,
            imageRes = R.drawable.accessory2
        )
    )

    // 🗂 Temporary Outfit Storage (in-memory)
    val outfits = mutableListOf<Outfit>()
}
