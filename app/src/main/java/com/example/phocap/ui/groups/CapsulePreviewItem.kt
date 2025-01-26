package com.example.phocap.ui.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.phocap.R
import com.example.phocap.data.model.business.Capsule
import com.example.phocap.ui.model.CapsuleUi
import com.example.phocap.ui.theme.PhoCapTheme

@Composable
fun CapsuleItem(
    capsuleUi: CapsuleUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .size(220.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
    ) {
        val capsule = capsuleUi.capsule
        val isUnlocked = capsuleUi.status is CapsuleUi.Status.Unlocked
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = capsule.ownerName,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)

                )

                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = capsule.ownerName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                val firstImage = capsule.photoUris.firstOrNull()
                if (firstImage == null)
                    Image(
                        painter = painterResource(R.drawable.capsule_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium)
                            .blur(if (isUnlocked) 0.dp else 32.dp),
                        contentScale = ContentScale.Crop
                    ) else
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = firstImage,
                        ),
                        contentDescription = null,

                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium)
                            .blur(if (!capsule.isUnlocked) 32.dp else 0.dp),
                        contentScale = ContentScale.Crop
                    )

                if (!isUnlocked) {
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
                            .size(40.dp)
                    )
                }
            }

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = if (capsuleUi.status is CapsuleUi.Status.Locked) {
                    capsuleUi.status.message.asString()
                } else {
                    stringResource(R.string.capsule_item_unlocked)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

        }
    }
}

@PreviewLightDark
@Composable
fun CapsuleItemPreview() {
    PhoCapTheme {
        CapsuleItem(
            capsuleUi = CapsuleUi(
                capsule = Capsule(
                    id = 1,
                    title = "capsule",
                    photoUris = listOf("https://example.com/image.jpg"),
                    unlockTime = 1234567890,
                    description = "This is a capsule",
                    groupId = 1
                ),
                status = CapsuleUi.Status.Unlocked
            )
        )
    }
}