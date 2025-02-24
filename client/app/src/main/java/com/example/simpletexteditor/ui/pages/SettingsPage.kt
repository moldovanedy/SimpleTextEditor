package com.example.simpletexteditor.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import com.example.simpletexteditor.R
import com.example.simpletexteditor.settings.DefaultSettings
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.settings.SettingsTopBar

@Composable
fun SettingsPage(globalState: GlobalState) {
    val kbdController = LocalSoftwareKeyboardController.current

    var undoRedoMemoryLimit by remember { mutableFloatStateOf(50f) }
    var alwaysSaveOnCloud by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SettingsTopBar(globalState) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()))
            {
                Text(
                    text = stringResource(R.string.general),
                    modifier = Modifier.padding(15.dp, 0.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(24.dp))

                CustomSettingButton(
                    name = stringResource(R.string.undo_memory_limit),
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = undoRedoMemoryLimit,
                                onValueChange = {
                                    //coerceIn is clamp
                                    undoRedoMemoryLimit =
                                        it
                                            .fastRoundToInt()
                                            .coerceIn(
                                                DefaultSettings.UNDO_MEMORY_MIN_SIZE..DefaultSettings.UNDO_MEMORY_MAX_SIZE
                                            )
                                            .toFloat()
                                },
                                valueRange =
                                DefaultSettings.UNDO_MEMORY_MIN_SIZE.toFloat()..
                                        DefaultSettings.UNDO_MEMORY_MAX_SIZE.toFloat(),
                                //20 dp to avoid the back button swipe area
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(20.dp, 0.dp, 0.dp, 0.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                )
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            TextField(
                                undoRedoMemoryLimit.toInt().toString(),
                                onValueChange = {
                                    if (it.isEmpty()) {
                                        undoRedoMemoryLimit = 0f
                                        return@TextField
                                    }

                                    undoRedoMemoryLimit = try {
                                        it.toInt().toFloat()
                                    } catch (e: Exception) {
                                        DefaultSettings.UNDO_MEMORY_DEFAULT_SIZE.toFloat()
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done,
                                    autoCorrectEnabled = false
                                ),
                                singleLine = true,
                                keyboardActions = KeyboardActions(onDone = {
                                    undoRedoMemoryLimit =
                                        undoRedoMemoryLimit
                                            .coerceIn(
                                                DefaultSettings.UNDO_MEMORY_MIN_SIZE.toFloat(),
                                                DefaultSettings.UNDO_MEMORY_MAX_SIZE.toFloat()
                                            )

                                    kbdController?.hide()
                                }),
                                modifier = Modifier.fillMaxWidth(0.2f)
                            )
                        }
                    },
                    shortDescription = stringResource(R.string.undo_memory_limit_short_desc)
                )

                HorizontalDivider()

                SwitchSettingButton(
                    name = stringResource(R.string.always_save_on_cloud),
                    shortDescription = stringResource(R.string.always_save_on_cloud_short_desc),
                    switchState = alwaysSaveOnCloud,
                    onSwitchStateChanged = { value -> alwaysSaveOnCloud = value }
                )
            }
        }
    }
}

@Composable
fun CustomSettingButton(
    name: String,
    shortDescription: String = "",
    longDescription: String = "",
    content: @Composable () -> Unit
) {
    Column(
        modifier =
        Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(15.dp, 10.dp)
    )
    {
        Text(name, fontWeight = FontWeight.Bold)
        content()
        Text(shortDescription, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SwitchSettingButton(
    name: String,
    shortDescription: String = "",
    longDescription: String = "",
    switchState: Boolean,
    onSwitchStateChanged: (Boolean) -> Unit
) {
    Row(
        modifier =
        Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(15.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold)
            Text(shortDescription, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }

        Switch(
            switchState,
            onCheckedChange = { value -> onSwitchStateChanged.invoke(value) }
        )
    }
}