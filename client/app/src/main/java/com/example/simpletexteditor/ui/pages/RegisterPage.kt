package com.example.simpletexteditor.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.example.simpletexteditor.R
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.utils.linkColor

@Composable
fun RegisterPage(globalState: GlobalState) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp, 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.register), style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        label = { Text(stringResource(R.string.email)) })
                    Spacer(modifier = Modifier.height(10.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        label = { Text(stringResource(R.string.password)) },
                        trailingIcon = {
                            val image = if (isPasswordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description =
                                if (isPasswordVisible)
                                    stringResource(R.string.acc_hide_password)
                                else
                                    stringResource(R.string.acc_show_password)

                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(imageVector = image, description)
                            }
                        })
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.align(Alignment.Start)) {
                        Text(
                            buildAnnotatedString {
                                append(stringResource(R.string.account_existing_statement) + " ")
                                withLink(
                                    LinkAnnotation.Clickable(
                                        tag = "login",
                                        linkInteractionListener = { globalState.navController.popBackStack() },
                                        styles = TextLinkStyles(SpanStyle(MaterialTheme.colorScheme.linkColor))
                                    )
                                )
                                {
                                    append(stringResource(R.string.login).lowercase())
                                }
                            },
                            softWrap = true
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    Button(modifier = Modifier.align(Alignment.End), onClick = {}) {
                        Text(stringResource(R.string.register))
                    }
                }
            }
        }
    }
}