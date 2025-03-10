package com.example.simpletexteditor.ui.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.cloudmanager.FileManagement
import com.example.simpletexteditor.cloudmanager.dtos.file.FileDetailsDto
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.cloud.CloudFileItem
import com.example.simpletexteditor.ui.partials.cloud.CloudTopBar
import com.example.simpletexteditor.utils.ANIM_DURATION
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CloudPage(globalState: GlobalState?) {
    val taskScope = rememberCoroutineScope()

    var isLoadingData by rememberSaveable { mutableStateOf(true) }
    var files by rememberSaveable { mutableStateOf<List<FileDetailsDto>?>(null) }

    LaunchedEffect(isLoadingData) {
        if (files != null) {
            Toast.makeText(MainActivity.getContext(), "Cached", Toast.LENGTH_LONG).show()
            isLoadingData = false
            return@LaunchedEffect
        }

        val authToken: String =
            MainActivity
                .getActivity()
                ?.getPreferences(Context.MODE_PRIVATE)
                ?.getString("AUTH_TOKEN", "")
                ?: ""

        if (authToken == "") {
            taskScope.launch {
                //wait for the transition to the cloud page to complete
                delay(ANIM_DURATION.toLong())
                globalState?.navController?.navigate("/login")
            }

            return@LaunchedEffect
        }

        taskScope.launch {
            val result: Pair<List<FileDetailsDto>?, String?> = FileManagement.getAllFilesDetails()
            if (result.first == null) {
                //Error
                Toast.makeText(MainActivity.getContext(), result.second, Toast.LENGTH_LONG).show()
                isLoadingData = false
                return@launch
            }

            files = result.first
            isLoadingData = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CloudTopBar(onLogout = { globalState?.navController?.navigate("/login") })

        if (isLoadingData) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                files?.map { fileData ->
                    CloudFileItem(data = fileData, onDataModified = { files = null; isLoadingData = true })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
fun CloudPagePreview() {
    CloudPage(null)
}