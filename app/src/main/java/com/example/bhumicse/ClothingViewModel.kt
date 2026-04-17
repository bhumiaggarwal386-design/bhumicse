package com.example.bhumicse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClothingViewModel(private val repository: ClothingRepository) : ViewModel() {

    // Logic to get all clothes as a StateFlow
    val allClothes: StateFlow<List<ClothingItem>> = repository.allClothes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getItemsByCategory(category: String): Flow<List<ClothingItem>> {
        return repository.getItemsByCategory(category)
    }

    // Logic to add a new clothing item
    fun insert(item: ClothingItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun delete(item: ClothingItem) = viewModelScope.launch {
        repository.delete(item)
    }
}