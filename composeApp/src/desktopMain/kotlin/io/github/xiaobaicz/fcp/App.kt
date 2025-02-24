package io.github.xiaobaicz.fcp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.xiaobaicz.fcp.widgets.Title

@Composable
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Title("选择复制目标")
            val fmc = remember { FileManagerComponent() }
            fmc.Content(modifier = Modifier.padding(top = 8.dp))
        }
    }
}