package com.example.simpletexteditor.ui.partials

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.simpletexteditor.R
import com.example.simpletexteditor.ui.GlobalState

@Composable
fun BottomBar(globalState: GlobalState) {
    val pageIndex = rememberSaveable { mutableIntStateOf(0) }
    var isSettingsRoute by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        globalState.navController.addOnDestinationChangedListener { _, newRoute, _ ->
            isSettingsRoute = newRoute.route == "/settings"
        }
    }

    AnimatedVisibility(
        visible = !isSettingsRoute,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        NavigationBar {
            NavigationBarItem(
                selected = pageIndex.intValue == 0,
                modifier = Modifier.clickable(
                    onClick = {},
                    onClickLabel = stringResource(R.string.acc_edit)
                ),
                onClick = {
                    if (pageIndex.intValue == 0) {
                        return@NavigationBarItem
                    }

                    pageIndex.intValue = 0
                    globalState.navController.navigate("/")
                },
                icon = { Icon(Icons.Filled.Edit, null) },
                label = { Text(stringResource(R.string.edit)) })
            NavigationBarItem(
                selected = pageIndex.intValue == 1,
                modifier = Modifier.clickable(
                    onClick = {},
                    onClickLabel = stringResource(R.string.acc_cloud)
                ),
                onClick = {
                    if (pageIndex.intValue == 1) {
                        return@NavigationBarItem
                    }

                    pageIndex.intValue = 1
                    globalState.navController.navigate("/cloud")
                },
                icon = { Icon(Icons.Filled.Cloud, null) },
                label = { Text(stringResource(R.string.cloud)) })
        }
    }
}
