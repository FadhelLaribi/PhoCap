package com.example.phocap.data.repository

import com.example.phocap.data.model.business.Capsule
import kotlinx.coroutines.flow.Flow

interface CapsuleRepository {
    fun getCapsules(groupId: Int?, count: Int?): Flow<List<Capsule>>
    suspend fun addCapsule(capsule: Capsule)
    suspend fun updateCapsulesStatus()
}
