package com.example.phocap.ui

import androidx.compose.runtime.Composable

data class FabStateHolder(
    val icon: @Composable () -> Unit = {},
    val onClick: () -> Unit = {},
    val isVisible: Boolean = false
)
