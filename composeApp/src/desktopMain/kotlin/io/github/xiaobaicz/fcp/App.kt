package io.github.xiaobaicz.fcp

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File

@Composable
@Preview
fun App() {
    MaterialTheme {
        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
//                val src = "/Users/lbc/.gradle/caches"
//                val dir = "/Users/lbc/gg"
//
//                Utils.copy(File(src), File(dir)) { c, s, sp ->
//                    val mb = s / 1024.0 / 1024
//                    val sp = sp / 1024.0 / 1024
//                    println("Count: $c \nSum: $mb MB \nSpeed: $sp MB/s\n")
//                }
            }
        }
    }
}