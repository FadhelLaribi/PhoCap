package com.example.phocap.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class AppBarStateHolder(
    val title: String?,
    val isAppBarVisible: Boolean = true,
    val actions: @Composable RowScope.() -> Unit = {}
)
