package com.example.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.remote.FirestoreGeneratedStoryDataSource
import com.example.data.remote.FirestoreProjectChildrenDataSource
import com.example.data.remote.RemoteProjectDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectSyncWorkerFactory @Inject constructor(
    private val database: AppDatabase,
    private val remote: RemoteProjectDataSource,
    private val childrenRemote: FirestoreProjectChildrenDataSource,
    private val storiesRemote: FirestoreGeneratedStoryDataSource
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? =
        if (workerClassName == ProjectSyncWorker::class.java.name) {
            ProjectSyncWorker(
                appContext,
                workerParameters,
                database,
                remote,
                childrenRemote,
                storiesRemote
            )
        } else null
}
