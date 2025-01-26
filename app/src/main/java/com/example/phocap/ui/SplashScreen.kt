package com.example.phocap.ui

import android.view.Window
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.phocap.R
import com.example.phocap.ui.theme.PhoCapTheme
import kotlinx.coroutines.delay

private const val SPLASH_DELAY = 2000L

@Composable
fun SplashScreen(modifier: Modifier = Modifier, onSplashFinished: () -> Unit) {

    val window = LocalActivity.current!!.window
    val primaryColor = MaterialTheme.colorScheme.primary
    val systemInDarkTheme = isSystemInDarkTheme()
    var defaultColor: Int
    DisposableEffect(Unit) {
        defaultColor = window.statusBarColor
        setSystemBarsColor(window, primaryColor.toArgb(), false)
        onDispose {
            setSystemBarsColor(window, defaultColor, !systemInDarkTheme)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.displayLarge
            )

            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
        }

        LaunchedEffect(Unit) {
            delay(SPLASH_DELAY)
            onSplashFinished()
        }
    }
}

private fun setSystemBarsColor(window: Window, color: Int, isLight: Boolean) {
    window.statusBarColor = color
    window.navigationBarColor = color
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = isLight
        isAppearanceLightNavigationBars = isLight
    }
}

@PreviewLightDark
@Composable
private fun SplashScreenPreview() {
    PhoCapTheme {
        SplashScreen(onSplashFinished = {})
    }
}