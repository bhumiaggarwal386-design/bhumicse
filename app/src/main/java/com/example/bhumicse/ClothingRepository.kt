package com.example.bhumicse

import kotlinx.coroutines.flow.Flow

class ClothingRepository(private val clothingDao: ClothingDao) {

    // This will give us a Flow of all clothes that updates automatically
    val allClothes: Flow<List<ClothingItem>> = clothingDao.getAllClothes()

    suspend fun insert(item: ClothingItem) {
        clothingDao.addClothes(item)
    }

    fun getItemsByCategory(category: String): Flow<List<ClothingItem>> {
        return clothingDao.getItemsByCategory(category)
    }

    suspend fun delete(item: ClothingItem) {
        clothingDao.deleteClothes(item)
    }
}