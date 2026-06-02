package com.example.bhumicse.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {

    // ── INSERT ───────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: PlannerEntryEntity): Long

    // ── DELETE ───────────────────────────────
    @Delete
    suspend fun deleteEntry(entry: PlannerEntryEntity)

    // ── QUERIES ──────────────────────────────

    // All planner entries joined with their outfit
    // UI needs both the date AND the outfit name/image
    @Transaction
    @Query("SELECT * FROM planner_entries ORDER BY date ASC")
    fun getAllEntries(): Flow<List<PlannerEntryEntity>>

    // All entries for a specific outfit
    // Useful later if you want to show "this outfit is planned for X date"
    @Query("SELECT * FROM planner_entries WHERE outfitId = :outfitId")
    fun getEntriesForOutfit(outfitId: Long): Flow<List<PlannerEntryEntity>>
}