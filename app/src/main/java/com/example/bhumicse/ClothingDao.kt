package com.example.bhumicse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {
    @Insert
    suspend fun addClothes(item: ClothingItem)

    @Query("SELECT * FROM wardrobe_table")
    fun getAllClothes(): Flow<List<ClothingItem>>

    @Query("SELECT * FROM wardrobe_table WHERE category = :category")
    fun getItemsByCategory(category: String): Flow<List<ClothingItem>>
}