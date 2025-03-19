package com.example.simpletexteditor.ui.partials

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.R
import com.example.simpletexteditor.cloudmanager.CloudFileManagement
import com.example.simpletexteditor.textmanager.FileHandler
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.jvm.internal.Ref.ObjectRef

@Composable
fun NewFileDialog(onDismissRequested: () -> Unit) {
    val taskScope = rememberCoroutineScope()

    var fileName by remember { mutableStateOf(FileHandler.generateNewName()) }
    var shouldSaveOnCloud by remember { mutableStateOf(false) }
    var isInProgress by remember { mutableStateOf(false) }

    fun createFile() {
        isInProgress = true

        if (shouldSaveOnCloud) {
            taskScope.launch {
                val serverId = ObjectRef<String>()
                val error: String? = CloudFileManagement.createFile(fileName, serverId)

                if (error != null) {
                    Log.e("DBG", error)
                    Toast.makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG).show()
                }

                val uuid =
                    try {
                        UUID.fromString(serverId.element)
                    } catch (e: Exception) {
                        null
                    }
                FileHandler.createFile(fileName, error == null, uuid)

                onDismissRequested.invoke()
                isInProgress = false
                FileHandler.activeFileIndex = FileHandler.getNumberOfFiles() - 1
            }
        } else {
            FileHandler.createFile(fileName)
            FileHandler.activeFileIndex = FileHandler.getNumberOfFiles() - 1
        }
    }

    Dialog(
        onDismissRequest = {
            if (isInProgress) {
                return@Dialog
            }

            onDismissRequested.invoke()
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = true
        )
    ) {
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
                    .height(IntrinsicSize.Max)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(10.dp, 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_a_new_file),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface,
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
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = { onDismissRequested.invoke() }) {
                        Text(
                            stringResource(R.string.cancel).uppercase(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(
                        onClick = { createFile(); },
                        enabled = fileName.isNotEmpty()
                    ) {
                        Text(
                            stringResource(R.string.create).uppercase(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}