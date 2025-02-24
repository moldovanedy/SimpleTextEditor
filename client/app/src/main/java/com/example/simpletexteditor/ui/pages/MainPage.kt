package com.example.simpletexteditor.ui.pages

import androidx.compose.runtime.Composable
import com.example.simpletexteditor.textLines
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.partials.main.TextEditor

@Composable
fun MainPage(globalState: GlobalState) {
    val sb: StringBuilder = StringBuilder()
    textLines.map { sb.append(it); sb.append('\n') }
    TextEditor(globalState = globalState, text = sb.toString())
}