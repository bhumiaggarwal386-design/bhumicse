package com.example.bhumicse.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bhumicse.ClothingData
import com.example.bhumicse.Category



// ─────────────────────────────────────────────
// TABLE 1: clothing_items
// Replaces both WardrobeItem and ClothingData
// imageUri stores either a real photo URI string
// or a drawable resource ID string during transition
// ─────────────────────────────────────────────

@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,   // "Topwear", "Bottomwear", "Accessory"
    val color: String = "",
    val imageUri: String,   // Uri.toString() for real photos
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// TABLE 2: outfits
// Replaces the Outfit data class
// ─────────────────────────────────────────────

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val rating: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// TABLE 3: outfit_items (join table)
// Replaces SavedCanvasItem
// Stores the many-to-many relationship between
// outfits and clothing items PLUS canvas transforms
// ─────────────────────────────────────────────

@Entity(
    tableName = "outfit_items",
    primaryKeys = ["outfitId", "clothingItemId"],
    foreignKeys = [
        ForeignKey(
            entity = OutfitEntity::class,
            parentColumns = ["id"],
            childColumns = ["outfitId"],
            onDelete = ForeignKey.CASCADE  // delete outfit → its items auto-deleted
        ),
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["clothingItemId"],
            onDelete = ForeignKey.CASCADE  // delete clothing item → removed from outfits
        )
    ],
    indices = [
        Index("outfitId"),
        Index("clothingItemId")
    ]
)
data class OutfitItemCrossRef(
    val outfitId: Long,
    val clothingItemId: Long,
    // ── Canvas transforms (preserved exactly) ──
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f
)

// ─────────────────────────────────────────────
// TABLE 4: planner_entries
// Replaces OutfitPlan — now links to a real outfitId
// ─────────────────────────────────────────────

@Entity(
    tableName = "planner_entries",
    foreignKeys = [
        ForeignKey(
            entity = OutfitEntity::class,
            parentColumns = ["id"],
            childColumns = ["outfitId"],
            onDelete = ForeignKey.CASCADE  // delete outfit → planner entry removed too
        )
    ],
    indices = [Index("outfitId")]
)
data class PlannerEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val outfitId: Long,
    val date: String,       // keeping "dd/mm/yyyy" string to match your existing UI
    val note: String = ""
)
// ─────────────────────────────────────────────
// Extension: converts ClothingItemEntity to
// ClothingData so CanvasItem keeps working
// without any changes to canvas gesture logic
// ─────────────────────────────────────────────
