package com.example.simpletexteditor.ui.partials

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.simpletexteditor.R
import com.example.simpletexteditor.getActivityOrNull

@Composable
fun AppSettingsDialog(onDismissRequested: () -> Unit) {
    val activity = LocalContext.current.getActivityOrNull()
    var editorTextSizeMultiplier by remember {
        mutableFloatStateOf(
            activity?.getPreferences(Context.MODE_PRIVATE)?.getFloat("EDITOR_TEXT_SIZE", 1f) ?: 1f
        )
    }

    fun saveChanges() {
        val prefs = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        with(prefs.edit()) {
            putFloat("EDITOR_TEXT_SIZE", editorTextSizeMultiplier)
            apply()
        }
    }

    Dialog(
        onDismissRequest = onDismissRequested,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = true
        )
    ) {
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(10.dp, 10.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
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
                Text(
                    text = stringResource(R.string.editor_text_size),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("A", color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = editorTextSizeMultiplier,
                        onValueChange = { editorTextSizeMultiplier = it; saveChanges() },
                        valueRange = 0.5f..3f,
                        steps = 11,
                        modifier = Modifier.weight(1f)
                    )
                    Text("A", fontSize = 36.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.align(Alignment.End)) {
                TextButton(onClick = { saveChanges(); onDismissRequested.invoke() }) {
                    Text(
                        stringResource(R.string.ok).uppercase(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}