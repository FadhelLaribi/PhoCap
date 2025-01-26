package com.example.phocap.ui.addcapsule

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.phocap.R
import com.example.phocap.ui.theme.PhoCapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhotoModal(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(),
    onTakePhotoClicked: () -> Unit,
    onUploadClicked: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModalItem(
                onClick = {
                    onDismissRequest()
                    onTakePhotoClicked()
                },
                painter = painterResource(R.drawable.ic_camera),
                text = stringResource(R.string.edit_photo_modal_camera_title)
            )

            ModalItem(
                onClick = {
                    onDismissRequest()
                    onUploadClicked()
                },
                painter = painterResource(R.drawable.ic_file),
                text = stringResource(R.string.edit_photo_modal_upload_title)
            )
        }
    }
}

@Composable
private fun ModalItem(
    onClick: () -> Unit,
    painter: Painter,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun EditPhotoModalPreview() {
    PhoCapTheme {
        AddPhotoModal(
            onDismissRequest = {},
            sheetState = SheetState(
                initialValue = SheetValue.Expanded,
                skipPartiallyExpanded = false,
                density = LocalDensity.current
            ),
            onTakePhotoClicked = {},
            onUploadClicked = {})
    }
}
