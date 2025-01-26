package com.example.phocap.data.repository

import com.example.phocap.data.db.CapsuleDao
import com.example.phocap.data.mapper.toCapsule
import com.example.phocap.data.mapper.toCapsuleEntity
import com.example.phocap.data.model.business.Capsule
import com.example.phocap.di.IoDispatcher
import com.example.phocap.utils.TimeHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

// As parameters for the repository in a real app,
// we would have a local data source encapsulating the room dao and
// a remote data source encapsulating the api service. For the sake of simplicity, they were omitted.

class CapsuleRepositoryImpl @Inject constructor(
    private val capsuleDao: CapsuleDao,
    private val timeHelper: TimeHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CapsuleRepository {

    override fun getCapsules(groupId: Int?, count: Int?): Flow<List<Capsule>> {
        return capsuleDao.getCapsulesByGroupLimitCount(groupId = groupId, count = count)
            .map { capsulesWithUris ->
                capsulesWithUris.map { it.toCapsule() }
            }
    }

    override suspend fun addCapsule(capsule: Capsule) {
        withContext(ioDispatcher) {
            capsuleDao.addCapsulesWithUris(
                capsule = capsule.toCapsuleEntity(),
                uris = capsule.photoUris
            )
        }
    }

    override suspend fun updateCapsulesStatus() {
        withContext(ioDispatcher) {
            val now = timeHelper.utcNowEpochSecond()
            val capsulesToUpdate = capsuleDao.getLockedCapsules(now)
            if (capsulesToUpdate.isNotEmpty()) {
                val updatedCapsules = capsulesToUpdate.map { capsule ->
                    capsule.copy(isUnlocked = now - capsule.unlockTime > 0)
                }
                capsuleDao.update(updatedCapsules)
            }
        }
    }
}