package io.github.xiaobaicz.fcp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState

val utilList = mutableStateListOf<Util>()

@Composable
fun CopyWindow() {
    val state = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(480.dp, 320.dp)
    )
    Window(
        onCloseRequest = {  },
        state = state,
        title = "Copy List",
        resizable = false,
        visible = utilList.isNotEmpty(),
        alwaysOnTop = utilList.isNotEmpty(),
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
            items(utilList) {
                UtilItem(it)
            }
        }
    }
}

@Composable
fun UtilItem(util: Util, modifier: Modifier = Modifier) {
    val mod = modifier.padding(bottom = 8.dp).fillMaxWidth()
        .shadow(8.dp, RoundedCornerShape(8.dp))
        .background(Color.White)
        .padding(8.dp)
    Column(modifier = mod) {
        Text(util.title, color = Color.Black, style = TextStyle(fontWeight = FontWeight.Bold))
        Text("remCount: ${util.remCount}")
        Text("remSum: ${util.remSum / 1024 / 1024} MB")
        Text("speed: ${util.speed / 1024 / 1024} MB/s")
        val time = util.time.let { t ->
            if (t < 60)
                "${t}s"
            else
                "${t / 60}m${t % 60}s"
        }
        Text("time: $time")
    }
}
