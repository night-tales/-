package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getCharactersForProject(projectId: String): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: String): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacter(id: String)
}
