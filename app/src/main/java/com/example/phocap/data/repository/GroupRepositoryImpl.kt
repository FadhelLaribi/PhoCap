package com.example.phocap.data.repository

import com.example.phocap.data.db.CapsuleDao
import com.example.phocap.data.db.GroupDao
import com.example.phocap.data.mapper.toGroup
import com.example.phocap.data.model.business.Group
import com.example.phocap.data.model.entity.CapsuleEntity
import com.example.phocap.data.model.entity.GroupEntity
import com.example.phocap.data.model.entity.GroupWithCapsules
import com.example.phocap.di.IoDispatcher
import com.example.phocap.utils.TimeHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

// As parameters for the repository in a real app,
// we would have a local data source encapsulating the room dao and
// a remote data source encapsulating the api service. For the sake of simplicity, they were omitted.

class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val capsuleDao: CapsuleDao,
    private val timeHelper: TimeHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupRepository {

    override fun getAllGroups() =
        groupDao.getAll().map { it.map { groupEntity -> groupEntity.toGroup() } }

    override fun getGroup(id: Int): Flow<Group?> = groupDao.getFlow(id).map { it?.toGroup() }

    override suspend fun refresh() {
        withContext(ioDispatcher) {
            // Simulate delay
            delay(1000)
            val isEmpty = groupDao.countGroups() == 0
            if (isEmpty) {
                val initialGroups = listOf(
                    GroupEntity(
                        name = "Friends",
                        description = "Group for friends",
                        imageUrl = null
                    ),
                    GroupEntity(name = "Family", description = "Group for family", imageUrl = null)
                )
                groupDao.insertAll(initialGroups)

                val initialCapsules = listOf(
                    CapsuleEntity(
                        groupId = 1,
                        title = "Roma, la città eterna",
                        unlockTime = timeHelper.utcNowEpochSecond(),
                        description = "An unforgettable trip to one of the greatest cities !",
                        isUnlocked = true,
                    ),
                    CapsuleEntity(
                        groupId = 1,
                        title = "Fête de fin d'année \uD83C\uDF93",
                        unlockTime = timeHelper.utcNowEpochSecond(),
                        description = "Freshly graduated !!",
                        isUnlocked = true,
                    ),
                    CapsuleEntity(
                        groupId = 2,
                        title = "Camping à Arcachon",
                        unlockTime = timeHelper.toUtcEpochSecond(
                            timeHelper.utcNow().plusMinutes(5)
                        ),
                        description = "This is a locked capsule"
                    ),
                    CapsuleEntity(
                        groupId = null,
                        title = "Capsule 3",
                        unlockTime = timeHelper.toUtcEpochSecond(
                            timeHelper.utcNow().plusMinutes(1)
                        ),
                        description = "This is a locked public capsule"
                    ),
                    CapsuleEntity(
                        groupId = null,
                        title = "Anniversaire !",
                        unlockTime = timeHelper.toUtcEpochSecond(
                            timeHelper.utcNow()
                        ),
                        description = "This is an unlocked public capsule",
                        isUnlocked = true,
                    ),
                    CapsuleEntity(
                        groupId = null,
                        title = "Secret !",
                        unlockTime = timeHelper.toUtcEpochSecond(
                            timeHelper.utcNow().plusMinutes(8)
                        ),
                        description = "Secret capsule !",
                    ),
                )
                capsuleDao.addCapsules(initialCapsules)
            }
        }
    }

    override suspend fun getGroupWithCapsules(groupId: Int): GroupWithCapsules? {
        return withContext(ioDispatcher) {
            val group = groupDao.get(groupId) ?: return@withContext null
            val capsulesWithUris = capsuleDao.getAll(groupId)
            GroupWithCapsules(group, capsulesWithUris)
        }
    }
}