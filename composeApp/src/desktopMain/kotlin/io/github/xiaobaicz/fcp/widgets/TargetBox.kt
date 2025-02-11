package io.github.xiaobaicz.fcp.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferAction
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import io.github.xiaobaicz.fcp.Util
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
@Preview
fun TargetBox(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    var dir by remember { mutableStateOf<File?>(null) }

    val dndTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val toDir = dir ?: return false
                val data = event.dragData()
                if (data is DragData.Text) {
                    val text = data.readText()
                    if (text.isEmpty()) return false
                    val src = File(text)
                    val util = Util(src, toDir)
                    scope.launch { util.copy() }
                    return true
                }
                return false
            }
        }
    }

    FileListBox(modifier.dragAndDropTarget({
        it.action == DragAndDropTransferAction.Move
    }, dndTarget), {
        dir = it
    }) { file ->
        FileBox(file, modifier = Modifier.combinedClickable(null, null, onDoubleClick = {
            scope.launch {
                open(file)
            }
        }) {})
    }
}
