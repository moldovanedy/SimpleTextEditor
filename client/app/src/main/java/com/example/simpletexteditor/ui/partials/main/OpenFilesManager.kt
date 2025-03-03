package com.example.simpletexteditor.ui.partials.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.simpletexteditor.R
import com.example.simpletexteditor.textmanager.FileDetails
import com.example.simpletexteditor.textmanager.FileHandler
import com.example.simpletexteditor.ui.theme.AppTheme

@Composable
fun OpenFilesManager(onDismissRequested: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequested,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = true
        )
    ) {
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(0.dp, 10.dp)
        ) {
            Text(
                text = stringResource(R.string.open_files),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.6f)
                    .verticalScroll(rememberScrollState())
            ) {
                HorizontalDivider()

                FileHandler.getOpenedFilesRef().mapIndexed { idx, file ->
                    FileData(
                        details = file,
                        onSelected = { FileHandler.activeFileIndex = idx; onDismissRequested.invoke() },
                        isActive = idx == FileHandler.activeFileIndex
                    )
                    HorizontalDivider()
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = { onDismissRequested.invoke() }, modifier = Modifier.align(Alignment.End)) {
                Text(stringResource(R.string.cancel).uppercase())
            }
        }
    }
}

@Composable
private fun FileData(details: FileDetails, onSelected: () -> Unit, isActive: Boolean = false) {
    var isEditingName by remember { mutableStateOf(false) }
    var isOptionsDropdownOpen by remember { mutableStateOf(false) }
    var fileNameEdited by remember { mutableStateOf(details.name) }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isActive)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceContainerHighest
            )
            .clickable(onClick = { onSelected.invoke() })
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (details.areChangesSavedLocally) "" else "\u2b24",
                modifier = Modifier.widthIn(13.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            if (isEditingName) {
                TextField(
                    value = fileNameEdited,
                    onValueChange = { fileNameEdited = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { isEditingName = false }),
                    singleLine = true,
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .weight(1f)
                        .focusRequester(focusRequester),
//                        .onFocusChanged { focusState ->
//                            if (!focusState.isFocused) {
//                                isEditingName = false
//                            }
//                        }
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    trailingIcon = {
                        IconButton(onClick = { isEditingName = false }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.cancel),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            } else {
                Text(
                    text = details.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            //this will probably confuse the user otherwise
            if (!isEditingName) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.acc_delete_file),
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.align(Alignment.End)) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = if (details.isSyncedWithServer) Icons.Filled.CloudSync else Icons.Filled.CloudOff,
                    contentDescription = stringResource(R.string.acc_save_on_cloud),
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.acc_export_file),
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = { isOptionsDropdownOpen = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.acc_more_actions),
                    modifier = Modifier.size(28.dp)
                )

                FileDataOptions(
                    shouldShow = isOptionsDropdownOpen,
                    onClose = { isOptionsDropdownOpen = false },
                    onRenameRequested = { isEditingName = true }
                )
            }
        }

    }
}

@Composable
private fun FileDataOptions(
    shouldShow: Boolean,
    onClose: () -> Unit,
    onRenameRequested: () -> Unit
) {
    DropdownMenu(
        expanded = shouldShow,
        onDismissRequest = { onClose.invoke() }) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.rename)) },
            onClick = {
                onClose.invoke()
                onRenameRequested.invoke()
            })
    }
}


@Preview
@Composable
fun OpenFilesManagerPreview() {
    AppTheme {
        OpenFilesManager(onDismissRequested = {})
    }
}