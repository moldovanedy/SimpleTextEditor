package com.example.simpletexteditor.ui.partials.main

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextAfterSelection
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextEditor(text: String) {
    var currentLine by remember { mutableIntStateOf(1) }
    var currentColumn by remember { mutableIntStateOf(1) }

    var state by remember { mutableStateOf(TextFieldValue(text)) }
    var lastAbsoluteCharPosition by remember { mutableIntStateOf(0) }

    fun handleSelectionChanged(value: TextFieldValue) {
        if (value.selection.start == lastAbsoluteCharPosition) {
            return
        }

        val isGoingForward: Boolean = value.selection.start > lastAbsoluteCharPosition
        val cursorDiff = if (isGoingForward) {
            value.getTextBeforeSelection(value.selection.start - lastAbsoluteCharPosition).text
        } else {
            value.getTextAfterSelection(lastAbsoluteCharPosition - value.selection.start).text
        }

        var i = if (isGoingForward) 0 else lastAbsoluteCharPosition - value.selection.start - 1
        val limit = if (isGoingForward) cursorDiff.length else -1
        var isIndeterminate = false

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

            //now in absolute coordinates
            i = value.selection.start
            while (i >= 0) {
                if (value.text[i] == '\n') {
                    break
                }

                i--
                count++
            }

            currentColumn = count
        }

        lastAbsoluteCharPosition = value.selection.start
    }

    //TODO: for extra options in a selection, see TextToolbar and \
    //TODO (continue): https://stackoverflow.com/questions/68956792/floating-toolbar-for-text-selection-jetpack-compose

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                .horizontalScroll(rememberScrollState())
                .verticalScroll(rememberScrollState())
        ) {
            BasicTextField(
                state,
                onValueChange = {
                    state = it
                    handleSelectionChanged(it)
                },
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp, 10.dp, 0.dp),
                textStyle =
                LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                ),
                cursorBrush = Brush.linearGradient(
                    colors = List(2) {
                        MaterialTheme.colorScheme.secondary
                        MaterialTheme.colorScheme.secondary
                    })
            )
        }


        Row(modifier = Modifier.background(MaterialTheme.colorScheme.secondary)) {
            Text(
                "Ln: $currentLine, Col: $currentColumn",
                color = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "UTF-8",
                color = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "LF",
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}