package com.example.simpletexteditor.ui.pages

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.simpletexteditor.R
import com.example.simpletexteditor.cloudmanager.IdentityManagement
import com.example.simpletexteditor.cloudmanager.UserValidations
import com.example.simpletexteditor.cloudmanager.dtos.user.ForgotPasswordUserDto
import com.example.simpletexteditor.ui.GlobalState
import kotlinx.coroutines.launch

@Composable
fun ForgotPassword(globalState: GlobalState) {
    var isInEmailState by rememberSaveable { mutableStateOf(true) }
    var typedEmail by rememberSaveable { mutableStateOf("") }

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
                AnimatedVisibility(isInEmailState) {
                    EmailStage(
                        globalState,
                        onCanGoNext = { email ->
                            typedEmail = email
                            isInEmailState = false
                        }
                    )
                }

                AnimatedVisibility(!isInEmailState) {
                    PasswordResetStage(globalState, typedEmail)
                }
            }
        }
    }
}

@Composable
fun EmailStage(globalState: GlobalState, onCanGoNext: (String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .verticalScroll(rememberScrollState())
            .padding(20.dp, 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.widthIn(min = 250.dp, max = 300.dp)
        ) {
            IconButton(
                onClick = { globalState.navController.popBackStack() },
                modifier = Modifier.requiredSize(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Text(
                stringResource(R.string.password_reset),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.requiredSize(32.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            stringResource(R.string.email_password_reset),
            softWrap = true,
            modifier = Modifier.widthIn(max = 300.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            label = { Text(stringResource(R.string.email)) })
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            modifier = Modifier.align(Alignment.End),
            enabled = email.isNotEmpty(),
            onClick = { onCanGoNext.invoke(email) }
        ) {
            Text(stringResource(R.string.next))
        }
    }
}

@Composable
fun PasswordResetStage(globalState: GlobalState, typedEmail: String) {
    var newPassword by rememberSaveable { mutableStateOf("") }
    var retypeNewPassword by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordRetypeVisible by remember { mutableStateOf(false) }

    var isPasswordError by rememberSaveable { mutableStateOf(true) }
    var isPasswordNotMatchingError by rememberSaveable { mutableStateOf(true) }
    var passwordErrorMsgId by rememberSaveable { mutableIntStateOf(R.string.password_error_short) }

    val taskScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isRequestSent by remember { mutableStateOf(false) }

    fun validatePassword() {
        val id: Int? = UserValidations.validatePassword(newPassword)
        isPasswordError = id != null
        passwordErrorMsgId = id ?: 0
    }

    fun validatePasswordRetype() {
        isPasswordNotMatchingError = newPassword != retypeNewPassword
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .verticalScroll(rememberScrollState())
            .padding(20.dp, 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.password_reset), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = newPassword,
            onValueChange = { newPassword = it; validatePassword() },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = { Text(stringResource(R.string.new_password)) },
            supportingText = {
                if (isPasswordError) {
                    Text(
                        text = stringResource(passwordErrorMsgId),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                val image = if (isPasswordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description =
                    if (isPasswordVisible)
                        stringResource(R.string.acc_hide_password)
                    else
                        stringResource(R.string.acc_show_password)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = image, description)
                    }

                    if (isPasswordError) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = stringResource(passwordErrorMsgId),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            })
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = retypeNewPassword,
            onValueChange = { retypeNewPassword = it; validatePasswordRetype() },
            singleLine = true,
            visualTransformation = if (isPasswordRetypeVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = { Text(stringResource(R.string.retype_password)) },
            supportingText = {
                if (isPasswordNotMatchingError) {
                    Text(
                        text = stringResource(R.string.passwords_not_matching),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                val image = if (isPasswordRetypeVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description =
                    if (isPasswordVisible)
                        stringResource(R.string.acc_hide_password)
                    else
                        stringResource(R.string.acc_show_password)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isPasswordRetypeVisible = !isPasswordRetypeVisible }) {
                        Icon(imageVector = image, description)
                    }

                    if (isPasswordError) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = stringResource(passwordErrorMsgId),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            })
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            modifier = Modifier.align(Alignment.End),
            enabled = !isRequestSent && !isPasswordError && !isPasswordNotMatchingError,
            onClick = {
                if (isRequestSent) {
                    return@Button
                }

                isRequestSent = true

                taskScope.launch {
                    val errorMessage: String? =
                        IdentityManagement.forgotPassword(ForgotPasswordUserDto(typedEmail, newPassword))
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        //TEMP
                        Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show()
                    }
                }

                isRequestSent = false
            }
        ) {
            Text(stringResource(R.string.reset_password))
        }
    }
}