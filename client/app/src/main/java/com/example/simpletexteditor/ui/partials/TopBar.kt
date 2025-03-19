package com.example.simpletexteditor.ui.partials

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpletexteditor.R
import com.example.simpletexteditor.textmanager.FileHandler
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.main.OpenFilesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(globalState: GlobalState, onUndo: () -> Unit, onRedo: () -> Unit) {
    var showOpenFilesMenu by remember { mutableStateOf(false) }
    var showOptionsDropDown by remember { mutableStateOf(false) }

    var activeFileRef by remember { mutableIntStateOf(-1) }

    fun onActiveFileChanged(newIndex: Int) {
        activeFileRef = newIndex
    }

    LaunchedEffect(Unit) {
        FileHandler.activeFileChangedEvent -= ::onActiveFileChanged
        FileHandler.activeFileChangedEvent += ::onActiveFileChanged
    }

    key(activeFileRef) {
        TopAppBar(
            modifier = Modifier.safeDrawingPadding(),
            colors = TopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
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

                IconButton(onClick = onUndo) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Undo,
                        contentDescription = stringResource(R.string.acc_undo),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }

                IconButton(onClick = onRedo) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Redo,
                        contentDescription = stringResource(R.string.acc_redo),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                IconButton(onClick = { showOptionsDropDown = !showOptionsDropDown }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.acc_more_actions),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    OptionsDropDown(globalState, showOptionsDropDown, onClose = { showOptionsDropDown = false })
                }
            })
    }
}

@Composable
private fun OptionsDropDown(globalState: GlobalState, shouldShow: Boolean, onClose: () -> Unit) {
    var isShowingNewFileDialog by remember { mutableStateOf(false) }
    var isShowingAppSettings by remember { mutableStateOf(false) }
    var isShowingExportShareDialog by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = shouldShow,
        onDismissRequest = { onClose.invoke() }) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.new_file)) },
            onClick = { onClose.invoke(); isShowingNewFileDialog = true })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.export_share)) },
            onClick = { onClose.invoke(); isShowingExportShareDialog = true })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.settings)) },
            onClick = { onClose.invoke(); isShowingAppSettings = true })
    }

    if (isShowingNewFileDialog) {
        NewFileDialog(onDismissRequested = { isShowingNewFileDialog = false })
    }

    if (isShowingAppSettings) {
        AppSettingsDialog(onDismissRequested = { isShowingAppSettings = false })
    }

    if (isShowingExportShareDialog) {
        ExportSendPreview(onDismissRequested = { isShowingExportShareDialog = false })
    }
}

@Preview
@Composable
fun NewFileDialogPreview() {
    AppSettingsDialog(onDismissRequested = {})
}