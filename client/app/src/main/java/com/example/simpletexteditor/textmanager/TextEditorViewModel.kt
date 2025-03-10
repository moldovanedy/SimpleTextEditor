package com.example.simpletexteditor.textmanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.lifecycle.ViewModel

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

    fun refresh() {
        resetValues()
        currentLine = 1
        currentColumn = 1
    }


    fun onTextChanged(value: TextFieldValue) {
        if (value.text == "") {
            lastAbsoluteCharPosition = 0
        }

        val previousIndex = lastAbsoluteCharPosition
        val previousText = textFieldValue.text

        handleSelectionChanged(value)
        textFieldValue = value

        if (!canPushChanges) {
            return
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
        if (diff.isEmpty() || diffStartIndex < 0) {
            return
        }

        currentMemoryFile?.pushChange(
            MemoryFile.TextDiff(
                textChange = diff.toString(),
                index = diffStartIndex,
                isAdded = isAdding
            )
        )
        resetValues()
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

        //TODO: this prevents crashes, but it must be solved, so that the line counter does not remain outdated
        if (i >= cursorDiff.length) {
            lastAbsoluteCharPosition = 0
            currentLine = 1
            currentColumn = 1
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

        lastAbsoluteCharPosition = value.selection.start
    }

    private fun resetValues() {
        diff.clear()
        diffStartIndex = -1
        isAdding = true
    }
}