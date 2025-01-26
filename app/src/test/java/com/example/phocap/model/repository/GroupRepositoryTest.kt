package com.example.phocap.model.repository

import com.example.phocap.RuleTestSetup
import com.example.phocap.coVerifyCalled
import com.example.phocap.data.db.CapsuleDao
import com.example.phocap.data.db.GroupDao
import com.example.phocap.data.mapper.toGroup
import com.example.phocap.data.model.entity.CapsuleEntity
import com.example.phocap.data.model.entity.CapsuleWithUris
import com.example.phocap.data.model.entity.GroupEntity
import com.example.phocap.data.repository.GroupRepository
import com.example.phocap.data.repository.GroupRepositoryImpl
import com.example.phocap.fixtures.fixture
import com.example.phocap.utils.TimeHelper
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupRepositoryTest : RuleTestSetup() {

    @MockK
    private lateinit var groupDao: GroupDao

    @MockK
    private lateinit var capsuleDao: CapsuleDao

    @MockK
    private lateinit var timeHelper: TimeHelper


    @Test
    fun `getAllGroups will return the list of groups from the dao mapped to Group`() = runTest {
        // GIVEN
        val groupEntities = listOf(
            GroupEntity.fixture(id = 1, name = "Group1"),
            GroupEntity.fixture(id = 2, name = "Group 2")
        )
        every { groupDao.getAll() } returns flowOf(groupEntities)
        val groups = groupEntities.map { it.toGroup() }
        val repository = repository()

        // WHEN
        val result = repository.getAllGroups().first()

        // THEN
        assertEquals(groups, result)
    }

    @Test
    fun `getGroup will return the group from the dao mapped to Group`() = runTest {
        // GIVEN
        val groupEntity = GroupEntity.fixture(id = 1, name = "Group1")

        coEvery { groupDao.getFlow(1) } returns flowOf(groupEntity)

        val group = groupEntity.toGroup()
        val repository = repository()

        // WHEN
        val result = repository.getGroup(1).first()

        // THEN
        assertEquals(group, result)
    }

    @Test
    fun `getGroupWithCapsules will return the group with its capsules`() = runTest {
        // GIVEN
        val groupId = 1
        val groupEntity = GroupEntity.fixture(id = groupId, name = "Group1")
        val capsules = listOf(
            CapsuleWithUris.fixture(capsule = CapsuleEntity.fixture(id = 1)),
        )
        coEvery { groupDao.get(groupId) } returns groupEntity
        coEvery { capsuleDao.getAll(groupId) } returns capsules

        val repository = repository()

        // WHEN
        val result = repository.getGroupWithCapsules(groupId)

        // THEN
        assertEquals(capsules, result?.capsules)
    }

    @Test
    fun `refresh will insert mock data in the db if no data is available`() = runTest {
        // GIVEN
        coEvery { groupDao.countGroups() } returns 0

        val repository = repository()
        coEvery { groupDao.insertAll(any()) }.just(Runs)
        coEvery { capsuleDao.addCapsules(any()) }.just(Runs)
        every { timeHelper.timeMillisToUtcEpochSeconds(any()) } returns 0
        every { timeHelper.utcNowEpochSecond() } returns 0
        every { timeHelper.utcNow() } returns LocalDateTime.MIN
        every { timeHelper.toUtcEpochSecond(any()) } returns 0

        // WHEN
        repository.refresh()

        // THEN
        coVerifyCalled { capsuleDao.addCapsules(any()) }
        coVerifyCalled { groupDao.insertAll(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun repository(): GroupRepository = GroupRepositoryImpl(
        groupDao = groupDao,
        ioDispatcher = UnconfinedTestDispatcher(),
        timeHelper = timeHelper,
        capsuleDao = capsuleDao
    )
}