package com.example.simpletexteditor.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Suppress("UnusedReceiverParameter")
val ColorScheme.linkColor: Color
    @Composable
    get() =
        if (isSystemInDarkTheme())
            Color(0xff_58_a6_ff)
        else
            Color(0xff_00_00_ee)