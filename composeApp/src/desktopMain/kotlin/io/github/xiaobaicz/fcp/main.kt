package io.github.xiaobaicz.fcp

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val context = rememberSaveable { WindowContext("选择复制目标") }
    WindowContextProvider(context) {
        val state = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(480.dp, 720.dp)
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = title,
            state = state,
            resizable = resizable,
            enabled = enabled,
            visible = visible,
            alwaysOnTop = alwaysOnTop,
            focusable = focusable,
        ) {
            App()
        }
    }
}