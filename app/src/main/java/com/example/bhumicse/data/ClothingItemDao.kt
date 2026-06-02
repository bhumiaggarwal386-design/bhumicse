package com.example.bhumicse.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {

    // ── INSERT ───────────────────────────────
    // Returns the auto-generated id of the new row
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClothingItemEntity): Long

    // ── DELETE ───────────────────────────────
    @Delete
    suspend fun deleteItem(item: ClothingItemEntity)

    // ── QUERIES ──────────────────────────────

    // All items — used by WardrobeScreen
    // Flow means the UI auto-updates when data changes
    @Query("SELECT * FROM clothing_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ClothingItemEntity>>

    // Items filtered by category — used by CanvasScreen item picker
    @Query("SELECT * FROM clothing_items WHERE category = :category ORDER BY createdAt DESC")
    fun getItemsByCategory(category: String): Flow<List<ClothingItemEntity>>

    // Single item by id — used when rebuilding canvas from saved outfit
    @Query("SELECT * FROM clothing_items WHERE id = :id")
    suspend fun getItemById(id: Long): ClothingItemEntity?
}