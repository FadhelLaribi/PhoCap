package com.example.phocap.data.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val imageUrl: String?
) {
    companion object
}

data class GroupWithCapsules(
    @Embedded val group: GroupEntity,
    val capsules: List<CapsuleWithUris>
)