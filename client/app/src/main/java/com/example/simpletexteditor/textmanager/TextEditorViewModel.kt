package com.example.simpletexteditor.textmanager

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.lifecycle.ViewModel
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.cloudmanager.CloudFileManagement
import com.example.simpletexteditor.cloudmanager.dtos.file.FileDiffDto
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread
import kotlin.math.min

class TextEditorViewModel : ViewModel() {
    var currentLine by mutableIntStateOf(1)
        private set
    var currentColumn by mutableIntStateOf(1)
        private set
    var textFieldValue by mutableStateOf(TextFieldValue(""))
        private set

    var currentMemoryFile: MemoryFile? = null
        set(value) {
            pushChanges()
            field = value
        }
    var canPushChanges: Boolean = true

    private var lastAbsoluteCharPosition by mutableIntStateOf(0)
    private var diff: StringBuilder = StringBuilder("")
    private var diffStartIndex: Int = -1
    private var isAdding: Boolean = true

    private var timer = Timer()

    fun refresh() {
        resetValues()
        currentLine = 1
        currentColumn = 1
    }


    fun onTextChanged(value: TextFieldValue) {
        timer.cancel()
        timer = Timer()
        timer.schedule(getTimerCompletedTask(), 1000)

        val previousIndex = lastAbsoluteCharPosition
        val previousText = textFieldValue.text
        val previousSelection = textFieldValue.selection

        if (value.text == "") {
            lastAbsoluteCharPosition = 0
        }

        try {
            handleSelectionChanged(value)
        } catch (_: Exception) {
            resetLineCounter()
        }

        textFieldValue = value

        if (!canPushChanges) {
            return
        }

        //when only the selection changes
        if (previousText == value.text) {
            //if there were no changes, this will not actually push changes
            pushChanges()
            lastAbsoluteCharPosition = min(value.selection.start, value.selection.end)
            return
        }

        //the text is different and the selection was spanning multiple characters, it means those characters were removed
        if (previousSelection.length > 0) {
            isAdding = false
            diff.clear()

            val start: Int
            val end: Int
            if (previousSelection.start <= previousSelection.end) {
                start = previousSelection.start
                end = previousSelection.end
            } else {
                start = previousSelection.end
                end = previousSelection.start
            }

            diff.append(previousText.substring(start, end))
            diffStartIndex = start
            pushChanges()
        }

        if (previousIndex <= lastAbsoluteCharPosition) {
            if (!isAdding) {
                pushChanges()
                isAdding = true
            }

            diff.append(textFieldValue.text.substring(previousIndex, lastAbsoluteCharPosition))

            if (diffStartIndex == -1) {
                diffStartIndex = previousIndex
            }
        } else {
            if (isAdding) {
                pushChanges()
                isAdding = false
            }

            diff.insert(0, previousText.substring(lastAbsoluteCharPosition, previousIndex))
            diffStartIndex = lastAbsoluteCharPosition
        }
    }

