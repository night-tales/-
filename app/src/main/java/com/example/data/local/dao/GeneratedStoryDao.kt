package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.GeneratedStoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedStoryDao {
    @Query("SELECT * FROM generated_stories ORDER BY createdAt DESC")
    fun getAllStories(): Flow<List<GeneratedStoryEntity>>

    @Query("SELECT * FROM generated_stories WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteStories(): Flow<List<GeneratedStoryEntity>>

    @Query("SELECT * FROM generated_stories WHERE id = :id")
    suspend fun getStoryById(id: String): GeneratedStoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: GeneratedStoryEntity)

    @Query("DELETE FROM generated_stories WHERE id = :id")
    suspend fun deleteStory(id: String)
}
