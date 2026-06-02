package com.example.bhumicse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ClothingItemEntity::class,
        OutfitEntity::class,
        OutfitItemCrossRef::class,
        PlannerEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Room auto-generates these implementations
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun outfitDao(): OutfitDao
    abstract fun plannerDao(): PlannerDao

    // ─────────────────────────────────────────
    // Singleton — ensures only ONE database
    // instance exists across the whole app
    // ─────────────────────────────────────────
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wardrobe_database"     // name of the .db file on device
                )
                    .fallbackToDestructiveMigration() // during development only —
                    // if you change entities,
                    // it rebuilds the db cleanly
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}