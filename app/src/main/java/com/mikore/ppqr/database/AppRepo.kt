package com.mikore.ppqr.database

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class AppRepo @Inject constructor(
    private val accountDao: AppAccountDao,
    private val historyDao: AppHistoryDao
) {

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }

    fun getAccounts(): Flow<List<AppAccount>> = accountDao.getAccounts()

    suspend fun getAccount(uid: String?): AppAccount? = accountDao.getAccount(uid)

    suspend fun getAccountByNo(no: String): AppAccount? = accountDao.getAccountByNo(no)

    fun getHistories(): Flow<List<AppHistory>> = historyDao.getHistories()

    suspend fun accountExists(no: String): Boolean = accountDao.getAccountByNo(no) != null

    suspend fun saveAccount(account: AppAccount): Result<Unit> = try {
        accountDao.upsert(account)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun deleteAccount(uid: String): Result<Unit> = try {
        accountDao.delete(uid)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun saveHistory(history: AppHistory): Result<Unit> = try {
        historyDao.add(history)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun deleteHistory(history: AppHistory): Result<Unit> = try {
        historyDao.delete(history)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
