package com.example.simpletexteditor

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import com.example.simpletexteditor.cloudmanager.FileManagement
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.pages.CloudPage
import com.example.simpletexteditor.ui.pages.ForgotPassword
import com.example.simpletexteditor.ui.pages.LoginPage
import com.example.simpletexteditor.ui.pages.MainPage
import com.example.simpletexteditor.ui.pages.RegisterPage
import com.example.simpletexteditor.ui.partials.BottomBar
import com.example.simpletexteditor.ui.theme.AppTheme
import com.example.simpletexteditor.utils.slideComposable
import java.lang.ref.WeakReference

class MainActivity : ComponentActivity() {
    companion object {
        private var _contextRef: WeakReference<Context>? = null
        private var _activityRef: WeakReference<Activity>? = null

        fun getContext(): Context? {
            return _contextRef?.get()
        }

        fun getActivity(): Activity? {
            return _activityRef?.get()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _contextRef = WeakReference(applicationContext)
        _activityRef = WeakReference(this)

        FileManagement.refreshAuthToken()
//        val success = FileHandler.loadFromStorage()
//        if (!success) {
//            Toast.makeText(this, this.resources.getText(R.string.local_load_failed), Toast.LENGTH_LONG).show()
//        }

        setContent {
            AppTheme {
                MainContent()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _contextRef?.clear()
        _activityRef?.clear()
    }
}

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
                slideComposable("/cloud") { CloudPage(globalState) }
                slideComposable("/login") { LoginPage(globalState) }
                slideComposable("/register") { RegisterPage(globalState) }
                slideComposable("/forgotPassword") { ForgotPassword(globalState) }
            }
        }
    }
}

fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity)
            return context

        context = context.baseContext
    }

    return null
}


//@Preview(showBackground = true)
//@Composable
//fun ActivityPreview() {
//    AppTheme {
//        MainContent()
//    }
//}