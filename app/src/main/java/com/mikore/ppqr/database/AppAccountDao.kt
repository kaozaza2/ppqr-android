package com.mikore.ppqr.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppAccountDao {
    @Query("SELECT * FROM account")
    fun getAccounts(): Flow<List<AppAccount>>

    @Query("SELECT * FROM account WHERE uid = :uid")
    suspend fun getAccount(uid: String?): AppAccount?

    @Query("SELECT * FROM account WHERE no = :no LIMIT 1")
    suspend fun getAccountByNo(no: String): AppAccount?

    @Upsert
    suspend fun upsert(account: AppAccount)

    @Query("DELETE FROM account WHERE uid = :uid")
    suspend fun delete(uid: String)
}