package com.example.phocap.fixtures

import com.example.phocap.data.model.entity.GroupEntity

fun GroupEntity.Companion.fixture(
    id: Int = 1,
    name: String = "Group Name",
    description: String = "Group Description",
    imageUrl: String? = "group_image.jpg"
) = GroupEntity(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl
)