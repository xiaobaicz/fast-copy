package io.github.xiaobaicz.fcp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().padding(8.dp)) {
            val fmc = remember {
                FileManagerComponent {}
            }
            fmc.Content(modifier = Modifier)
        }
    }
}
