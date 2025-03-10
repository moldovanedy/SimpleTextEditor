package com.example.simpletexteditor.ui.partials.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simpletexteditor.R
import com.example.simpletexteditor.ui.theme.AppTheme

@Composable
fun CustomSettingButton(
    name: String,
    description: String = "",
    content: @Composable () -> Unit
) {
    var showDescriptionDialog by remember { mutableStateOf(false) }

    Column(
        modifier =
        Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(15.dp, 10.dp)
            .clickable(onClick = { showDescriptionDialog = true })
    )
    {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
        Text(
            text = description,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    if (showDescriptionDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = { Text(name, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(description, color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                TextButton(onClick = { showDescriptionDialog = false }) {
                    Text("OK", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            onDismissRequest = { showDescriptionDialog = false },
        )
    }
}

@Composable
fun SwitchSettingButton(
    name: String,
    description: String = "",
    switchState: Boolean,
    onSwitchStateChanged: (Boolean) -> Unit
) {
    var showDescriptionDialog by remember { mutableStateOf(false) }

    Row(
        modifier =
        Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(15.dp, 10.dp)
            .clickable(onClick = { showDescriptionDialog = true }),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Switch(
            switchState,
            onCheckedChange = { value -> onSwitchStateChanged.invoke(value) }
        )
    }

    if (showDescriptionDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = { Text(text = name, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(description, color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                TextButton(onClick = { showDescriptionDialog = false }) {
                    Text("OK", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            onDismissRequest = { showDescriptionDialog = false },
        )
    }
}

@Composable
fun TextFieldButton(
    name: String,
    description: String = "",
    text: String,
    onTextChanged: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var showDescriptionDialog by remember { mutableStateOf(false) }

    Column(
        modifier =
        Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(15.dp, 10.dp)
            .clickable(onClick = { showDescriptionDialog = true })
    )
    {
        TextField(
            text,
            onValueChange =
            onTextChanged,
            label = { Text(name, color = MaterialTheme.colorScheme.onSurface) }
        )
        content()
        Text(
            text = description,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    if (showDescriptionDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = { Text(name, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(description, color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                TextButton(onClick = { showDescriptionDialog = false }) {
                    Text("OK", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            onDismissRequest = { showDescriptionDialog = false },
        )
    }
}

@Composable
fun DropDownButton(
    name: String,
    description: String = "",
    values: Array<String>,
    indexOfSelected: Int = 0,
    onValueChanged: (Int) -> Unit,
    content: @Composable () -> Unit
) {
    var showDescriptionDialog by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableIntStateOf(indexOfSelected) }

    Column(
        modifier =
        Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(15.dp, 10.dp)
            .clickable(onClick = { showDescriptionDialog = true })
    )
    {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(25.dp))

            Button(
                onClick = { expanded = true },
                modifier = Modifier.weight(1f),
                colors = ButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (selected >= values.size || selected < 0)
                            "?"
                        else
                            values[selected],
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        modifier = Modifier.size(24.dp),
                        contentDescription =
                        if (expanded)
                            stringResource(R.string.acc_close_dropdown)
                        else
                            stringResource(R.string.acc_open_dropdown)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    modifier = Modifier.fillMaxWidth(),
                    onDismissRequest = { expanded = false }
                ) {
                    values.forEachIndexed { i, el ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Text(el, color = MaterialTheme.colorScheme.onSurface)

                                    if (i == indexOfSelected) {
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(imageVector = Icons.Filled.Check, contentDescription = "")
                                    }
                                }
                            },
                            onClick = { selected = i; onValueChanged.invoke(i); expanded = false }
                        )
                    }
                }
            }

        }

        content()
        Text(
            text = description,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    if (showDescriptionDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = { Text(name, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(description, color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                TextButton(onClick = { showDescriptionDialog = false }) {
                    Text("OK", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            onDismissRequest = { showDescriptionDialog = false },
        )
    }
}

@Preview
@Composable
fun Preview() {
    AppTheme {

    }
}