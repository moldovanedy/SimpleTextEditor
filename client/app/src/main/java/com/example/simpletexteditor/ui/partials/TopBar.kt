package com.example.simpletexteditor.ui.partials

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.simpletexteditor.R

@Composable
fun TopBar() {
    val showDropDown = remember{ mutableStateOf(false) }

    androidx.compose.material.TopAppBar(
        modifier = Modifier.safeDrawingPadding(),
        backgroundColor = MaterialTheme.colorScheme.primary,
        title = {
            Text(
                "File: aa un fdusn isudnui nrtn ntinhbn hiun",
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                color = MaterialTheme.colorScheme.onPrimary)
        },
        actions = {
            IconButton(onClick = {showDropDown.value = !showDropDown.value}) {
                Icon(
                    if (showDropDown.value) {
                        Icons.Filled.ArrowDropUp
                    }
                    else {
                        Icons.Filled.ArrowDropDown
                    },
                    stringResource(R.string.show_open_files_acc),
                    tint = MaterialTheme.colorScheme.onPrimary)
            }
            DropdownMenu(
                expanded = showDropDown.value,
                onDismissRequest = {showDropDown.value = false}) {
                DropdownMenuItem(
                    text = { Text("AAA") },
                    onClick = {showDropDown.value = false})
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.AutoMirrored.Outlined.Undo,
                    stringResource(R.string.undo_acc),
                    tint = MaterialTheme.colorScheme.onPrimary,)
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.AutoMirrored.Outlined.Redo,
                    stringResource(R.string.redo_acc),
                    tint = MaterialTheme.colorScheme.onPrimary)
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Outlined.MoreVert,
                    stringResource(R.string.more_actions_acc),
                    tint = MaterialTheme.colorScheme.onPrimary)
            }
        })
}