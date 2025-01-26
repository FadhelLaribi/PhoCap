package com.example.phocap.data.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "capsules")
data class CapsuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupId: Int?,
    val title: String?,
    val unlockTime: Long,
    val description: String?,
    val isUnlocked: Boolean = false,
    // In a real app, this would be a reference to the user id
    val ownerName: String = "Anonymous"
) {
    companion object
}

data class CapsuleWithUris(
    @Embedded val capsule: CapsuleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "capsuleId"
    )
    val uris: List<UriEntity>
) {
    companion object
}
