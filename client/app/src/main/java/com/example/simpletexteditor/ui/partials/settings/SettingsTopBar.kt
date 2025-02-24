package com.example.simpletexteditor.ui.partials.settings

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.simpletexteditor.R
import com.example.simpletexteditor.ui.GlobalState

@Composable
fun SettingsTopBar(globalState: GlobalState) {
    androidx.compose.material.TopAppBar(
        modifier = Modifier.safeDrawingPadding(),
        backgroundColor = MaterialTheme.colorScheme.primary,
        title = {
            Text(
                stringResource(R.string.settings),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = { globalState.navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Sharp.ArrowBack,
                    stringResource(R.string.acc_go_back),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}