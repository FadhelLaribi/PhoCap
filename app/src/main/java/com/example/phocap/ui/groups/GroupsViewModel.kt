package com.example.phocap.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phocap.data.model.business.Group
import com.example.phocap.data.repository.CapsuleRepository
import com.example.phocap.data.repository.GroupRepository
import com.example.phocap.ui.converter.CapsuleUiConverter
import com.example.phocap.ui.model.CapsuleUi
import com.example.phocap.utils.stateInWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val capsuleRepository: CapsuleRepository,
    private val converter: CapsuleUiConverter
) : ViewModel() {

    data class ScreenState(
        val publicCapsules: List<CapsuleUi> = emptyList(),
        val groups: List<Group> = emptyList(),
        val isRefreshing: Boolean = false
    )

    sealed interface Action {
        data class NavigateToGroup(val groupId: Int) : Action
    }

    private val isRefreshing = MutableStateFlow(false)

    val screenState: StateFlow<ScreenState> = combine(
        groupRepository.getAllGroups(),
        // groupId null to get public capsules
        capsuleRepository.getCapsules(groupId = null, count = PREVIEW_MAXIMUM_COUNT),
        isRefreshing
    ) { groups, publicCapsules, isRefreshing ->
        ScreenState(
            publicCapsules = publicCapsules.map { converter.convert(it) },
            groups = groups,
            isRefreshing = isRefreshing
        )
    }.stateInWhileSubscribed(viewModelScope, ScreenState())

    val action = MutableSharedFlow<Action>()

    init {

        startStatusUpdateTask()

        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            groupRepository.refresh()
            isRefreshing.value = false
        }
    }

    fun onGroupClicked(groupId: Int) {
        viewModelScope.launch {
            action.emit(Action.NavigateToGroup(groupId))
        }
    }


    // In a real app, that method would not be necessary as we would be doing
    // refresh from API for updates. We can send push notification for users or groups user wants
    // to see updates immediately. If we absolutely want a regular update, we can use WorkManager
    // for updating when app in background or even killed. This was done in order to see the updates
    // as we have no API.
    private fun startStatusUpdateTask() {
        viewModelScope.launch {
            while (isActive) {
                // Call the repository method to update capsule statuses.
                capsuleRepository.updateCapsulesStatus()

                // Wait for 2 minutes before checking again
                delay(UPDATE_INTERVAL)
            }
        }
    }

    private companion object {
        const val PREVIEW_MAXIMUM_COUNT = 5
        const val UPDATE_INTERVAL = 120_000L
    }
}
