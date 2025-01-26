package com.example.phocap.ui.capsulelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CapsuleListScreen(groupId: Int?) {
    val viewModel = hiltViewModel(creationCallback = { factory: CapsuleListViewModel.Factory ->
        factory.create(groupId)
    })

    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    CapsuleListScreenContent(
        screenState = screenState,
        onRefresh = viewModel::refresh,
        onCapsuleClicked = { _ -> }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapsuleListScreenContent(
    screenState: CapsuleListViewModel.ScreenState,
    onRefresh: () -> Unit,
    onCapsuleClicked: (Int) -> Unit
) {
    PullToRefreshBox(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        isRefreshing = screenState.isRefreshing,
        onRefresh = onRefresh
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = screenState.welcomeMessage.asString(),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            items(screenState.capsules) { capsuleUi ->
                CapsuleItem(
                    capsuleUi = capsuleUi,
                    onCapsuleClicked = { onCapsuleClicked(capsuleUi.capsule.id) }
                )
            }

        }
    }
}
