package com.example.phocap.ui.composable

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.phocap.R
import com.example.phocap.ui.theme.PhoCapTheme

@Composable
fun DateTextField(
    value: Long,
    label: String,
    placeHolder: String,
    onDatePickerClicked: () -> Unit,
    displayableValue: String,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = displayableValue,
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text(placeHolder) },
        trailingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = stringResource(R.string.date_text_field_content_description)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(value) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        onDatePickerClicked()
                    }
                }
            }
    )
}

@PreviewLightDark
@Composable
fun DateTextFieldPreview() {
    PhoCapTheme {
        DateTextField(
            value = 1737936000,
            label = "Unlock Date",
            onDatePickerClicked = {},
            displayableValue = "11/23/2026",
            placeHolder = "MM/DD/YYYY"
        )
    }
}