package com.mikore.ppqr.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppHistoryDao {
    @Query("SELECT * FROM history ORDER BY update_at DESC")
    fun getHistories(): Flow<List<AppHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(history: AppHistory)

    @Delete
    suspend fun delete(history: AppHistory)
}
