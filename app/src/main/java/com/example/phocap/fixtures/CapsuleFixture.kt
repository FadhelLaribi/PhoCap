package com.example.phocap.fixtures

import com.example.phocap.data.model.business.Capsule
import com.example.phocap.data.model.entity.CapsuleEntity
import com.example.phocap.data.model.entity.CapsuleWithUris
import com.example.phocap.data.model.entity.UriEntity

fun Capsule.Companion.fixture(
    id: Int = 1,
    ownerName: String = "Anonymous",
    photoUris: List<String> = listOf("photo1.jpg", "photo2.jpg"),
    unlockTime: Long = System.currentTimeMillis() + 86400000, // 1 day from now
    title: String? = "Capsule Title",
    description: String? = "Capsule Description",
    groupId: Int? = null,
    isUnlocked: Boolean = false
) = Capsule(
    id = id,
    ownerName = ownerName,
    photoUris = photoUris,
    unlockTime = unlockTime,
    title = title,
    description = description,
    groupId = groupId,
    isUnlocked = isUnlocked
)

fun CapsuleEntity.Companion.fixture(
    id: Int = 1,
    ownerName: String = "Anonymous",
    unlockTime: Long = 23762398,
    title: String? = "Capsule Title",
    description: String? = "Capsule Description",
    isUnlocked: Boolean = false,
    groupId: Int? = null
) = CapsuleEntity(
    id = id,
    ownerName = ownerName,
    unlockTime = unlockTime,
    title = title,
    description = description,
    groupId = groupId,
    isUnlocked = isUnlocked
)

fun UriEntity.Companion.fixture(
    id: Int = 1,
    capsuleId: Int = 1,
    uri: String = "photo1.jpg"
) = UriEntity(
    id = id,
    capsuleId = capsuleId,
    uri = uri
)


fun CapsuleWithUris.Companion.fixture(
    capsule: CapsuleEntity = CapsuleEntity.fixture(),
    uris: List<UriEntity> = listOf(UriEntity.fixture(id = 1), UriEntity.fixture(id = 2))
) = CapsuleWithUris(
    capsule = capsule,
    uris = uris
)