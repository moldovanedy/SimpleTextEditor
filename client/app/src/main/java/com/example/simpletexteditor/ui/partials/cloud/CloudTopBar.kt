package com.example.simpletexteditor.ui.partials.cloud

import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.R
import com.example.simpletexteditor.cloudmanager.IdentityManagement
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudTopBar(onLogout: () -> Unit) {
    var isPopupActive by remember { mutableStateOf(false) }

    key(isPopupActive) {
        TopAppBar(
            title = { Text(stringResource(R.string.cloud), fontSize = 28.sp) },
            colors = TopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                IconButton(onClick = { isPopupActive = true }) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )

                    AccountPopupMenu(
                        isExpanded = isPopupActive,
                        onDismissRequested = { isPopupActive = false },
                        onLogout = onLogout
                    )
                }
            }
        )
    }
}

@Composable
private fun AccountPopupMenu(isExpanded: Boolean, onDismissRequested: () -> Unit, onLogout: () -> Unit) {
    val taskScope = rememberCoroutineScope()

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequested
    ) {
        DropdownMenuItem(
            text = { Text("email@example.com", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = {}
        )
        HorizontalDivider()

        DropdownMenuItem(
            text = { Text(stringResource(R.string.logout)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = {
                taskScope.launch {
                    val error: String? = IdentityManagement.logout()
                    if (error != null) {
                        Toast.makeText(MainActivity.getContext(), error, Toast.LENGTH_LONG).show()
                    }

                    onDismissRequested.invoke()
                    onLogout.invoke()
                }
            }
        )
    }
}

@Preview
@Composable
private fun CloudTopBarPreview() {
    CloudTopBar(onLogout = {})
}