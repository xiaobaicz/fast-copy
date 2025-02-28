package io.github.xiaobaicz.fcp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import java.io.File

private var target by mutableStateOf<File?>(null)

private val copyList = mutableStateListOf<CopyContext>()

@Composable
fun App() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().padding(8.dp)) {
            val fmc = remember {
                FileManagerComponent {
                    target = it
                }
            }
            fmc.Content()
        }
        localWindowContext.enabled = target == null
        CopyTo()
        TaskList()
    }
}

@Composable
fun CopyTo() {
    val src = target ?: return
    val context = rememberSaveable { WindowContext("${src.name} 复制到 ？", alwaysOnTop = true) }
    WindowContextProvider(context) {
        val state = rememberWindowState(size = DpSize(480.dp, 720.dp))
        Window(
            onCloseRequest = { target = null },
            title = title,
            state = state,
            resizable = resizable,
            enabled = enabled,
            visible = visible,
            alwaysOnTop = alwaysOnTop,
            focusable = focusable,
        ) {
            Box(Modifier.fillMaxSize().padding(8.dp)) {
                val fmc = remember {
                    FileManagerComponent {
                        if (!it.isDirectory) return@FileManagerComponent
                        val copyContext = CopyContext(src, it)
                        if (copyList.contains(copyContext)) return@FileManagerComponent
                        copyList.add(copyContext)
                        target = null
                    }.apply {
                        isOnlyDir = true
                        refresh()
                    }
                }
                fmc.Content()
            }
        }
    }
}

@Composable
fun TaskList() {
    if (copyList.isEmpty()) return
    val context = rememberSaveable { WindowContext("复制列表") }
    WindowContextProvider(context) {
        val state = rememberWindowState(size = DpSize(480.dp, 480.dp))
        Window(
            onCloseRequest = {
                for (cc in copyList) cc.close()
                copyList.clear()
            },
            title = title,
            state = state,
            resizable = resizable,
            enabled = enabled,
            visible = visible,
            alwaysOnTop = alwaysOnTop,
            focusable = focusable,
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(4.dp)) {
                items(
                    items = copyList,
                    key = { "${it.src.absolutePath}-${it.dest.absolutePath}@${it.hashCode()}" },
                ) {
                    Task(it)
                }
            }
        }
    }
}

@Composable
fun Task(context: CopyContext) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(4.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp)).background(Color.White)
            .padding(8.dp)
    ) {
        TaskText("目标: ${context.src.absolutePath}")
        TaskText("复制到: ${context.dest.absolutePath}")
        TaskText("文件数量: ${context.remCount} / ${context.totalCount}")
        TaskText("文件大小: ${context.remSizeFormat} / ${context.totalSizeFormat}")
        TaskText("速度: ${context.speedFormat}")
        TaskText("用时: ${context.timeFormat}")
        TaskText("取消", style = LocalTextStyle.current.copy(color = Color.Red), modifier = Modifier.clickable {
            context.close()
            copyList.remove(context)
        })
        LaunchedEffect(context) {
            context.run()
            copyList.remove(context)
        }
    }
}

@Composable
fun TaskText(text: String, style: TextStyle = LocalTextStyle.current, modifier: Modifier = Modifier) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, style = style, modifier = modifier)
}
