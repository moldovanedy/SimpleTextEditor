package com.example.simpletexteditor.ui.partials.main

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.R
import com.example.simpletexteditor.cloudmanager.CloudFileManagement
import com.example.simpletexteditor.textmanager.FileDetails
import com.example.simpletexteditor.textmanager.FileHandler
import com.example.simpletexteditor.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.jvm.internal.Ref.ObjectRef

@Composable
fun OpenFilesManager(onDismissRequested: () -> Unit) {
    val taskScope = rememberCoroutineScope()

    var refresher by remember { mutableIntStateOf(0) }
    var isInProgress by remember { mutableStateOf(false) }

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
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isInProgress) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(0.4f)
                        .background(Color(0x40_00_00_00))
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(0.6f)
                        .verticalScroll(rememberScrollState())
                ) {
                    HorizontalDivider()

                    key(refresher) {
                        FileHandler.getFileListRef().mapIndexed { idx, file ->
                            FileData(
                                details = file,
                                onSelected = {
                                    taskScope.launch {
                                        val details = FileHandler.getActiveFileData() ?: return@launch

                                        if (details.isStoredOnCloud) {
                                            isInProgress = true

                                            val error: String? =
                                                CloudFileManagement.fullyUpdateFile(
                                                    details.id.toString(),
                                                    details.memoryFile.content.toString()
                                                )

                                            if (error != null) {
                                                Toast
                                                    .makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG)
                                                    .show()
                                            }

                                            isInProgress = false
                                        }

                                        FileHandler.activeFileIndex = idx
                                        onDismissRequested.invoke()
                                    }
                                },
                                onDataModified = {
                                    //if the deleted file was the last one
                                    if (FileHandler.activeFileIndex >= FileHandler.getNumberOfFiles()) {
                                        FileHandler.activeFileIndex = 0
                                    }

                                    refresher++
                                },
                                isActive = idx == FileHandler.activeFileIndex
                            )
                            HorizontalDivider()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                TextButton(onClick = { onDismissRequested.invoke() }, modifier = Modifier.align(Alignment.End)) {
                    Text(
                        stringResource(R.string.cancel).uppercase(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun FileData(
    details: FileDetails,
    onSelected: () -> Unit,
    onDataModified: () -> Unit,
    isActive: Boolean = false
) {
    var isEditingName by remember { mutableStateOf(false) }
    var isOptionsDropdownOpen by remember { mutableStateOf(false) }
    var isCloudDropdownOpen by remember { mutableStateOf(false) }
    var fileNameEdited by remember { mutableStateOf(details.name) }

    var isDeleteConfirmationOpen by remember { mutableStateOf(false) }
    var isInProgress by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val taskScope = rememberCoroutineScope()

    fun onCloudAction() {
        if (details.isStoredOnCloud) {
            taskScope.launch {
                try {
                    val error = CloudFileManagement.deleteFile(details.id.toString())
                    if (error != null) {
                        Toast.makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    details.isStoredOnCloud = false
                } catch (e: Exception) {
                    Log.e("DBG", e.toString())
                    Toast
                        .makeText(
                            MainActivity.getContext(),
                            MainActivity.getActivity()?.getString(R.string.unknown_error),
                            Toast.LENGTH_LONG
                        ).show()
                } finally {
                    isCloudDropdownOpen = false
                }
            }
        } else {
            val serverId = ObjectRef<String>()
            taskScope.launch {
                try {
                    var error = CloudFileManagement.createFile(details.name, serverId)
                    if (error != null) {
                        Toast.makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    FileHandler.resetFileID(details, UUID.fromString(serverId.element))

                    error = CloudFileManagement.fullyUpdateFile(
                        details.id.toString(),
                        FileHandler.getFileContent(details.id)
                    )
                    if (error != null) {
                        Toast.makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    details.isStoredOnCloud = true
                    FileHandler.saveToStorage()
                } catch (e: Exception) {
                    Log.e("DBG", e.toString())
                    Toast
                        .makeText(
                            MainActivity.getContext(),
                            MainActivity.getActivity()?.getString(R.string.unknown_error),
                            Toast.LENGTH_LONG
                        ).show()
                } finally {
                    isCloudDropdownOpen = false
                }
            }
        }
    }

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
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.widthIn(13.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            if (isEditingName) {
                TextField(
                    value = fileNameEdited,
                    onValueChange = { fileNameEdited = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions =
                    KeyboardActions(
                        onDone = {
                            isEditingName = false

                            if (FileHandler.getFileListRef().find { it.name == fileNameEdited } != null) {
                                Toast.makeText(
                                    MainActivity.getContext(),
                                    MainActivity.getActivity()?.getString(R.string.file_name_already_exists),
                                    Toast.LENGTH_LONG
                                ).show()
                                return@KeyboardActions
                            }

                            isInProgress = true

                            taskScope.launch {
                                if (details.isStoredOnCloud) {
                                    val error: String? = CloudFileManagement.deleteFile(details.id.toString())

                                    if (error != null) {
                                        Toast
                                            .makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }

                                details.name = fileNameEdited
                                FileHandler.saveToStorage()

                                onDataModified.invoke()
                                isInProgress = false
                            }
                        }
                    ),
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
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            //this will probably confuse the user otherwise
            if (!isEditingName) {
                IconButton(onClick = { isDeleteConfirmationOpen = true }) {
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
            IconButton(onClick = { isCloudDropdownOpen = true }) {
                //if stored on cloud
                Icon(
                    imageVector = if (details.isStoredOnCloud) Icons.Filled.Cloud else Icons.Filled.CloudOff,
                    contentDescription = stringResource(R.string.acc_save_on_cloud),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )

                DropdownMenu(
                    expanded = isCloudDropdownOpen,
                    onDismissRequest = { isCloudDropdownOpen = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector =
                                    if (details.isStoredOnCloud)
                                        Icons.Filled.Delete
                                    else
                                        Icons.Filled.CloudUpload,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    stringResource(
                                        if (details.isStoredOnCloud)
                                            R.string.delete_from_cloud
                                        else
                                            R.string.save_on_cloud
                                    )
                                )
                            }
                        },
                        onClick = { onCloudAction() }
                    )
                }
            }

            IconButton(onClick = { isOptionsDropdownOpen = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.acc_more_actions),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )

                FileDataOptions(
                    shouldShow = isOptionsDropdownOpen,
                    onClose = { isOptionsDropdownOpen = false },
                    onRenameRequested = { isEditingName = true }
                )
            }
        }

        if (isDeleteConfirmationOpen) {
            AlertDialog(
                title = { Text(stringResource(R.string.confirm_file_delete_title)) },
                text = { Text(stringResource(R.string.confirm_file_delete_text)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isInProgress = true
                            //remove the file from local storage
                            FileHandler.deleteLocalFile(details.id)

                            taskScope.launch {
                                if (details.isStoredOnCloud) {
                                    val error: String? = CloudFileManagement.deleteFile(details.id.toString())

                                    if (error != null) {
                                        Toast.makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG).show()
                                    }
                                }

                                FileHandler.saveToStorage()
                                isInProgress = false
                                isDeleteConfirmationOpen = false
                                onDataModified.invoke()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.delete).uppercase())
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isDeleteConfirmationOpen = false }) {
                        Text(stringResource(R.string.cancel).uppercase())
                    }
                },
                onDismissRequest = { isDeleteConfirmationOpen = false }
            )
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
            text = {
                Text(
                    stringResource(R.string.rename),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
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