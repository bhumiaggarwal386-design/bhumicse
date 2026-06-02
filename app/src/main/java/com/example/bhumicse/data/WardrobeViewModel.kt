package com.example.bhumicse.data

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WardrobeViewModel(application: Application) : AndroidViewModel(application) {

    // ── Setup ─────────────────────────────────
    private val repository = WardrobeRepository(
        AppDatabase.getInstance(application)
    )

    // ── All wardrobe items as StateFlow ───────
    // StateFlow means the UI always has the
    // latest list without manually reloading
    val allItems: StateFlow<List<ClothingItemEntity>> =
        repository.getAllItems()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // ── Add a new clothing item ───────────────
    fun addItem(
        name: String,
        category: String,
        color: String,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            repository.insertItem(
                ClothingItemEntity(
                    name     = name,
                    category = category,
                    color    = color,
                    imageUri = imageUri.toString()
                )
            )
        }
    }

    // ── Delete a clothing item ────────────────
    fun deleteItem(item: ClothingItemEntity) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    // ── Run JSON migration once on first launch
    fun migrateIfNeeded() {
        viewModelScope.launch {
            repository.migrateJsonToRoomIfNeeded(getApplication())
        }
    }

    // ── Factory (no Hilt, beginner friendly) ──
    companion object {
        fun factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    @Suppress("UNCHECKED_CAST")
                    return WardrobeViewModel(application) as T
                }
            }
        }
    }
}