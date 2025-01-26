package com.example.phocap.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * stateInWhileSubscribed is a utility function that converts a Flow into a StateFlow. It is
 * generally used in the context of a ViewModel. The 5000L milliseconds duration is the ANR deadline.
 * It is the time the app has to complete a configuration change.
 * This deadline is thus used to avoid restarting the upstream flow during configuration changes.
 */

fun <T> Flow<T>.stateInWhileSubscribed(scope: CoroutineScope, initialValue: T) = stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(5000L),
    initialValue = initialValue
)