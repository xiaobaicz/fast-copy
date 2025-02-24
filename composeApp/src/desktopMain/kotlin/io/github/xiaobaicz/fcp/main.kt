package io.github.xiaobaicz.fcp

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

fun main() = application {
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(480.dp, 720.dp)
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "快速复制",
        state = windowState,
    ) {
        App()
    }
}