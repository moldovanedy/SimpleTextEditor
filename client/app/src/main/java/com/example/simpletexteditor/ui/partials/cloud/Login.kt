package com.example.simpletexteditor.ui.partials.cloud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.example.simpletexteditor.R
import com.example.simpletexteditor.utils.linkColor

@Composable
fun Login() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(20.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.login), style = MaterialTheme.typography.headlineMedium)
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
                label = { Text(stringResource(R.string.password)) })
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.align(Alignment.Start)) {
                Text(buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "forgotPassword",
                            linkInteractionListener = {},
                            styles = TextLinkStyles(SpanStyle(MaterialTheme.colorScheme.linkColor))
                        )
                    )
                    {
                        append(stringResource(R.string.forgot_password))
                    }
                })
            }
            Spacer(modifier = Modifier.height(15.dp))

            Row(modifier = Modifier.align(Alignment.Start)) {
                Text(
                    buildAnnotatedString {
                        append(stringResource(R.string.account_existence_statement) + " ")
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "register",
                                linkInteractionListener = {},
                                styles = TextLinkStyles(SpanStyle(MaterialTheme.colorScheme.linkColor))
                            )
                        )
                        {
                            append(stringResource(R.string.register).lowercase())
                        }
                    },
                    softWrap = true
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            Button(modifier = Modifier.align(Alignment.End), onClick = {}) {
                Text(stringResource(R.string.login))
            }
        }
    }
}