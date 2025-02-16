package com.example.phocap.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.phocap.R
import com.example.phocap.data.repository.GroupRepository
import com.example.phocap.navigation.Feed
import com.example.phocap.navigation.PhoCapNavHost
import com.example.phocap.navigation.navigateToFirstLevelScreen
import com.example.phocap.navigation.topLevelDestinations
import com.example.phocap.ui.theme.PhoCapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: GroupRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(2000)
            keepSplashScreen = false
        }
        enableEdgeToEdge()
        setContent {
            PhoCapApp()
        }
    }
}

@Composable
fun PhoCapApp(modifier: Modifier = Modifier) {
    PhoCapTheme {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val startDestinationTitle = stringResource(Feed.title)
        val coroutineScope = rememberCoroutineScope()

        var appBarStateHolder by remember {
            mutableStateOf(
                AppBarStateHolder(title = startDestinationTitle)
            )
        }

        var fabStateHolder by remember {
            mutableStateOf(
                FabStateHolder()
            )
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                AppBar(
                    navController = navController,
                    appBarStateHolder = appBarStateHolder
                )
            },
            bottomBar = { BottomNavigation(navController = navController) },
            floatingActionButton = { FloatingButton(fabStateHolder = fabStateHolder) },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }) { innerPadding ->
            PhoCapNavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                showSnackbar = {
                    showSnackbar(coroutineScope, snackbarHostState, it)
                },
                setAppBarState = {
                    appBarStateHolder = it
                },
                setFabState = {
                    fabStateHolder = it
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    navController: NavController,
    appBarStateHolder: AppBarStateHolder,
    modifier: Modifier = Modifier
) {
    if (appBarStateHolder.isAppBarVisible)
        TopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = appBarStateHolder.title.orEmpty(),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            navigationIcon = {
                val canNavigateBack = navController.previousBackStackEntry != null
                val isFirstLevelScreen =
                    navController.currentDestination?.route in topLevelDestinations.map { it.route }
                val showBackArrow = canNavigateBack && !isFirstLevelScreen
                if (showBackArrow) {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_content_description),
                        )
                    }
                }
            },
            actions = appBarStateHolder.actions
        )
}

@Composable
private fun BottomNavigation(navController: NavController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    if (currentDestination != null && topLevelDestinations.find { it.route == currentDestination.route } != null)
        NavigationBar(
            modifier = modifier.height(80.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            topLevelDestinations.forEach { destination ->
                val icon = requireNotNull(destination.icon) {
                    "Bottom navigation icon is null for destination: ${destination.route}"
                }
                val title = requireNotNull(destination.title) {
                    "Bottom navigation title is null for destination: ${destination.route}"
                }
                val isSelected = currentDestination.hierarchy.any { it.route == destination.route }
                NavigationBarItem(
                    selected = isSelected,
                    icon = {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = stringResource(title)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(title),
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    ),
                    onClick = { navController.navigateToFirstLevelScreen(destination.route) }
                )
            }
        }
}

@Composable
private fun FloatingButton(modifier: Modifier = Modifier, fabStateHolder: FabStateHolder) {
    if (fabStateHolder.isVisible) {
        FloatingActionButton(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            onClick = fabStateHolder.onClick
        ) {
            fabStateHolder.icon()
        }
    }
}

private fun showSnackbar(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(message = message)
    }
}
