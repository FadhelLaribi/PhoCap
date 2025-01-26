package com.example.phocap.ui.capsulelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.phocap.R
import com.example.phocap.data.model.business.Capsule
import com.example.phocap.ui.model.CapsuleUi
import com.example.phocap.ui.theme.PhoCapTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun CapsuleItem(
    modifier: Modifier = Modifier,
    capsuleUi: CapsuleUi,
    onCapsuleClicked: (Int) -> Unit
) {
    val capsule = capsuleUi.capsule
    val status = capsuleUi.status
    val isUnlocked = status is CapsuleUi.Status.Unlocked
    Card(
        modifier = modifier.clickable { onCapsuleClicked(capsule.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(200 / 158f)
        ) {
            if (isUnlocked) ImageCarousel(photoUris = capsule.photoUris)
            else AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(200 / 158f)
                    .blur(80.dp),
                model = capsule.photoUris.firstOrNull(),
                fallback = painterResource(R.drawable.capsule_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            if (status is CapsuleUi.Status.Locked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent.copy(alpha = 0.9f),
                                    Color.Transparent
                                )
                            ), shape = MaterialTheme.shapes.medium
                        )
                )
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.capsule_item_locked_content_description),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp)
                )
            }
        }

        val textBlurValue = if (isUnlocked) 0.dp else 16.dp

        capsule.title?.let {
            Text(
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp)
                    .blur(textBlurValue),
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        capsule.description?.let {
            Text(
                modifier = Modifier
                    .padding(start = 20.dp, top = 4.dp)
                    .blur(textBlurValue),
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 12.dp),
            text = if (status is CapsuleUi.Status.Locked) {
                status.message.asString()
            } else {
                stringResource(R.string.capsule_item_unlocked)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun ImageCarousel(modifier: Modifier = Modifier, photoUris: List<String>) {
    when (val size = photoUris.size) {
        0 -> Image(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(200 / 158f),
            painter = painterResource(R.drawable.capsule_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        1 -> AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(200 / 158f),
            model = photoUris.first(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            fallback = painterResource(R.drawable.capsule_placeholder)
        )

        else -> {
            val coroutineScope = rememberCoroutineScope()
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(200 / 158f)
            ) {
                val pagerState = rememberPagerState(pageCount = { size })
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = size
                ) { page ->
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(200 / 158f),
                        model = photoUris[page],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        fallback = painterResource(R.drawable.capsule_placeholder)
                    )
                }
                LazyRow(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(size) { i ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (i == pagerState.currentPage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(
                                        alpha = 0.3f
                                    ),
                                    shape = CircleShape
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            i
                                        )
                                    }
                                }
                        )
                    }
                }

                LaunchedEffect(Unit) {
                    while (isActive) {
                        delay(5000)
                        pagerState.animateScrollToPage((pagerState.currentPage + 1) % size)
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CapsuleItemPreview(modifier: Modifier = Modifier) {
    PhoCapTheme {
        CapsuleItem(
            modifier = modifier,
            capsuleUi = CapsuleUi(
                capsule = Capsule(
                    id = 1,
                    title = "capsule",
                    photoUris = listOf("https://example.com/image.jpg"),
                    unlockTime = 212343,
                    description = "This is a capsule",
                    groupId = 1
                ),
                status = CapsuleUi.Status.Unlocked
            ), onCapsuleClicked = {}
        )
    }
}
