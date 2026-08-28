package com.example

import android.app.Application
import androidx.work.Configuration
import com.example.data.sync.ProjectSyncWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HakayatApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: ProjectSyncWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
