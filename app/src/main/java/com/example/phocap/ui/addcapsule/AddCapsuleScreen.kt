package com.example.phocap.ui.addcapsule

import android.Manifest
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.phocap.R
import com.example.phocap.ui.composable.DateTextField
import com.example.phocap.ui.theme.PhoCapTheme
import com.example.phocap.utils.ObserveAsEvent
import com.example.phocap.utils.TimeHelper
import com.example.phocap.utils.createImageFile
import com.example.phocap.utils.getUriForFile
import logcat.LogPriority
import logcat.logcat

private const val LOG_TAG = "AddCapsuleScreen"

@Composable
fun AddCapsuleScreen(onSubmitSuccess: () -> Unit, showSnackbar: (String) -> Unit) {
    val viewModel = hiltViewModel<AddCapsuleViewModel>()
    val screenState = viewModel.screenState.collectAsStateWithLifecycle().value

    val context = LocalContext.current

    val mediaPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.onFileSelected(uri.toString())
            } else {
                logcat(LOG_TAG, LogPriority.WARN) { "No media selected" }
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success)
                viewModel.onPhotoAdded()
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // As we don't have an API to get image urls, I save the image to the cache
                // get the uris and store them in the database to be able to find them once
                // the user stop and then starts the app later. As an enhancement, we can check the
                // files that we created but then weren't added to a capsule because the creation
                // process stopped was interrupted and delete them.
                // It would be querying all uris in the cacheDir and delete those that aren't in DB.
                // In a real app, it would be simpler by having the url directly from API
                val tempFile = context.createImageFile()
                val uri = context.getUriForFile(tempFile)
                viewModel.onPhotoPermissionGranted(uri.toString())
            }
        })

    ObserveAsEvent(viewModel.action) {
        when (it) {
            AddCapsuleViewModel.Action.PickPhoto -> pickImage(mediaPicker)
            is AddCapsuleViewModel.Action.CapturePhotoPermission -> permissionLauncher.launch(
                Manifest.permission.CAMERA
            )

            is AddCapsuleViewModel.Action.CapturePhoto -> cameraLauncher.launch(it.uri.toUri())
            AddCapsuleViewModel.Action.BackToGroup -> {
                onSubmitSuccess()
                showSnackbar(context.getString(R.string.capsule_successfully_added_message))
            }
        }
    }

    AddCapsuleScreenContent(
        state = screenState,
        onAddPhotoClick = viewModel::onAddPhotoClicked,
        onAddPhotoDismissed = viewModel::onAddPhotoDismissed,
        onDatePicked = { viewModel.setUnlockDate(it) },
        onTitleChange = { viewModel.setTitle(it) },
        onDescriptionChange = { viewModel.setDescription(it) },
        onDatePickerClick = viewModel::onDatePickerClicked,
        onDatePickerDismissed = viewModel::onDatePickerDismissed,
        onTakePhotoClick = viewModel::onCapturePhotoClicked,
        onUploadPhotoClick = viewModel::onPickPhotoClicked,
        onSubmit = viewModel::submit,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCapsuleScreenContent(
    state: AddCapsuleViewModel.AddCapsuleScreenState,
    onAddPhotoClick: () -> Unit,
    onDatePicked: (Long?) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDatePickerClick: () -> Unit,
    onDatePickerDismissed: () -> Unit,
    onAddPhotoDismissed: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onUploadPhotoClick: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            stringResource(R.string.add_capsule_add_photo_label),
            style = MaterialTheme.typography.bodySmall
        )

        val form = state.form

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            form.photos.forEach { photoUri ->
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )
            }

            if (form.photos.size < 3) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .clickable { onAddPhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_capsule_add_photo_content_description)
                    )
                }
            }
        }

        form.photoError?.let {
            Text(
                text = it.asString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }


        Text(
            stringResource(R.string.add_capsule_unlock_date_field_label),
            style = MaterialTheme.typography.bodySmall
        )

        DateTextField(
            displayableValue = form.formattedUnlockDate,
            label = stringResource(R.string.add_capsule_unlock_date_field_placeholder),
            value = form.unlockDate ?: 0,
            placeHolder = TimeHelper.DateFormat.DayMonthYear.value,
            onDatePickerClicked = onDatePickerClick,
        )

        if (state.showPicker)
            DatePickerField(onDateSelected = onDatePicked, onDismiss = onDatePickerDismissed)

        form.dateError?.let {
            Text(
                text = it.asString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        TextField(
            value = form.title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.add_capsule_title_field_label)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = form.description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.add_capsule_description_field_label)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // For the sake of simplicity, all added capsules are public.
        // I didn't include the possibility of either choosing a group or making them public.

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_capsule_description_submit_button_label))
        }

        if (state.showPhotoModal)
            AddPhotoModal(
                onDismissRequest = onAddPhotoDismissed,
                onTakePhotoClicked = onTakePhotoClick,
                onUploadClicked = onUploadPhotoClick
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    modifier: Modifier = Modifier, onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    // Material 3 for compose does not provide a time picker. To keep things simpler, I only added
    // date choice. Choosing the current date will create an unlocked capsule.
    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun pickImage(picker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
    val mimeType = "image/*"
    picker.launch(
        PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.SingleMimeType(
                mimeType
            )
        )
    )
}


@PreviewLightDark
@Composable
private fun AddCapsuleScreenPreview() {
    PhoCapTheme {
        AddCapsuleScreenContent(
            state = AddCapsuleViewModel.AddCapsuleScreenState(),
            onAddPhotoClick = {},
            onDatePicked = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onSubmit = {},
            onDatePickerClick = {},
            onDatePickerDismissed = {},
            onAddPhotoDismissed = {},
            onTakePhotoClick = {},
            onUploadPhotoClick = {}
        )
    }
}