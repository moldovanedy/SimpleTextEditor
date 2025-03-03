package com.example.simpletexteditor.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.simpletexteditor.R
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.settings.SettingsTopBar
import com.example.simpletexteditor.ui.partials.settings.SwitchSettingButton

@Composable
fun SettingsPage(globalState: GlobalState) {
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

                HorizontalDivider()
                SwitchSettingButton(
                    name = stringResource(R.string.always_save_on_cloud),
                    description = stringResource(R.string.always_save_on_cloud_desc),
                    switchState = alwaysSaveOnCloud,
                    onSwitchStateChanged = { value -> alwaysSaveOnCloud = value }
                )
            }
        }
    }
}
