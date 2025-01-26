package com.example.phocap.ui.addcapsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phocap.R
import com.example.phocap.data.model.business.Capsule
import com.example.phocap.data.repository.CapsuleRepository
import com.example.phocap.utils.StringValue
import com.example.phocap.utils.TimeHelper
import com.example.phocap.utils.stateInWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCapsuleViewModel @Inject constructor(
    private val capsuleRepository: CapsuleRepository,
    private val timeHelper: TimeHelper
) :
    ViewModel() {

    data class AddCapsuleScreenState(
        val form: Form = Form(),
        val showPicker: Boolean = false,
        val showPhotoModal: Boolean = false
    ) {
        data class Form(
            val photos: List<String> = emptyList(),
            val unlockDate: Long? = null,
            val formattedUnlockDate: String = "",
            val title: String = "",
            val description: String = "",
            val photoError: StringValue? = null,
            val dateError: StringValue? = null
        )
    }

    sealed interface Action {
        data object PickPhoto : Action
        data object CapturePhotoPermission : Action
        data class CapturePhoto(val uri: String) : Action
        data object BackToGroup : Action
    }

    val action = MutableSharedFlow<Action>()

    private val form = MutableStateFlow(AddCapsuleScreenState.Form())
    private val showDatePicker = MutableStateFlow(false)
    private val showPhotoModal = MutableStateFlow(false)

    private val photoCaptureUri = mutableListOf<String>()

    val screenState: StateFlow<AddCapsuleScreenState> = combine(
        form, showDatePicker, showPhotoModal
    ) { form, showDatePicker, showPhotoModal ->
        AddCapsuleScreenState(form, showDatePicker, showPhotoModal)

    }.stateInWhileSubscribed(viewModelScope, AddCapsuleScreenState())


    fun onAddPhotoClicked() {
        showPhotoModal.value = true
    }

    fun onAddPhotoDismissed() {
        showPhotoModal.value = false
    }

    fun onPhotoAdded() {
        form.update { state ->
            if (form.value.photos.size < 3) {
                state.copy(photos = state.photos + photoCaptureUri.last())
            } else {
                state.copy(photoError = StringValue.StringResource(R.string.add_capsule_add_photo_maximum_limit))
            }
        }
    }

    fun setUnlockDate(date: Long?) {
        showDatePicker.value = false
        if (date != null) {
            val formattedDate = timeHelper.formatDate(TimeHelper.DateFormat.DayMonthYear, date)
            form.update {
                it.copy(
                    unlockDate = date,
                    dateError = null,
                    formattedUnlockDate = formattedDate
                )
            }
        } else {
            form.update { it.copy(dateError = StringValue.StringResource(R.string.add_capsule_add_unlock_date_error)) }
        }
    }

    fun setTitle(title: String) {
        form.update { it.copy(title = title) }
    }

    fun setDescription(description: String) {
        form.update { it.copy(description = description) }
    }

    private fun isValid(): Boolean {
        val currentState = form.value

        var hasError = false
        if (currentState.photos.isEmpty()) {
            form.update { it.copy(photoError = StringValue.StringResource(R.string.add_capsule_add_photo_error)) }
            hasError = true
        }
        if (currentState.unlockDate == null) {
            form.update { it.copy(dateError = StringValue.StringResource(R.string.add_capsule_add_unlock_date_error)) }
            hasError = true
        }

        return !hasError
    }

    fun onDatePickerClicked() {
        showDatePicker.value = true
    }

    fun onDatePickerDismissed() {
        showDatePicker.value = false
    }

    fun onPickPhotoClicked() {
        viewModelScope.launch {
            action.emit(Action.PickPhoto)
        }
    }

    fun onCapturePhotoClicked() {
        viewModelScope.launch {
            action.emit(Action.CapturePhotoPermission)
        }
    }

    fun onPhotoPermissionGranted(uri: String) {
        photoCaptureUri.add(uri)
        viewModelScope.launch {
            action.emit(Action.CapturePhoto(uri))
        }
    }

    fun onFileSelected(uri: String) {
        form.update {
            it.copy(photos = it.photos.toMutableList() + uri)
        }
    }

    fun submit() {
        if (isValid()) viewModelScope.launch {
            val unlockTime = timeHelper.timeMillisToUtcEpochSeconds(form.value.unlockDate!!)
            capsuleRepository.addCapsule(
                Capsule(
                    id = 0,
                    photoUris = form.value.photos,
                    unlockTime = unlockTime,
                    title = form.value.title,
                    description = form.value.description,
                    groupId = null,
                    isUnlocked = timeHelper.utcNowEpochSecond() - unlockTime > 0
                )
            )
            action.emit(Action.BackToGroup)
        }
    }
}