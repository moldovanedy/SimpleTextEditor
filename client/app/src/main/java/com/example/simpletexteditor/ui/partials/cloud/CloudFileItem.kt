package com.example.simpletexteditor.ui.partials.cloud

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.R
import com.example.simpletexteditor.cloudmanager.CloudFileManagement
import com.example.simpletexteditor.cloudmanager.dtos.file.FileDetailsDto
import com.example.simpletexteditor.textmanager.FileHandler
import com.example.simpletexteditor.textmanager.MemoryFile
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@Composable
fun CloudFileItem(globalState: GlobalState?, data: FileDetailsDto, onDataModified: () -> Unit) {
    val dateFormatter = SimpleDateFormat.getDateTimeInstance()
    val taskScope = rememberCoroutineScope()

    var isOptionsMenuOpened by remember { mutableStateOf(false) }
    var isLoadingData by remember { mutableStateOf(false) }

    fun onOpen() {
        var fileIdx = -1
        FileHandler.getFileListRef().forEachIndexed { index, fileDetails ->
            if (fileDetails.id.toString() == data.id) {
                fileIdx = index
                return@forEachIndexed
            }
        }

        if (fileIdx != -1) {
            FileHandler.activeFileIndex = fileIdx
            //the only way to get to the cloud page is from the edit page, so going back is ok
            globalState?.navController?.popBackStack()
            return
        }

        taskScope.launch {
            val response: Pair<Boolean, String> =
                CloudFileManagement.getFileContents(FileHandler.getActiveFileData()?.id.toString())

            //on failure
            if (!response.first) {
                Toast.makeText(MainActivity.getContext(), response.second, Toast.LENGTH_LONG).show()
                //the only way to get to the cloud page is from the edit page, so going back is ok
                globalState?.navController?.popBackStack()
                return@launch
            }

            isLoadingData = true

            try {
                val uuid = UUID.fromString(data.id)
                FileHandler.createFile(data.name, true, uuid)
                FileHandler.activeFileIndex = FileHandler.getNumberOfFiles() - 1

                FileHandler.modifyFileContents(
                    uuid,
                    MemoryFile.TextDiff(textChange = response.second)
                )
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                Toast
                    .makeText(
                        MainActivity.getContext(),
                        MainActivity.getActivity()?.getString(R.string.unknown_error),
                        Toast.LENGTH_LONG
                    ).show()
            } finally {
                //the only way to get to the cloud page is from the edit page, so going back is ok
                globalState?.navController?.popBackStack()
            }
        }
    }

    if (isLoadingData) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 10.dp)
                .clickable(onClick = { onOpen() })
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = data.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    softWrap = true,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { isOptionsMenuOpened = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )

                    OptionsDropdown(
                        isVisible = isOptionsMenuOpened,
                        onDismissRequested = { isOptionsMenuOpened = false },
                        fileId = data.id,
                        onDataModified = onDataModified
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Text(
                    text = stringResource(R.string.file_size),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = data.size.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = true
                )
            }

            Row {
                Text(
                    text = stringResource(R.string.file_date_created),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateFormatter.format(Date(data.dateCreated * 1000L)),
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = true
                )
            }

            Row {
                Text(
                    text = stringResource(R.string.file_date_modified),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateFormatter.format(Date(data.dateModified * 1000L)),
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = true
                )
            }
        }
    }
}

@Composable
private fun OptionsDropdown(
    fileId: String,
    isVisible: Boolean,
    onDismissRequested: () -> Unit,
    onDataModified: () -> Unit
) {
    val taskScope = rememberCoroutineScope()

    var isDeleteConfirmationOpen by remember { mutableStateOf(false) }
    var isInProgress by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = isVisible,
        onDismissRequest = onDismissRequested
    ) {
        if (isInProgress) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
            DropdownMenuItem(
                text = { Text(stringResource(R.string.edit)) },
                leadingIcon = { Icon(Icons.Filled.EditNote, contentDescription = null) },
                onClick = {}
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.rename)) },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                onClick = {}
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    isDeleteConfirmationOpen = true
                }
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

                        taskScope.launch {
                            val error: String? = CloudFileManagement.deleteFile(fileId)

                            if (error != null) {
                                Toast.makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG).show()
                            }

                            try {
                                val uuid = UUID.fromString(fileId)
                                FileHandler.deleteLocalFile(uuid)
                            } catch (e: Exception) {
                                Log.e("DBG", e.toString())
                            }

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

@Preview
@Composable
fun CloudFileItemPreview() {
    AppTheme {
        CloudFileItem(
            null,
            FileDetailsDto(
                id = "",
                name = "file.txt",
                size = 12,
                dateModified = 342342,
                dateCreated = 3235235
            ),
            onDataModified = {}
        )
    }
}