    fun pushChanges() {
        timer.cancel()
        timer = Timer()

        if (diff.isEmpty() || diffStartIndex < 0) {
            return
        }

        val diff =
            MemoryFile.TextDiff(
                textChange = diff.toString(),
                index = diffStartIndex,
                isAdded = isAdding
            )

        currentMemoryFile?.pushChange(diff)
        resetValues()

        val id = FileHandler.getActiveFileData()?.id ?: return
        FileHandler.modifyFileContents(id, diff)

        if (FileHandler.getActiveFileData()?.isStoredOnCloud == true) {
            thread {
                runBlocking {
                    val error: String? =
                        CloudFileManagement.updateFileByDiff(
                            FileHandler.getActiveFileData()?.id.toString(),
                            FileDiffDto(diff.textChange, diff.isAdded, diff.index)
                        )

                    if (error != null) {
                        MainActivity.getActivity()?.runOnUiThread {
                            Toast.makeText(MainActivity.getContext(), error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    fun undo() {
        //push the pending change
        pushChanges()

        val wereChangesMade: Boolean = currentMemoryFile?.undoChange() ?: false
        if (!wereChangesMade) {
            return
        }

        val thisText = currentMemoryFile?.content?.toString() ?: ""

        canPushChanges = false
        onTextChanged(
            TextFieldValue(
                text = thisText,
                selection = TextRange(if (lastAbsoluteCharPosition < thisText.length) lastAbsoluteCharPosition else 0)
            )
        )
        canPushChanges = true
    }

    fun redo() {
        val wereChangesMade: Boolean = currentMemoryFile?.redoChange() ?: false
        if (!wereChangesMade) {
            return
        }

        val thisText = currentMemoryFile?.content?.toString() ?: ""

        canPushChanges = false
        onTextChanged(
            TextFieldValue(
                text = thisText,
                selection = TextRange(if (lastAbsoluteCharPosition < thisText.length) lastAbsoluteCharPosition else 0)
            )
        )
        canPushChanges = true
    }

    private fun handleSelectionChanged(value: TextFieldValue) {
        if (value.selection.start == lastAbsoluteCharPosition) {
            return
        }

        val isGoingForward: Boolean = value.selection.start > lastAbsoluteCharPosition
        val cursorDiff = if (isGoingForward) {
            value.getTextBeforeSelection(value.selection.start - lastAbsoluteCharPosition).text
        } else {
            //TODO: fix this (it returns an empty string when cursor is at the end)
            //value.getTextAfterSelection(lastAbsoluteCharPosition - value.selection.start).text
            textFieldValue.text.substring(value.selection.start, lastAbsoluteCharPosition)
        }

        var i = if (isGoingForward) 0 else lastAbsoluteCharPosition - value.selection.start - 1
        val limit = if (isGoingForward) cursorDiff.length else -1
        var isIndeterminate = false

        if (i >= cursorDiff.length) {
            resetLineCounter()
            return
        }

        //goes from one end to the other, direction is specified by isGoingForward
        while (i != limit) {
            if (cursorDiff[i] == '\n') {
                if (isGoingForward) {
                    currentLine++
                    currentColumn = 1

                    i++
                } else {
                    currentLine--
                    //we don't know the number of characters on this row, so it's indeterminate
                    isIndeterminate = true
                    currentColumn = Int.MAX_VALUE
                    i--
                }
                continue
            }

            if (isGoingForward) {
                currentColumn++
                i++
            } else {
                currentColumn--
                i--
            }
        }

        if (isIndeterminate) {
            var count = 0

            if (value.text[value.selection.start] == '\n' && value.selection.start >= 1) {
                count = 1

                //edge case (if this is false false): the row is has a single newline (empty)
                if (value.text[value.selection.start - 1] != '\n') {
                    //now in absolute coordinates
                    i = value.selection.start - 1
                    while (i >= 0) {
                        if (value.text[i] == '\n') {
                            break
                        }

                        i--
                        count++
                    }
                }
            } else {
                //now in absolute coordinates
                i = value.selection.start
                while (i >= 0) {
                    if (value.text[i] == '\n') {
                        break
                    }

                    i--
                    count++
                }
            }

            currentColumn = count
        }

        lastAbsoluteCharPosition = min(value.selection.start, value.selection.end)
    }

    private fun resetLineCounter() {
        currentLine = 1
        currentColumn = 1

        var i = 0
        while (i < textFieldValue.selection.start) {
            if (textFieldValue.text[i] == '\n') {
                currentLine++
                currentColumn = 1
            }

            currentColumn++
            i++
        }
    }

    private fun getTimerCompletedTask() = object : TimerTask() {
        override fun run() {
            pushChanges()
        }
    }

    private fun resetValues() {
        diff.clear()
        diffStartIndex = -1
        isAdding = true
    }
}