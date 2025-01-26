package com.example.phocap.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "uris",
    foreignKeys = [ForeignKey(
        entity = CapsuleEntity::class,
        parentColumns = ["id"],
        childColumns = ["capsuleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["capsuleId"])]
)
data class UriEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val capsuleId: Int,
    val uri: String
) {
    companion object
}
