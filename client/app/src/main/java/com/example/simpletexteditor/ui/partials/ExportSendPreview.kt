package com.example.simpletexteditor.ui.partials

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider.getUriForFile
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.R
import com.example.simpletexteditor.textmanager.FileHandler
import java.io.File


@Composable
fun ExportSendPreview(onDismissRequested: () -> Unit) {
    var fileName by remember {
        mutableStateOf(
            if (FileHandler.getActiveFileData()?.name?.indexOf('.') != -1) {
                FileHandler.getActiveFileData()?.name ?: "New File.txt"
            } else {
                FileHandler.getActiveFileData()?.name?.plus(".txt") ?: "New File.txt"
            }
        )
    }

    fun exportFile() {
        val activity = MainActivity.getActivity() ?: return

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        startActivityForResult(activity, intent, 1, null)
    }

    fun shareFile() {
        val context = MainActivity.getContext() ?: return

        try {
            val filesPath = File(context.filesDir, "/")
            val originalFile = File(filesPath, FileHandler.getActiveFileData()?.id.toString())

            //create a new, cached file with the friendly name
            val cachePath = File(context.cacheDir, "/")
            val friendlyFile = File(cachePath, fileName)
            //copy the contents
            originalFile.copyTo(friendlyFile, true)

            val contentUri: Uri = getUriForFile(context, "com.example.simpletexteditor.fileprovider", friendlyFile)

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "text/plain"
            }

            MainActivity.getActivity()?.startActivity(Intent.createChooser(shareIntent, null))
        } catch (e: Exception) {
            Log.e("DBG", e.toString())
            Toast
                .makeText(
                    context,
                    MainActivity.getActivity()?.getString(R.string.share_failed),
                    Toast.LENGTH_LONG
                )
                .show()
        }
    }

    Dialog(
        onDismissRequest = { onDismissRequested.invoke() },
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
                text = stringResource(R.string.share_or_export_file),
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
                    text = stringResource(R.string.export_explanation),
                    softWrap = true,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fileName,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { fileName = it },
                    label = {
                        Text(
                            text = stringResource(R.string.file_name),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Spacer(modifier = Modifier.height(15.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { exportFile() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .size(58.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(5.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = stringResource(R.string.save_on_device),
                            softWrap = true,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.widthIn(max = 90.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { shareFile() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .size(58.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(5.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = stringResource(R.string.share),
                            softWrap = true,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.widthIn(max = 90.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = { onDismissRequested.invoke() }) {
                        Text(
                            stringResource(R.string.cancel).uppercase(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}