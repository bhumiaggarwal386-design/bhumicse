package com.example.bhumicse.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// ─────────────────────────────────────────────
// This is NOT a database table.
// Room uses this to automatically JOIN
// outfits + outfit_items + clothing_items
// in a single query.
// ─────────────────────────────────────────────

data class OutfitWithItems(
    @Embedded
    val outfit: OutfitEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = OutfitItemCrossRef::class,
            parentColumn = "outfitId",
            entityColumn = "clothingItemId"
        )
    )
    val items: List<ClothingItemEntity>
)