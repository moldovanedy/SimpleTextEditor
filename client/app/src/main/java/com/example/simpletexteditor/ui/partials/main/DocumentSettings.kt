package com.example.simpletexteditor.ui.partials.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.simpletexteditor.R
import com.example.simpletexteditor.ui.partials.settings.DropDownButton
import com.example.simpletexteditor.ui.partials.settings.SwitchSettingButton
import com.example.simpletexteditor.ui.theme.AppTheme

@Composable
fun DocumentSettings(onDismissRequested: () -> Unit) {
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
                .padding(0.dp, 10.dp)
        ) {
            Text(
                text = "Document settings",
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.headlineMedium
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                HorizontalDivider()

                DropDownButton(
                    name = stringResource(R.string.doc_setting_eol),
                    description = stringResource(R.string.doc_setting_eol_desc),
                    values = stringArrayResource(R.array.doc_setting_eol_values),
                    onValueChanged = {}) { }
                HorizontalDivider()

                SwitchSettingButton(
                    name = stringResource(R.string.doc_setting_use_bom),
                    description = stringResource(R.string.doc_setting_use_bom_desc),
                    switchState = false,
                    onSwitchStateChanged = {}
                )
                HorizontalDivider()

                DropDownButton(
                    name = stringResource(R.string.doc_setting_encoding),
                    description = stringResource(R.string.doc_setting_encoding_desc),
                    values = stringArrayResource(R.array.doc_setting_encoding_values),
                    onValueChanged = {}) { }
                HorizontalDivider()
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .height(32.dp)
                    .padding(10.dp, 0.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onDismissRequested.invoke() }) {
                    Text(stringResource(R.string.cancel).uppercase())
                }
                Spacer(modifier = Modifier.width(10.dp))

                TextButton(onClick = { onDismissRequested.invoke() }) {
                    Text(stringResource(R.string.apply).uppercase())
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityPreview() {
    AppTheme {
        DocumentSettings({})
    }
}