package com.example.bhumicse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ClothingDao {
    @Insert
    suspend fun addClothes(item: ClothingItem)

    @Query("SELECT * FROM wardrobe_table")
    fun getAllClothes(): List<ClothingItem>
}