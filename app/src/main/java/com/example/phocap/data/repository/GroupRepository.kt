package com.example.phocap.data.repository

import com.example.phocap.data.model.business.Group
import com.example.phocap.data.model.entity.GroupWithCapsules
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getAllGroups(): Flow<List<Group>>
    fun getGroup(id: Int): Flow<Group?>
    suspend fun refresh()
    suspend fun getGroupWithCapsules(groupId: Int): GroupWithCapsules?
}
