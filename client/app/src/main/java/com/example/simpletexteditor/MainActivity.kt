package com.example.simpletexteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.pages.CloudPage
import com.example.simpletexteditor.ui.pages.MainPage
import com.example.simpletexteditor.ui.pages.SettingsPage
import com.example.simpletexteditor.ui.partials.BottomBar
import com.example.simpletexteditor.ui.theme.AppTheme
import com.example.simpletexteditor.utils.slideComposable

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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainContent() {
    val navController = rememberNavController()

    val globalState = GlobalState(navController)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(globalState) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "/"
            ) {
                slideComposable("/") { MainPage(globalState) }
                slideComposable("/settings") { SettingsPage(globalState) }
                slideComposable("/cloud") { CloudPage(globalState) }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ActivityPreview() {
//    AppTheme {
//        MainContent()
//    }
//}