package com.mikore.ppqr.di

import com.mikore.ppqr.utility.PromptPayUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePromptPayUtil(): PromptPayUtil = PromptPayUtil()
}
