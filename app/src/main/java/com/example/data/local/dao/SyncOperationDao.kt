package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.SyncOperationEntity

@Dao
interface SyncOperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(operation: SyncOperationEntity)

    @Query("SELECT * FROM sync_operations ORDER BY createdAt ASC, id ASC LIMIT 1")
    suspend fun next(): SyncOperationEntity?

    @Query("DELETE FROM sync_operations WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE sync_operations SET attempts = attempts + 1, lastError = :error WHERE id = :id")
    suspend fun recordFailure(id: Long, error: String?)

    @Query("SELECT COUNT(*) FROM sync_operations")
    suspend fun count(): Int
}
