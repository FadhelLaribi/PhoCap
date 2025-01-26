package com.example.phocap.ui.model

import com.example.phocap.data.model.business.Capsule
import com.example.phocap.utils.StringValue

data class CapsuleUi(
    val capsule: Capsule,
    val status: Status
) {

    sealed interface Status {
        data object Unlocked : Status
        data class Locked(val message: StringValue) : Status
    }

    companion object
}
