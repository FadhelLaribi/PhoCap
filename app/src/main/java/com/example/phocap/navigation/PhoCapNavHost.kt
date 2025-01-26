package com.example.phocap.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.phocap.ui.AppBarStateHolder
import com.example.phocap.ui.FabStateHolder
import com.example.phocap.ui.SplashScreen
import com.example.phocap.ui.addcapsule.AddCapsuleScreen
import com.example.phocap.ui.capsulelist.CapsuleListScreen
import com.example.phocap.ui.groups.GroupsScreen

@Composable
fun PhoCapNavHost(
    navController: NavHostController,
    showSnackbar: (String) -> Unit,
    setAppBarState: (AppBarStateHolder) -> Unit,
    setFabState: (FabStateHolder) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Splash.route,
        modifier = modifier
    ) {
        composable(route = Splash.route) {

            LaunchedEffect(Unit) {
                setAppBarState(AppBarStateHolder(title = null, isAppBarVisible = false))
            }

            SplashScreen(onSplashFinished = {
                navController.navigate(Groups.route) {
                    popUpTo(Splash.route) { inclusive = true }
                }
            })
        }

        composable(route = Groups.route) {
            LaunchedEffect(Unit) {
                setAppBarState(AppBarStateHolder(title = context.getString(Groups.title)))
                setFabState(FabStateHolder(isVisible = true, icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                    )
                }, onClick = {
                    navController.navigate(AddCapsule.route)
                }))
            }
            GroupsScreen(
                onGoToFeedClicked = { navController.navigateToFirstLevelScreen(Feed.route) },
                navigateToGroup = { navController.navigateWithArgs(Group.route, it.toString()) })

        }

        composable(route = Feed.route) {
            LaunchedEffect(Unit) {
                setAppBarState(AppBarStateHolder(title = context.getString(Feed.title)))
                setFabState(FabStateHolder())
            }
            CapsuleListScreen(groupId = null)
        }

        composable(
            route = Group.routeWithArgs, arguments = Group.arguments
        ) {
            CapsuleListScreen(groupId = it.arguments?.getInt(Group.GROUP_ID))
        }

        composable(route = AddCapsule.route) {
            LaunchedEffect(Unit) {
                setAppBarState(AppBarStateHolder(title = context.getString(AddCapsule.title)))
                setFabState(FabStateHolder())
            }
            AddCapsuleScreen(
                onSubmitSuccess = { navController.popBackStack() },
                showSnackbar = showSnackbar
            )
        }
    }
}


fun NavController.navigateToFirstLevelScreen(route: String) {
    navigate(route) {
        popUpTo(topLevelDestinations.first().route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavHostController.navigateWithArgs(route: String, vararg args: String = emptyArray()) {
    var argsString = ""
    args.forEach { argsString += "/$it" }
    navigate(route + argsString)
}