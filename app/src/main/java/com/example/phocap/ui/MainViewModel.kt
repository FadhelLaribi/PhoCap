package com.example.phocap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    data class ScreenState(val showSplashScreen: Boolean = true)

    
    interface Action {
        data object FinishSplashScreen: Action
    }

    val action = MutableSharedFlow<Action>()

    init {
        viewModelScope.launch {
            delay(SPLASH_SCREEN_DELAY)
            action.emit(Action.FinishSplashScreen)
        }
    }

    private companion object {
        const val SPLASH_SCREEN_DELAY = 2000L
    }
}