package com.codinginflow.inventarization.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.codinginflow.inventarization.data.AssetsFileWorker
import com.codinginflow.inventarization.data.InventarDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context): InventarDatabase =
        Room.databaseBuilder(app, InventarDatabase::class.java, "invt_database")
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val periodicWorkRequest = OneTimeWorkRequestBuilder<AssetsFileWorker>().build()
                    WorkManager.getInstance(app.applicationContext).enqueue(periodicWorkRequest)
                }
            })
            .build()
}