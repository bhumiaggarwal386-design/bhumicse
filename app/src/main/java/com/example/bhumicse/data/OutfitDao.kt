package com.example.bhumicse.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {

    // ── INSERT ───────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity): Long

    // Inserts one row into the join table (outfit_items)
    // Called once per clothing item when saving a canvas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitItem(crossRef: OutfitItemCrossRef)

    // ── DELETE ───────────────────────────────
    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    // Removes all items from an outfit before re-saving
    // Used when editing an existing outfit on canvas
    @Query("DELETE FROM outfit_items WHERE outfitId = :outfitId")
    suspend fun deleteOutfitItems(outfitId: Long)

    // ── UPDATE ───────────────────────────────
    @Query("UPDATE outfits SET rating = :rating WHERE id = :outfitId")
    suspend fun updateRating(outfitId: Long, rating: Int)

    @Transaction
    @Query("SELECT * FROM outfits WHERE id = :outfitId")
    suspend fun getOutfitById(outfitId: Long): OutfitWithItems?

    @Query("UPDATE outfits SET name = :name WHERE id = :outfitId")
    suspend fun updateName(outfitId: Long, name: String)
    @Query("UPDATE outfits SET name = :name WHERE id = :outfitId")
    suspend fun updateOutfitName(outfitId: Long, name: String)

    // ── QUERIES ──────────────────────────────

    // All outfits with their clothing items
    // The @Transaction makes sure outfit + items
    // are always read together atomically
    @Transaction
    @Query("SELECT * FROM outfits ORDER BY createdAt DESC")
    fun getAllOutfitsWithItems(): Flow<List<OutfitWithItems>>

    // Single outfit with items — used by canvas when editing
    @Transaction
    @Query("SELECT * FROM outfits WHERE id = :outfitId")
    suspend fun getOutfitWithItems(outfitId: Long): OutfitWithItems?

    // Get the cross ref rows for an outfit
    // Needed to restore canvas transforms when editing
    @Query("SELECT * FROM outfit_items WHERE outfitId = :outfitId")
    suspend fun getOutfitItemRefs(outfitId: Long): List<OutfitItemCrossRef>
}