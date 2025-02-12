package com.example.simpletexteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpletexteditor.ui.partials.BottomBar
import com.example.simpletexteditor.ui.partials.TopBar
import com.example.simpletexteditor.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) {
        innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                Editor()
            }
    }
}

@Composable
fun Editor() {
    var displayedText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            displayedText,
            onValueChange = {displayedText = it},
            modifier = Modifier.fillMaxSize(),
            singleLine = false,
            minLines = 300,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            visualTransformation = VisualTransformation.None
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityPreview() {
    AppTheme {
        MainContent()
    }
}