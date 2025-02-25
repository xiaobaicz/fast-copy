package io.github.xiaobaicz.fcp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue

class WindowContext(
    val title: String,
    val resizable: Boolean = false,
    enabled: Boolean = true,
    visible: Boolean = true,
    alwaysOnTop: Boolean = false,
    focusable: Boolean = true,
) {
    var enabled by mutableStateOf(enabled)
    var visible by mutableStateOf(visible)
    var alwaysOnTop by mutableStateOf(alwaysOnTop)
    var focusable by mutableStateOf(focusable)
}

private val LocalWindowContext = compositionLocalOf<WindowContext> {
    throw NullPointerException()
}

val localWindowContext @Composable get() = LocalWindowContext.current

@Composable
fun WindowContextProvider(windowContext: WindowContext, content: @Composable WindowContext.() -> Unit) {
    CompositionLocalProvider(LocalWindowContext provides windowContext) {
        val context = localWindowContext
        rememberSaveableStateHolder().SaveableStateProvider(context) {
            content(context)
        }
    }
}