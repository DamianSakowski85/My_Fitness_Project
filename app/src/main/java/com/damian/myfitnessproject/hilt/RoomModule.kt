package com.damian.myfitnessproject.hilt

import android.app.Application
import androidx.room.Room
import com.damian.myfitnessproject.data.database.FitnessProjectDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application, callback: FitnessProjectDb.Callback) =
        Room.databaseBuilder(app, FitnessProjectDb::class.java, "FitnessProject.db")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()


    @Provides
    fun foodKtDao(db: FitnessProjectDb) = db.foodDao()

    @Provides
    fun mealDao(db: FitnessProjectDb) = db.mealDao()

    @Provides
    fun portionDao(db: FitnessProjectDb) = db.portionDao()

    @Provides
    fun bodyMeasurementsDao(db: FitnessProjectDb) = db.bodyMeasurementsDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope