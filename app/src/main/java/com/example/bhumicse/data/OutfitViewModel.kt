package com.example.bhumicse.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OutfitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WardrobeRepository(
        AppDatabase.getInstance(application)
    )

    // ── All outfits with manual state control ──────
    private val _allOutfits = MutableStateFlow<List<OutfitWithItems>>(emptyList())
    val allOutfits: StateFlow<List<OutfitWithItems>> = _allOutfits

    init {
        viewModelScope.launch {
            repository.getAllOutfitsWithItems().collect {
                _allOutfits.value = it
            }
        }
    }

    // ── Outfit item refs for preview ───────────────
    private val _outfitItemRefs = MutableStateFlow<List<OutfitItemCrossRef>>(emptyList())
    val outfitItemRefs: StateFlow<List<OutfitItemCrossRef>> = _outfitItemRefs

    fun loadRefsForOutfit(outfitId: Long) {
        viewModelScope.launch {
            _outfitItemRefs.value = repository.getOutfitItemRefs(outfitId)
        }
    }

    // ── Delete an outfit ───────────────────────────
    fun deleteOutfit(outfit: OutfitEntity) {
        viewModelScope.launch {
            repository.deleteOutfit(outfit)
        }
    }

    // ── Update rating ──────────────────────────────
    // Updates Room AND immediately updates local
    // state so UI recomposes right away
    fun updateRating(outfitId: Long, rating: Int) {
        viewModelScope.launch {
            repository.updateRating(outfitId, rating)

            // ✅ Immediately reflect change in UI
            _allOutfits.value = _allOutfits.value.map { outfitWithItems ->
                if (outfitWithItems.outfit.id == outfitId) {
                    outfitWithItems.copy(
                        outfit = outfitWithItems.outfit.copy(rating = rating)
                    )
                } else {
                    outfitWithItems
                }
            }
        }
    }

    // ── Factory ────────────────────────────────────
    companion object {
        fun factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    @Suppress("UNCHECKED_CAST")
                    return OutfitViewModel(application) as T
                }
            }
        }
    }
}