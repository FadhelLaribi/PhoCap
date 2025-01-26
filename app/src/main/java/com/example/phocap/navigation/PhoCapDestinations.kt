package com.example.phocap.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.phocap.R

sealed interface PhoCapDestinations {
    val route: String

    @get:StringRes
    val title: Int?

    @get:DrawableRes
    val icon: Int?
}

data object Splash : PhoCapDestinations {
    override val route = "splash"
    override val title = null
    override val icon = null
}

data object Groups : PhoCapDestinations {
    override val route = "groups"
    override val title = R.string.groups_title
    override val icon = R.drawable.ic_group
}

data object Feed : PhoCapDestinations {
    override val route = "feed"
    override val title = R.string.feed_title
    override val icon = R.drawable.ic_feed
}

data object Group : PhoCapDestinations {
    override val route = "group"
    override val title = null
    override val icon = null

    const val GROUP_ID = "group_id"
    val routeWithArgs = "$route/{$GROUP_ID}"
    val arguments = listOf(
        navArgument(GROUP_ID) { type = NavType.IntType }
    )
}

data object AddCapsule : PhoCapDestinations {
    override val route = "add_capsule"
    override val title = R.string.add_capsule_title
    override val icon = null
}

val topLevelDestinations = listOf(Groups, Feed)
