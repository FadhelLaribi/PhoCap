package com.example.phocap.data.mapper

import com.example.phocap.data.model.business.Capsule
import com.example.phocap.data.model.entity.CapsuleEntity
import com.example.phocap.data.model.entity.CapsuleWithUris

fun CapsuleWithUris.toCapsule() = Capsule(
    id = capsule.id,
    ownerName = capsule.ownerName,
    photoUris = uris.map { it.uri },
    unlockTime = capsule.unlockTime,
    title = capsule.title,
    groupId = capsule.groupId,
    isUnlocked = capsule.isUnlocked,
    description = capsule.description
)

fun Capsule.toCapsuleEntity(userName: String = "Current User") = CapsuleEntity(
    id = id,
    ownerName = userName,
    unlockTime = unlockTime,
    title = title,
    groupId = null,
    isUnlocked = isUnlocked,
    description = description
)
