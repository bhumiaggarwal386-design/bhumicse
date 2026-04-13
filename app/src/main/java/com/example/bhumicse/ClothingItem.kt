package com.example.bhumicse

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wardrobe_table")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // like "Indian Wear", "Western", etc.
    val color: String
)