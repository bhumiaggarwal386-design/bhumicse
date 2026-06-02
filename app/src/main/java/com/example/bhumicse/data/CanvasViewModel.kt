package com.example.bhumicse.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.bhumicse.CanvasItem
import com.example.bhumicse.ClothingData
import com.example.bhumicse.Category
class CanvasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WardrobeRepository(
        AppDatabase.getInstance(application)
    )

    // ── All wardrobe items for the item picker ─
    // Grouped by category exactly like before
    val allItems: StateFlow<List<ClothingItemEntity>> =
        repository.getAllItems()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // ── Save a brand new outfit ────────────────
    fun saveNewOutfit(
        name: String,
        canvasItems: List<CanvasItem>
    ) {
        viewModelScope.launch {
            // Step 1: Insert the outfit row
            val outfitId = repository.insertOutfit(
                OutfitEntity(name = name)
            )
            // Step 2: Insert each canvas item
            // with its transforms into join table
            canvasItems.forEach { canvasItem ->
                repository.insertOutfitItem(
                    OutfitItemCrossRef(
                        outfitId       = outfitId,
                        clothingItemId = canvasItem.item.id.toLong(),
                        offsetX        = canvasItem.offsetX,
                        offsetY        = canvasItem.offsetY,
                        scale          = canvasItem.scale,
                        rotation       = canvasItem.rotation
                    )
                )
            }
        }
    }

    // ── Update an existing outfit ──────────────
    // REPLACE updateOutfit entirely
    fun updateOutfit(
        outfitId: Long,
        name: String,
        canvasItems: List<CanvasItem>
    ) {
        viewModelScope.launch {
            // Step 1: Delete old items from join table only
            repository.deleteOutfitItems(outfitId)

            // ✅ Step 2: UPDATE name only — don't re-insert
            // Re-inserting with REPLACE strategy triggers
            // CASCADE delete on planner_entries!
            repository.updateOutfitName(outfitId, name)

            // Step 3: Re-insert all canvas items
            canvasItems.forEach { canvasItem ->
                repository.insertOutfitItem(
                    OutfitItemCrossRef(
                        outfitId       = outfitId,
                        clothingItemId = canvasItem.item.id.toLong(),
                        offsetX        = canvasItem.offsetX,
                        offsetY        = canvasItem.offsetY,
                        scale          = canvasItem.scale,
                        rotation       = canvasItem.rotation
                    )
                )
            }
        }
    }
    // ── Load canvas items from an existing outfit
    // Returns CanvasItem list with transforms restored
    suspend fun loadCanvasItems(
        outfitWithItems: OutfitWithItems
    ): List<CanvasItem> {

        // Get the cross refs which hold the transforms
        val refs = repository.getOutfitItemRefs(
            outfitWithItems.outfit.id
        )

        // Match each ref to its ClothingItemEntity
        return refs.mapNotNull { ref ->
            val entity = outfitWithItems.items.firstOrNull {
                it.id == ref.clothingItemId
            }
            if (entity != null) {
                CanvasItem(
                    item     = entity.toClothingData(),
                    offsetX  = ref.offsetX,
                    offsetY  = ref.offsetY,
                    scale    = ref.scale,
                    rotation = ref.rotation
                )
            } else null
        }
    }

    // ── Factory ────────────────────────────────
    companion object {
        fun factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    @Suppress("UNCHECKED_CAST")
                    return CanvasViewModel(application) as T
                }
            }
        }
    }
}
// ADD THIS AT BOTTOM OF CanvasViewModel.kt
fun ClothingItemEntity.toClothingData(): ClothingData {
    return ClothingData(
        id       = this.id.toInt(),
        name     = this.name,
        category = when (this.category.uppercase()) {
            "TOPWEAR"    -> Category.TOPWEAR
            "BOTTOMWEAR" -> Category.BOTTOMWEAR
            else         -> Category.ACCESSORY
        },
        imageRes = 0,
        imageUri = this.imageUri
    )
}