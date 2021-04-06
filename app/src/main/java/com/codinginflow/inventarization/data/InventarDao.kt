package com.codinginflow.inventarization.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: ArrayList<Inventar>)

    @Query("SELECT * from inventar")
    fun getAll(): Flow<List<Inventar>>

    @Query("UPDATE inventar  SET color = :color WHERE myid = :id ")
    suspend fun updateInventar(color: Int, id: String)

    @Query("UPDATE inventar  SET color = :color, userName = :userId, notes = :notes  WHERE myid = :id ")
    suspend fun updateInventar(color: Int, id: String, userId: String, notes: String)

    @Query("SELECT * FROM inventar WHERE color = 1")
    fun getScanningInventar(): Flow<List<Inventar>>

    @Query("UPDATE inventar SET color = :reset")
    suspend fun startNew(reset: Int)
}