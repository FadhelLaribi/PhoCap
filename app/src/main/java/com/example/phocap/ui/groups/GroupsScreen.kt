package com.example.phocap.ui.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.phocap.R
import com.example.phocap.ui.theme.PhoCapTheme
import com.example.phocap.utils.ObserveAsEvent

@Composable
fun GroupsScreen(
    onGoToFeedClicked: () -> Unit,
    navigateToGroup: (Int) -> Unit,
) {
    val viewModel = hiltViewModel<GroupsViewModel>()

    val screenState = viewModel.screenState.collectAsStateWithLifecycle()

    ObserveAsEvent(viewModel.action) {
        when (it) {
            is GroupsViewModel.Action.NavigateToGroup -> navigateToGroup(it.groupId)
        }
    }

    GroupScreenContent(
        screenState = screenState.value,
        onGoToFeedClick = onGoToFeedClicked,
        onGroupClicked = viewModel::onGroupClicked,
        onRefresh = viewModel::refresh
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupScreenContent(
    screenState: GroupsViewModel.ScreenState,
    onGroupClicked: (Int) -> Unit,
    onGoToFeedClick: () -> Unit,
    onRefresh: () -> Unit,
) {
    PullToRefreshBox(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        isRefreshing = screenState.isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Text(
                    modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 8.dp),
                    text = stringResource(R.string.groups_screen_welcome_message),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, start = 16.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.groups_screen_feed_section_title),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    TextButton(onClick = onGoToFeedClick) {
                        Text(text = stringResource(R.string.groups_screen_feed_see_all))
                    }
                }


                LazyRow(
                    modifier = Modifier.padding(top = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(screenState.publicCapsules) { capsule ->
                        CapsuleItem(capsuleUi = capsule)
                    }
                }


                Text(
                    text = stringResource(R.string.groups_screen_feed_your_groups_title),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(screenState.groups) {
                GroupCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    group = it,
                    onGroupClicked = onGroupClicked
                )
            }

            item {
                TextButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, start = 16.dp, top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                        Text(stringResource(R.string.groups_screen_feed_add_group))
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun GroupsScreenPreview() {
    PhoCapTheme {
        GroupScreenContent(
            screenState = GroupsViewModel.ScreenState(),
            onRefresh = {},
            onGroupClicked = {},
            onGoToFeedClick = {})
    }
}