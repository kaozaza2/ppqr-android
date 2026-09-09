package com.mikore.ppqr.di

import android.content.Context
import com.mikore.ppqr.database.AppAccountDao
import com.mikore.ppqr.database.AppDatabase
import com.mikore.ppqr.database.AppHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.build(context)

    @Provides
    fun provideAccountDao(db: AppDatabase): AppAccountDao = db.accountDao()

    @Provides
    fun provideHistoryDao(db: AppDatabase): AppHistoryDao = db.historyDao()
}
