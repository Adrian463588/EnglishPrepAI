package com.englishprep.ai

import android.app.Application
import com.englishprep.ai.data.room.AssetSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class EnglishPrepApp : Application() {
    @Inject
    lateinit var assetSeeder: AssetSeeder

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            assetSeeder.seedIfNeeded()
        }
    }
}
