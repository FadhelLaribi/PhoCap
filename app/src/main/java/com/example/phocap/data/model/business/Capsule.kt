package com.example.phocap.data.model.business

data class Capsule(
    val id: Int,
    // In a real app, this would be a reference to the user id
    val ownerName: String = "Anonymous",
    // In a real app, this would be a list of Urls to images
    val photoUris: List<String>,
    val unlockTime: Long,
    val title: String?,
    val description: String?,
    // For simplicity sake, we will consider that public capsules have null groupId
    val groupId: Int?,
    val isUnlocked: Boolean = false,
) {
    companion object
}
