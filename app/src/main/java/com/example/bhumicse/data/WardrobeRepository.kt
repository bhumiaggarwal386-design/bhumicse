package com.example.bhumicse.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import java.io.File

class WardrobeRepository(private val db: AppDatabase) {

    // ── DAOs ─────────────────────────────────
    private val clothingDao = db.clothingItemDao()
    private val outfitDao   = db.outfitDao()
    private val plannerDao  = db.plannerDao()

    // ─────────────────────────────────────────
    // CLOTHING ITEMS
    // ─────────────────────────────────────────

    fun getAllItems(): Flow<List<ClothingItemEntity>> =
        clothingDao.getAllItems()

    fun getItemsByCategory(category: String): Flow<List<ClothingItemEntity>> =
        clothingDao.getItemsByCategory(category)

    suspend fun insertItem(item: ClothingItemEntity): Long =
        clothingDao.insertItem(item)

    suspend fun deleteItem(item: ClothingItemEntity) =
        clothingDao.deleteItem(item)

    suspend fun getItemById(id: Long): ClothingItemEntity? =
        clothingDao.getItemById(id)

    // ─────────────────────────────────────────
    // OUTFITS
    // ─────────────────────────────────────────

    fun getAllOutfitsWithItems(): Flow<List<OutfitWithItems>> =
        outfitDao.getAllOutfitsWithItems()

    suspend fun getOutfitWithItems(outfitId: Long): OutfitWithItems? =
        outfitDao.getOutfitWithItems(outfitId)

    suspend fun getOutfitItemRefs(outfitId: Long): List<OutfitItemCrossRef> =
        outfitDao.getOutfitItemRefs(outfitId)

    suspend fun insertOutfit(outfit: OutfitEntity): Long =
        outfitDao.insertOutfit(outfit)

    suspend fun insertOutfitItem(crossRef: OutfitItemCrossRef) =
        outfitDao.insertOutfitItem(crossRef)

    suspend fun deleteOutfit(outfit: OutfitEntity) =
        outfitDao.deleteOutfit(outfit)

    suspend fun deleteOutfitItems(outfitId: Long) =
        outfitDao.deleteOutfitItems(outfitId)

    suspend fun updateRating(outfitId: Long, rating: Int) =
        outfitDao.updateRating(outfitId, rating)

    // ─────────────────────────────────────────
    // PLANNER
    // ─────────────────────────────────────────

    fun getAllPlannerEntries(): Flow<List<PlannerEntryEntity>> =
        plannerDao.getAllEntries()

    fun getEntriesForOutfit(outfitId: Long): Flow<List<PlannerEntryEntity>> =
        plannerDao.getEntriesForOutfit(outfitId)

    suspend fun insertPlannerEntry(entry: PlannerEntryEntity): Long =
        plannerDao.insertEntry(entry)

    suspend fun deletePlannerEntry(entry: PlannerEntryEntity) =
        plannerDao.deleteEntry(entry)

    // ─────────────────────────────────────────
    // ONE-TIME JSON → ROOM MIGRATION
    // Call this once on app start from
    // SecondScreen. It checks a flag file so
    // it never runs twice.
    // ─────────────────────────────────────────

    suspend fun migrateJsonToRoomIfNeeded(context: Context) {

        // Flag file — if it exists, migration
        // already happened, skip everything
        val flagFile = File(context.filesDir, "migration_done.flag")
        if (flagFile.exists()) return

        // ── Migrate wardrobe_items.json ───────
        migrateWardrobeItems(context)

        // ── Migrate outfits.json ──────────────
        migrateOutfits(context)

        // Write flag so this never runs again
        flagFile.createNewFile()
    }

    private suspend fun migrateWardrobeItems(context: Context) {
        val file = File(context.filesDir, "wardrobe_items.json")
        if (!file.exists()) return

        try {
            val jsonArray = JSONArray(file.readText())
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                clothingDao.insertItem(
                    ClothingItemEntity(
                        name     = obj.getString("name"),
                        category = obj.getString("category"),
                        color    = obj.getString("color"),
                        imageUri = obj.getString("imageUri")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun migrateOutfits(context: Context) {
        val file = File(context.filesDir, "outfits.json")
        if (!file.exists()) return

        try {
            val jsonArray = JSONArray(file.readText())
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                // Insert the outfit row
                val outfitId = outfitDao.insertOutfit(
                    OutfitEntity(
                        name   = obj.getString("name"),
                        rating = obj.getInt("rating")
                    )
                )

                // Insert each saved canvas item
                val itemsArray = obj.getJSONArray("items")
                for (j in 0 until itemsArray.length()) {
                    val itemObj = itemsArray.getJSONObject(j)

                    // Old system used Int itemId referencing
                    // SampleData — we skip those since
                    // SampleData drawables aren't real
                    // wardrobe items. Only migrate items
                    // that have a valid clothingItemId
                    // matching something in Room.
                    val oldItemId = itemObj.getInt("itemId").toLong()
                    val exists = clothingDao.getItemById(oldItemId)
                    if (exists != null) {
                        outfitDao.insertOutfitItem(
                            OutfitItemCrossRef(
                                outfitId       = outfitId,
                                clothingItemId = oldItemId,
                                offsetX        = itemObj.getDouble("offsetX").toFloat(),
                                offsetY        = itemObj.getDouble("offsetY").toFloat(),
                                scale          = itemObj.getDouble("scale").toFloat(),
                                rotation       = itemObj.getDouble("rotation").toFloat()
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun updateOutfitName(outfitId: Long, name: String) =
        outfitDao.updateOutfitName(outfitId, name)
}