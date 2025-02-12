package com.example.simpletexteditor.ui.partials

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.simpletexteditor.R

@Composable
fun BottomBar(){
    val pageIndex = remember { mutableIntStateOf(0) }

    NavigationBar {
        NavigationBarItem(
            selected = pageIndex.intValue == 0,
            modifier = Modifier.clickable(
                onClick = {},
                onClickLabel = stringResource(R.string.edit_acc)),
            onClick = {pageIndex.intValue = 0},
            icon = { Icon(Icons.Filled.Edit, null) },
            label = { Text(stringResource(R.string.edit)) })
        NavigationBarItem(
            selected = pageIndex.intValue == 1,
            modifier = Modifier.clickable(
                onClick = {},
                onClickLabel = stringResource(R.string.cloud_acc)),
            onClick = {pageIndex.intValue = 1},
            icon = { Icon(Icons.Filled.Cloud, null) },
            label = { Text(stringResource(R.string.cloud)) })
    }
}