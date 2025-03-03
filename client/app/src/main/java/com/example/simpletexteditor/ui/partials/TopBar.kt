package com.example.simpletexteditor.ui.partials

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.simpletexteditor.R
import com.example.simpletexteditor.textmanager.FileHandler
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.main.DocumentSettings
import com.example.simpletexteditor.ui.partials.main.OpenFilesManager

@Composable
fun TopBar(globalState: GlobalState) {
    var showOpenFilesMenu by remember { mutableStateOf(false) }
    var showOptionsDropDown by remember { mutableStateOf(false) }

    androidx.compose.material.TopAppBar(
        modifier = Modifier.safeDrawingPadding(),
        backgroundColor = MaterialTheme.colorScheme.primary,
        title = {
            Text(
                "File: ${FileHandler.getActiveFileData()?.name ?: "New file"}",
                modifier = Modifier.clickable(onClick = {
                    showOpenFilesMenu = !showOpenFilesMenu
                }),
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                color = MaterialTheme.colorScheme.onPrimary
            )

            if (showOpenFilesMenu) {
                OpenFilesManager(onDismissRequested = { showOpenFilesMenu = false })
            }
        },
        actions = {
            IconButton(onClick = { showOpenFilesMenu = !showOpenFilesMenu }) {
                Icon(
                    if (showOpenFilesMenu) {
                        Icons.Filled.ArrowDropUp
                    } else {
                        Icons.Filled.ArrowDropDown
                    },
                    stringResource(R.string.acc_show_open_files),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.AutoMirrored.Outlined.Undo,
                    stringResource(R.string.acc_undo),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.AutoMirrored.Outlined.Redo,
                    stringResource(R.string.acc_redo),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = { showOptionsDropDown = !showOptionsDropDown }) {
                Icon(
                    Icons.Outlined.MoreVert,
                    stringResource(R.string.acc_more_actions),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                OptionsDropDown(globalState, showOptionsDropDown, onClose = { showOptionsDropDown = false })
            }
        })
}

@Composable
private fun OptionsDropDown(globalState: GlobalState, shouldShow: Boolean, onClose: () -> Unit) {
    var isShowingDocumentSettings by remember { mutableStateOf(false) }
    var isShowingNewFileDialog by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = shouldShow,
        onDismissRequest = { onClose.invoke() }) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.new_file)) },
            onClick = { onClose.invoke(); isShowingNewFileDialog = true })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.open_file)) },
            onClick = { onClose.invoke() })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.export)) },
            onClick = { onClose.invoke() })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.document_settings)) },
            onClick = { onClose.invoke(); isShowingDocumentSettings = true })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.settings)) },
            onClick = { onClose.invoke(); globalState.navController.navigate("/settings") })
    }

    if (isShowingDocumentSettings) {
        DocumentSettings(onDismissRequested = { isShowingDocumentSettings = false })
    }
    
    if(isShowingNewFileDialog) {
        NewFileDialog(onDismissRequested = {isShowingNewFileDialog = false})
    }
}

@Composable
private fun NewFileDialog(onDismissRequested: () -> Unit) {
    var fileName by remember { mutableStateOf(FileHandler.generateNewName()) }
    var shouldSaveOnCloud by remember { mutableStateOf(false) }

    fun createFile() {
        FileHandler.createFile(fileName, shouldSaveOnCloud)
        FileHandler.activeFileIndex = FileHandler.getNumberOfOpenedFiles() - 1
    }

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
                .padding(10.dp, 10.dp)
        ) {
            Text(
                text = stringResource(R.string.create_a_new_file),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = fileName,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { fileName = it },
                    label = {
                        Text(stringResource(R.string.file_name))
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = shouldSaveOnCloud,
                            onValueChange = { shouldSaveOnCloud = it },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = shouldSaveOnCloud,
                        // null for accessibility with screen readers
                        onCheckedChange = null
                    )
                    Text(
                        text = stringResource(R.string.save_on_cloud),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.align(Alignment.End)) {
                TextButton(onClick = { onDismissRequested.invoke() }) {
                    Text(stringResource(R.string.cancel).uppercase())
                }
                TextButton(
                    onClick = { createFile(); onDismissRequested.invoke() },
                    enabled = fileName.isNotEmpty()
                ) {
                    Text(stringResource(R.string.create).uppercase())
                }
            }
        }
    }
}

@Preview
@Composable
fun NewFileDialogPreview() {
    NewFileDialog(onDismissRequested = {})
}