package com.comanch.valley_wind_awake

import android.content.Context
import com.comanch.valley_wind_awake.dataBase.DataControl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object DatabaseModule {

        @Singleton
        @Provides
        fun provideDatabase (@ApplicationContext context: Context): DataControl {
            return DataControl.getInstance(context)
        }

        @Singleton
        @Provides
        fun provideTimeDataDao(database: DataControl) = database.timeDatabaseDao

        @Singleton
        @Provides
        fun provideRingtoneDataDao(database: DataControl) = database.ringtoneDatabaseDao

    }
}