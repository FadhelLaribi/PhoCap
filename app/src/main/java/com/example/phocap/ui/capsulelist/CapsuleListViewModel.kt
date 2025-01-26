package com.example.phocap.ui.capsulelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phocap.R
import com.example.phocap.data.repository.CapsuleRepository
import com.example.phocap.data.repository.GroupRepository
import com.example.phocap.ui.converter.CapsuleUiConverter
import com.example.phocap.ui.model.CapsuleUi
import com.example.phocap.utils.StringValue
import com.example.phocap.utils.stateInWhileSubscribed
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CapsuleListViewModel.Factory::class)
class CapsuleListViewModel @AssistedInject constructor(
    private val capsuleRepository: CapsuleRepository,
    groupRepository: GroupRepository,
    private val converter: CapsuleUiConverter,
    @Assisted private val groupId: Int?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(groupId: Int?): CapsuleListViewModel
    }

    data class ScreenState(
        val capsules: List<CapsuleUi> = emptyList(),
        val welcomeMessage: StringValue = StringValue.Empty,
        val isRefreshing: Boolean = false
    )

    private val isRefreshing = MutableStateFlow(false)

    val screenState: StateFlow<ScreenState> = combine(
        isRefreshing,
        capsuleRepository.getCapsules(groupId = groupId, count = null),
        if (groupId != null) groupRepository.getGroup(groupId).filterNotNull() else flowOf(null)
    ) { isRefreshing, publicCapsules, group ->
        ScreenState(
            welcomeMessage = if (groupId == null) StringValue.StringResource(R.string.feed_screen_welcome_message) else
                StringValue.StringResource(
                    R.string.group_screen_welcome_message,
                    group?.name ?: StringValue.StringResource(R.string.fallback_group_name)
                ),
            capsules = publicCapsules.map { converter.convert(it) },
            isRefreshing = isRefreshing
        )
    }.stateInWhileSubscribed(viewModelScope, ScreenState())

    init {

        startStatusUpdateTask()

        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            // Simulate a refresh, would fetch new capsules from the repository in real app
            delay(1000)
            isRefreshing.value = false
        }
    }

    // In a real app, that method would not be necessary as we would be doing
    // refresh from API for updates. We can send push notification for users or groups user wants
    // to see updates immediately. If we absolutely want a regular update, we can use WorkManager
    // for updating when app in background or even killed. This was done in order to see the updates
    // as we have no API
    private fun startStatusUpdateTask() {
        viewModelScope.launch {
            while (isActive) {
                // Call the repository method to update capsule statuses
                capsuleRepository.updateCapsulesStatus()

                // Wait for 2 minutes before checking again
                delay(UPDATE_INTERVAL)
            }
        }
    }

    private companion object {
        const val UPDATE_INTERVAL = 120_000L
    }
}