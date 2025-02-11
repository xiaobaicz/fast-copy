package io.github.xiaobaicz.fcp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.xiaobaicz.fcp.widgets.SourceBox
import io.github.xiaobaicz.fcp.widgets.TargetBox
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Box(Modifier.weight(1f)) {
                SourceBox()
            }
            Box(Modifier.weight(1f)) {
                TargetBox()
            }
        }
    }
    CopyWindow()
}