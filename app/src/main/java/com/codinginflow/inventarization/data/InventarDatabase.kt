package com.codinginflow.inventarization.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Inventar::class],
    version = 1,
    exportSchema = false
)
abstract class InventarDatabase : RoomDatabase() {

    abstract fun newsArticleDao(): InventarDao

}