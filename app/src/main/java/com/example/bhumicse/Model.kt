package com.example.bhumicse

// 📦 Item Categories
enum class Category {
    TOPWEAR,
    BOTTOMWEAR,
    ACCESSORY
}

// 👕 Base Item
// imageUri added for Room-based items
data class ClothingData(
    val id: Int,
    val name: String,
    val category: Category,
    val imageRes: Int,
    val imageUri: String = ""
)

// 🧱 Item placed on canvas (with transformations)
// uid used as key instead of id to support
// duplicate items on canvas
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

// 👗 Outfit — kept for reference only
// actual data lives in Room OutfitEntity
data class Outfit(
    val id: Int,
    var name: String,
    var items: MutableList<SavedCanvasItem> = mutableListOf(),
    var rating: Int = 0
)
