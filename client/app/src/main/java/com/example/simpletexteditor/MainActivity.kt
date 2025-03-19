package com.example.simpletexteditor

import android.app.Activity
import android.app.ComponentCaller
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.example.simpletexteditor.cloudmanager.CloudFileManagement
import com.example.simpletexteditor.textmanager.FileHandler
import com.example.simpletexteditor.ui.GlobalState
import com.example.simpletexteditor.ui.pages.CloudPage
import com.example.simpletexteditor.ui.pages.ForgotPassword
import com.example.simpletexteditor.ui.pages.LoginPage
import com.example.simpletexteditor.ui.pages.MainPage
import com.example.simpletexteditor.ui.pages.RegisterPage
import com.example.simpletexteditor.ui.partials.BottomBar
import com.example.simpletexteditor.ui.theme.AppTheme
import com.example.simpletexteditor.utils.Event
import com.example.simpletexteditor.utils.slideComposable
import kotlinx.coroutines.runBlocking
import java.io.OutputStream
import java.lang.ref.WeakReference

class MainActivity : ComponentActivity() {
    companion object {
        val appTerminatingEvent = Event<Unit>()

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

        CloudFileManagement.refreshAuthToken()
        val success = FileHandler.loadFromStorage()
        if (!success) {
            Toast.makeText(this, this.resources.getText(R.string.local_load_failed), Toast.LENGTH_LONG).show()
        }

        setContent {
            AppTheme {
                MainContent()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        appTerminatingEvent.invoke(Unit)
        FileHandler.saveToStorage()
        runBlocking {
            CloudFileManagement.fullyUpdateFile(
                FileHandler.getActiveFileData()?.id.toString(),
                FileHandler.getActiveFileData()?.memoryFile?.content.toString()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        appTerminatingEvent.invoke(Unit)
        FileHandler.saveToStorage()
        runBlocking {
            CloudFileManagement.fullyUpdateFile(
                FileHandler.getActiveFileData()?.id.toString(),
                FileHandler.getActiveFileData()?.memoryFile?.content.toString()
            )
        }

        _contextRef?.clear()
        _activityRef?.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, caller: ComponentCaller) {
        super.onActivityResult(requestCode, resultCode, data, caller)

        //for save file
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.also { uri ->
                var fs: OutputStream? = null

                try {
                    fs = getContext()?.contentResolver?.openOutputStream(uri)
                    if (fs == null) {
                        Toast
                            .makeText(
                                getContext(),
                                getActivity()?.getString(R.string.export_failed),
                                Toast.LENGTH_LONG
                            )
                            .show()
                        return@also
                    }

                    fs.write(
                        FileHandler.getActiveFileData()?.memoryFile?.content.toString().toByteArray(Charsets.UTF_8)
                    )
                } catch (e: Exception) {
                    Log.e("DBG", e.toString())
                    Toast
                        .makeText(
                            getContext(),
                            getActivity()?.getString(R.string.export_failed),
                            Toast.LENGTH_LONG
                        )
                        .show()
                } finally {
                    fs?.close()
                }
            }
        }
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