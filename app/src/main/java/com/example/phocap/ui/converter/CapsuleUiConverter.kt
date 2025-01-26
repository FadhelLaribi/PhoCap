package com.example.phocap.ui.converter

import com.example.phocap.R
import com.example.phocap.data.model.business.Capsule
import com.example.phocap.ui.model.CapsuleUi
import com.example.phocap.utils.StringValue
import com.example.phocap.utils.TimeHelper
import javax.inject.Inject
import kotlin.math.abs

class CapsuleUiConverter @Inject constructor(private val timeHelper: TimeHelper) {

    fun convert(capsule: Capsule): CapsuleUi {
        val elapsedEpochMinutes = timeHelper.getElapsedEpochMinutesTimeFrom(
            start = capsule.unlockTime,
            end = timeHelper.utcNowEpochSecond()
        )

        val status = if (elapsedEpochMinutes > 0) CapsuleUi.Status.Unlocked else {
            val absElapsedEpochMinutes = abs(elapsedEpochMinutes).toInt()
            CapsuleUi.Status.Locked(
                when {
                    absElapsedEpochMinutes < 3 -> StringValue.StringResource(R.string.capsule_item_unlocks_very_soon)
                    absElapsedEpochMinutes < 60 -> StringValue.PluralResource(
                        R.plurals.capsule_item_unlocks_in_minutes,
                        absElapsedEpochMinutes,
                        absElapsedEpochMinutes
                    )

                    absElapsedEpochMinutes < 1440 -> StringValue.PluralResource(
                        R.plurals.capsule_item_unlocks_in_hours,
                        absElapsedEpochMinutes / 60,
                        absElapsedEpochMinutes / 60
                    )

                    absElapsedEpochMinutes < 10080 -> StringValue.PluralResource(
                        R.plurals.capsule_item_unlocks_in_days,
                        absElapsedEpochMinutes / 1440,
                        absElapsedEpochMinutes / 1440
                    )

                    absElapsedEpochMinutes < 40320 -> StringValue.PluralResource(
                        R.plurals.capsule_item_unlocks_in_weeks,
                        absElapsedEpochMinutes / 10080,
                        absElapsedEpochMinutes / 10080
                    )

                    else -> StringValue.PluralResource(
                        R.plurals.capsule_item_unlocks_in_months,
                        absElapsedEpochMinutes / 40320,
                        absElapsedEpochMinutes / 40320

                    )

                }
            )
        }
        return CapsuleUi(capsule, status)
    }
}