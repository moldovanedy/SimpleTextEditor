package com.example.simpletexteditor.ui.pages

import androidx.compose.runtime.Composable
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.main.TextEditor

@Composable
fun MainPage(globalState: GlobalState) {
    TextEditor(globalState = globalState)
}