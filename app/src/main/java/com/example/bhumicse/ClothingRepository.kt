package com.example.bhumicse

import kotlinx.coroutines.flow.Flow

class ClothingRepository(private val clothingDao: ClothingDao) {

    // This will give us a list of all clothes that updates automatically
    val allClothes: List<ClothingItem> = clothingDao.getAllClothes()

    suspend fun insert(item: ClothingItem) {
        clothingDao.addClothes(item)
    }
    fun getItemsByCategory(category: String): List<ClothingItem> {
        return clothingDao.getItemsByCategory(category)
    }
}