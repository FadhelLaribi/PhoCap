package com.example.phocap.ui.addcapsule

import app.cash.turbine.test
import com.example.phocap.RuleTestSetup
import com.example.phocap.coVerifyCalled
import com.example.phocap.coVerifyNever
import com.example.phocap.data.model.business.Capsule
import com.example.phocap.data.repository.CapsuleRepository
import com.example.phocap.fixtures.fixture
import com.example.phocap.utils.TimeHelper
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AddCapsuleViewModelTest : RuleTestSetup() {

    @MockK
    private lateinit var capsuleRepository: CapsuleRepository

    @MockK
    private lateinit var timeHelper: TimeHelper

    @Test
    fun `onAddPhotoClicked updates showPhotoModal to true`() = runTest {
        // GIVEN
        val viewModel = viewModel()
        val expected = true

        // WHEN
        viewModel.onAddPhotoClicked()

        // THEN
        viewModel.screenState.test {
            assertEquals(expected, awaitItem().showPhotoModal)
        }
    }


    @Test
    fun `onAddPhotoDismissed updates showPhotoModal to false`() = runTest {
        // GIVEN
        val viewModel = viewModel()
        val expected = false

        // WHEN
        viewModel.onAddPhotoDismissed()

        // THEN
        viewModel.screenState.test {
            assertEquals(expected, awaitItem().showPhotoModal)
        }
    }

    @Test
    fun `onPhotoAdded adds a photo to the form`() = runTest {

        // GIVEN
        val viewModel = viewModel()
        val photoUri = "photo1"


        // WHEN
        viewModel.onPhotoPermissionGranted(photoUri)
        viewModel.onPhotoAdded()

        // THEN
        viewModel.screenState.test {
            assertTrue(awaitItem().form.photos.contains(photoUri))

        }
    }

    @Test
    fun `onPhotoAdded sets an error if photos exceed limit`() = runTest {

        // GIVEN
        val viewModel = viewModel()
        val photoUris = listOf("photo1", "photo2", "photo3")
        photoUris.forEach { uri ->
            viewModel.onPhotoPermissionGranted(uri)
            viewModel.onPhotoAdded()
        }

        // WHEN
        val extraPhoto = "photo4"
        viewModel.onPhotoPermissionGranted(extraPhoto)
        viewModel.onPhotoAdded()

        // THEN
        viewModel.screenState.test {
            val state = awaitItem()
            assertEquals(3, state.form.photos.size)
            assertNotNull(state.form.photoError)
        }
    }

    @Test
    fun `setUnlockDate updates unlock date and clears error`() = runTest {
        // GIVEN
        val unlockDate = 1672531200000L
        val formattedDate = "01 Jan 2023"
        every {
            timeHelper.formatDate(
                TimeHelper.DateFormat.DayMonthYear,
                unlockDate
            )
        } returns formattedDate
        val viewModel = viewModel()

        // WHEN

        viewModel.setUnlockDate(unlockDate)

        // THEN
        viewModel.screenState.test {
            val state = awaitItem()
            assertEquals(unlockDate, state.form.unlockDate)
            assertEquals(formattedDate, state.form.formattedUnlockDate)
            assertNull(state.form.dateError)
        }
    }

    @Test
    fun `setUnlockDate sets an error when date is null`() = runTest {
        // GIVEN
        val viewModel = viewModel()

        // WHEN
        viewModel.setUnlockDate(null)

        // THEN
        viewModel.screenState.test {
            val state = awaitItem()
            assertNotNull(state.form.dateError)
        }
    }

    @Test
    fun `action emits CapturePhotoPermission when onCapturePhotoClicked is called`() = runTest {
        // GIVEN
        val viewModel = viewModel()

        // WHEN
        viewModel.action.test {
            viewModel.onCapturePhotoClicked()
            assertEquals(AddCapsuleViewModel.Action.CapturePhotoPermission, awaitItem())
        }
    }

    @Test
    fun `submit calls addCapsule and emits BackToGroup`() = runTest {
        // GIVEN
        val viewModel = viewModel()

        val photos = listOf("photo1", "photo2")
        val unlockDate = 1672531200000L
        val title = "Test Capsule"
        val description = "Test Description"
        val unlockTimeEpoch = 1672531200L

        val formattedDate = "01 Jan 2023"
        val capsule = Capsule.fixture(
            id = 0,
            photoUris = photos,
            unlockTime = unlockTimeEpoch,
            title = title,
            description = description,
            groupId = null,
            isUnlocked = true
        )
        every {
            timeHelper.formatDate(
                TimeHelper.DateFormat.DayMonthYear,
                unlockDate
            )
        } returns formattedDate

        every {
            timeHelper.utcNowEpochSecond()
        } returns unlockTimeEpoch + 10

        every {
            timeHelper.timeMillisToUtcEpochSeconds(unlockDate)
        } returns unlockTimeEpoch

        coEvery {
            capsuleRepository.addCapsule(capsule)
        }.just(Runs)

        // WHEN
        photos.forEach { uri ->
            viewModel.onPhotoPermissionGranted(uri)
            viewModel.onPhotoAdded()
        }
        viewModel.setUnlockDate(unlockDate)
        viewModel.setTitle(title)
        viewModel.setDescription(description)

        // THEN

        viewModel.action.test {
            viewModel.submit()
            assertEquals(AddCapsuleViewModel.Action.BackToGroup, awaitItem())
        }

        coVerifyCalled {
            capsuleRepository.addCapsule(
                Capsule.fixture(
                    id = 0,
                    photoUris = photos,
                    unlockTime = unlockTimeEpoch,
                    title = title,
                    description = description,
                    groupId = null,
                    isUnlocked = true
                )
            )
        }
    }

    @Test
    fun `submit does not proceed if form is invalid`() = runTest {
        // GIVEN
        val viewModel = viewModel()

        viewModel.action.test {
            // WHEN
            viewModel.submit()
            // THEN
            expectNoEvents()
        }

        coVerifyNever { capsuleRepository.addCapsule(any()) }
    }

    private fun viewModel() =
        AddCapsuleViewModel(capsuleRepository = capsuleRepository, timeHelper = timeHelper)
}