package com.damian.myfitnessproject.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun context(@ApplicationContext application: Context): Context {
        return application
    }

    @Provides
    fun filesDirProvider(@ApplicationContext application: Context): File {
        return application.filesDir
    }
}