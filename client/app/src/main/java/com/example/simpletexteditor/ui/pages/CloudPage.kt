package com.example.simpletexteditor.ui.pages

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpletexteditor.MainContent
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.cloud.Login
import com.example.simpletexteditor.ui.theme.AppTheme

@Composable
fun CloudPage(globalState: GlobalState) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            Login()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityPreview() {
    AppTheme {
        MainContent()
    }
}