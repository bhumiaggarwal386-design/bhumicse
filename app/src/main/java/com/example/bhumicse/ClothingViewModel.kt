package com.example.bhumicse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClothingViewModel(private val repository: ClothingRepository) : ViewModel() {

    // Logic to get all clothes
    val allClothes: List<ClothingItem> = repository.allClothes

    // Logic to add a new clothing item
    fun insert(item: ClothingItem) = viewModelScope.launch {
        repository.insert(item)
    }
}