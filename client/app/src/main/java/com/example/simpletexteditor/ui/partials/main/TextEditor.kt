package com.example.simpletexteditor.ui.partials.main

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.getActivityOrNull
import com.example.simpletexteditor.textmanager.FileHandler
import com.example.simpletexteditor.textmanager.TextEditorViewModel
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.TopBar

@Composable
fun TextEditor(globalState: GlobalState, viewModel: TextEditorViewModel = viewModel()) {
    var activeFileRef by remember { mutableIntStateOf(-1) }

    fun onActiveFileChanged(newIndex: Int) {
        activeFileRef = newIndex
        //final save
        viewModel.pushChanges()

        viewModel.currentMemoryFile = FileHandler.getActiveFileData()?.memoryFile

        viewModel.canPushChanges = false
        viewModel.onTextChanged(
            TextFieldValue(
                text = FileHandler.getActiveFileData()?.memoryFile?.content?.toString() ?: ""
            )
        )
        viewModel.canPushChanges = true

        viewModel.refresh()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAppTerminating(x: Unit) {
        viewModel.pushChanges()
    }

    LaunchedEffect(Unit) {
        if (FileHandler.activeFileIndex == -1) {
            FileHandler.createFile("New file")
            FileHandler.activeFileIndex = FileHandler.getNumberOfFiles() - 1
        }

        MainActivity.appTerminatingEvent -= ::onAppTerminating
        MainActivity.appTerminatingEvent += ::onAppTerminating

        FileHandler.activeFileIndex = FileHandler.activeFileIndex.coerceIn(0, FileHandler.getNumberOfFiles() - 1)
        FileHandler.activeFileChangedEvent -= ::onActiveFileChanged
        FileHandler.activeFileChangedEvent += ::onActiveFileChanged

        //push the pending change
        viewModel.pushChanges()

        viewModel.canPushChanges = false
        viewModel.onTextChanged(
            TextFieldValue(
                text = FileHandler.getActiveFileData()?.memoryFile?.content?.toString() ?: ""
            )
        )
        viewModel.canPushChanges = true

        viewModel.currentMemoryFile = FileHandler.getActiveFileData()?.memoryFile
    }

    key(activeFileRef) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            TopBar(
                globalState,
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() }
            )

            BasicTextField(
                value = viewModel.textFieldValue,
                onValueChange = {
                    try {
                        viewModel.onTextChanged(it)
                    } catch (e: Exception) {
                        Log.e("DBG", e.toString())
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp, 10.dp, 0.dp)
                    .onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyUp) {
                            return@onPreviewKeyEvent false
                        }

                        if (!it.isCtrlPressed) {
                            return@onPreviewKeyEvent false
                        }

                        if (it.key == Key.Z) {
                            if (it.isShiftPressed) {
                                //Ctrl+Shift+Z is also a common redo shortcut
                                //redo()
                                viewModel.redo()
                                return@onPreviewKeyEvent true
                            }

                            viewModel.undo()
                            return@onPreviewKeyEvent true
                        } else if (it.key == Key.Y) {
                            viewModel.redo()
                            return@onPreviewKeyEvent true
                        }

                        false
                    },
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = (
                            20 *
                                    (LocalContext.current.getActivityOrNull()
                                        ?.getPreferences(Context.MODE_PRIVATE)
                                        ?.getFloat("EDITOR_TEXT_SIZE", 1f)
                                        ?: 1f)
                            ).sp
                ),
                cursorBrush = Brush.linearGradient(
                    colors = List(2) {
                        MaterialTheme.colorScheme.secondary
                        MaterialTheme.colorScheme.secondary
                    }
                )
            )


            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(10.dp, 0.dp)
            ) {
                Text(
                    "Ln: ${viewModel.currentLine}, Col: ${viewModel.currentColumn}",
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
