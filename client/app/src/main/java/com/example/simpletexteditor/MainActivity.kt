package com.example.simpletexteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpletexteditor.ui.partials.BottomBar
import com.example.simpletexteditor.ui.partials.TopBar
import com.example.simpletexteditor.ui.partials.main.TextEditor
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

val textLines: MutableList<String> = mutableListOf(
    "Row 1 Lorem ipsum dolor sit amet ndn afnis nasdf nsid sn",
    "Row 2 cv ndn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn",
    "Row 3 Lorem ipsum dolor sit am af e sfga s ff as ssdsfvm fdmmdfv dsfmndfsmdf fsn"
)

@Composable
fun MainContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
                .imePadding()
        ) {
            val sb: StringBuilder = StringBuilder()
            textLines.map { sb.append(it); sb.append('\n') }
            TextEditor(sb.toString())
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