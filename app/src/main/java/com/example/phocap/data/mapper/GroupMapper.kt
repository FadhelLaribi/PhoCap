package com.example.phocap.data.mapper

import com.example.phocap.data.model.business.Group
import com.example.phocap.data.model.entity.GroupEntity

fun GroupEntity.toGroup() = Group(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl
)