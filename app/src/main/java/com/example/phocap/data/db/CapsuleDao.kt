package com.example.phocap.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.phocap.data.model.entity.CapsuleEntity
import com.example.phocap.data.model.entity.CapsuleWithUris
import com.example.phocap.data.model.entity.UriEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CapsuleDao {

    @Transaction
    @Query("SELECT * FROM capsules WHERE groupId = :groupId")
    suspend fun getAll(groupId: Int): List<CapsuleWithUris>

    @Transaction
    @Query(
        """
   SELECT * FROM capsules 
    WHERE (:groupId IS NULL AND groupId IS NULL) 
    OR (:groupId IS NOT NULL AND groupId = :groupId)
    LIMIT CASE WHEN :count IS NOT NULL THEN :count ELSE 1000 END
    """
    )
    fun getCapsulesByGroupLimitCount(groupId: Int?, count: Int?): Flow<List<CapsuleWithUris>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCapsule(capsule: CapsuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCapsules(capsules: List<CapsuleEntity>)

    @Update
    suspend fun update(capsule: CapsuleEntity)

    @Update
    suspend fun update(capsule: List<CapsuleEntity>)

    @Query("SELECT * FROM capsules WHERE isUnlocked = 0 AND unlockTime <= :currentTime")
    suspend fun getLockedCapsules(currentTime: Long): List<CapsuleEntity>

    @Query("UPDATE capsules SET isUnlocked = 1 WHERE id = :id")
    suspend fun setToUnlocked(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUris(uris: List<UriEntity>)

    @Query("SELECT COUNT(*) FROM capsules WHERE id = :groupId")
    fun countCapsulesByGroup(groupId: Int): Int

    @Transaction
    suspend fun addCapsulesWithUris(capsule: CapsuleEntity, uris: List<String>) {
        val capsuleId = addCapsule(capsule).toInt()
        val uriEntities = uris.map { UriEntity(capsuleId = capsuleId, uri = it) }
        addUris(uriEntities)
    }
}